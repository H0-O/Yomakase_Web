package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.service.SavedRecipeService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@Slf4j
public class SavedRecipeController {

    private final SavedRecipeService savedRecipeService;

    @PostMapping("/save")
    public ResponseEntity<String> saveRecipe(@RequestBody Map<String, String> recipeData) {
        // 현재 로그인한 사용자의 정보를 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
        Integer memberNum = userDetails.getMemberNum();

        // 레시피 저장 로직 호출
        savedRecipeService.saveRecipe(recipeData, memberNum);

        // 성공 메시지 반환
        return ResponseEntity.ok("레시피가 성공적으로 저장되었습니다.");
    }

}
