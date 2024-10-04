package net.datasa.yomakase_web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class DeleteRecipeController {
    private final SavedRecipeService savedRecipeService;

    // indexNum을 기준으로 레시피를 삭제하는 컨트롤러 메서드
    @DeleteMapping("/deleteRecipe/{indexNum}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int indexNum, @RequestParam Integer memberNum) {
        try {
            // 레시피가 해당 사용자(memberNum)의 것인지 확인한 후 삭제
            boolean deleted = savedRecipeService.deleteRecipeByIndexNumAndMemberNum(indexNum, memberNum);

            if (deleted) {
                return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build(); // 권한이 없을 경우 403 Forbidden 반환
            }
        } catch (Exception e) {
            log.error("레시피 삭제 중 오류 발생: ", e); // 오류 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패 시 500 에러 반환
        }
    }
}
