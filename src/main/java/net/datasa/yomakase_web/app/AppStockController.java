package net.datasa.yomakase_web.app;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController  // REST API 처리를 위한 컨트롤러
@Slf4j
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class AppStockController {
    private final StockService stockService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/save")
    public ResponseEntity<String> saveStockForApp(
            @RequestBody List<Map<String, String>> ingredients,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 로그로 토큰과 데이터 출력
            log.info("Received token: {}", token);
            log.info("Received ingredients: {}", ingredients);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // 'Bearer ' 이후의 실제 JWT 부분만 추출
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken); // JWT에서 사용자 식별자 추출
                log.info("Extracted memberName from token: {}", memberNumFromToken);

                // 데이터 저장 처리
                stockService.saveStock(ingredients, memberNumFromToken); // 앱 사용자 처리
                log.info("Stock saved successfully for memberNum: {}", memberNumFromToken);
                return new ResponseEntity<>("저장되었습니다!", HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("앱 저장 실패: {}", e.getMessage());
            return new ResponseEntity<>("저장에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
