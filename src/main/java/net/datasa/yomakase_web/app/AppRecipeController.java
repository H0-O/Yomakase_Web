package net.datasa.yomakase_web.app;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  // REST API 처리를 위한 컨트롤러
@Slf4j
@RequestMapping("/api/recipe")
@RequiredArgsConstructor
public class AppRecipeController {

    private final SavedRecipeService savedRecipeService;
    private final JwtTokenProvider jwtTokenProvider;  // JWT를 통한 인증 처리

    // 특정 사용자의 저장된 레시피 데이터를 가져오는 GET 요청 처리
    @GetMapping("/myRecipe")
    public ResponseEntity<?> getRecipe(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // 'Bearer ' 이후의 실제 JWT 부분만 추출

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken); // JWT에서 사용자 식별자 추출

                // 저장된 레시피 데이터 가져오기
                List<SavedRecipeDTO> recipes = savedRecipeService.getSavedRecipes(memberNumFromToken);
                log.info("Fetched saved recipes for memberNum: {}", memberNumFromToken);
                log.debug("Fetched saved recipes: {}", recipes);

                // 데이터가 없을 경우
                if (recipes.isEmpty()) {
                    return new ResponseEntity<>("저장된 레시피 데이터가 없습니다.", HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(recipes, HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("저장된 레시피 데이터를 가져오는 데 실패했습니다: {}", e.getMessage());
            return new ResponseEntity<>("저장된 레시피 데이터를 가져오는 데 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/save")
    public ResponseEntity<String> saveRecipe(@RequestBody Map<String, String> recipeData,
                                             @RequestHeader(value = "Authorization") String token) {
        try {
            // JWT에서 사용자 식별자 추출
            String jwtToken = token.substring(7); // 'Bearer ' 제거
            Integer memberNum = jwtTokenProvider.getMemberNumFromToken(jwtToken);

            // 레시피 저장 로직 호출
            savedRecipeService.saveRecipe(recipeData, memberNum);
            log.info("Recipe saved for memberNum: {}", memberNum);

            // 성공 메시지 반환
            return ResponseEntity.ok("레시피가 성공적으로 저장되었습니다.");

        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("레시피 저장 중 오류 발생: {}", e.getMessage());
            return new ResponseEntity<>("레시피 저장 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 레시피 삭제 처리
    @DeleteMapping("/deleteRecipe/{indexNum}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int indexNum,
                                             @RequestHeader(value = "Authorization") String token) {
        try {
            // JWT에서 사용자 식별자 추출
            String jwtToken = token.substring(7); // 'Bearer ' 제거
            Integer memberNum = jwtTokenProvider.getMemberNumFromToken(jwtToken);

            // 레시피 삭제 로직 호출
            boolean deleted = savedRecipeService.deleteRecipeByIndexNumAndMemberNum(indexNum, memberNum);

            if (deleted) {
                return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한이 없을 경우 403 Forbidden 반환
            }

        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED); // JWT 오류 시 401 Unauthorized 반환
        } catch (Exception e) {
            log.error("레시피 삭제 중 오류 발생: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패 시 500 에러 반환
        }
    }
}
