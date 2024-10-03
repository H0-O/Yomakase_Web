package net.datasa.yomakase_web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
public class DeleteRecipeController {
    private final SavedRecipeService savedRecipeService;

    // indexNum을 기준으로 레시피를 삭제하는 컨트롤러 메서드
    @DeleteMapping("/deleteRecipe/{indexNum}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable int indexNum) {
        try {
            // indexNum을 기준으로 레시피 삭제
            savedRecipeService.deleteRecipeByIndexNum(indexNum);
            return ResponseEntity.ok().build(); // 성공 시 200 OK 반환
        } catch (Exception e) {
            log.error("레시피 삭제 중 오류 발생: ", e); // 오류 로그 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 실패 시 500 에러 반환
        }
    }
}
