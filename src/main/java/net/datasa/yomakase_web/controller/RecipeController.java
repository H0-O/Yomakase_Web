package net.datasa.yomakase_web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.service.RecipeService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// @RestController를 사용하여 JSON 응답을 반환하도록 설정
@RestController
@RequestMapping("/recipe")
@AllArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:7777") // Spring Boot 서버가 http://localhost:7777에서 오는 요청을 허용하도록 설정
public class RecipeController {

    private final RecipeService recipeService;

    // 요리법 추천 버튼을 클릭했을 때 호출될 엔드포인트
    @PostMapping("/recommend")
    public Map<String, Object> recommendRecipe(@RequestBody Map<String, List<String>> requestData) {
        log.debug("추천 요청 데이터: {}", requestData); // 로그 추가

        List<String> allergies = requestData.get("allergies");
        List<String> ingredients = requestData.get("ingredients");

        log.debug("추천 요청 알레르기 정보: {}", allergies);
        log.debug("추천 요청 재료 목록: {}", ingredients);

        // RecipeService를 사용하여 요리법 추천 요청
        Map<String, Object> response = recipeService.getRecipeRecommendation(allergies, ingredients);
        log.debug("서비스 응답: {}", response);
        return response;
    }
}
