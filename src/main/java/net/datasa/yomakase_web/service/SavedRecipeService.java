package net.datasa.yomakase_web.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.SavedRecipeEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.SavedRecipeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public void saveRecipe(Map<String, String> recipeData, Object identifier) {
        Integer memberNum = null;

        // identifier가 Integer인지 String인지 확인하고, 멤버 번호 조회
        if (identifier instanceof Integer) {
            // 앱에서 온 요청: memberNum이 Integer로 전달됨
            memberNum = (Integer) identifier;
        } else if (identifier instanceof String) {
            // 이메일(아이디)를 사용해 memberNum을 조회
            MemberEntity member = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("Member not found"));
            memberNum = member.getMemberNum();
        }

        // recipeData에서 레시피 정보 추출
        String foodName = recipeData.get("food_name");  // 음식 이름
        String recipe = recipeData.get("recipe");  // 레시피 내용
        String recipeUrl = recipeData.get("recipe_url");  // 레시피 URL

        log.info("Saving recipe: Food Name: {}, Recipe URL: {}", foodName, recipeUrl);

        // MemberEntity 조회
        MemberEntity member = memberRepository.findById(memberNum)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 레시피 엔티티 생성 후 저장
        SavedRecipeEntity savedRecipeEntity = SavedRecipeEntity.builder()
                .member(member)
                .foodName(foodName)
                .savedRecipe(recipe)
                .recipeUrl(recipeUrl)
                .build();

        // 레시피 저장
        savedRecipeRepository.save(savedRecipeEntity);
    }

    // 레시피 불러오기
    public List<SavedRecipeDTO> getSavedRecipes() {
        return savedRecipeRepository.findAll().stream()
                .map(recipe -> SavedRecipeDTO.builder()
                        .indexNum(recipe.getIndexNum())
                        .memberNum(recipe.getMember().getMemberNum())
                        .foodName(recipe.getFoodName())
                        .recipeUrl(recipe.getRecipeUrl())
                        .savedRecipe(recipe.getSavedRecipe())
                        .build())
                .collect(Collectors.toList());
    }

    // foodName을 기준으로 레시피 삭제
    public void deleteRecipeByIndexNum(int indexNum) {
        // indexNum에 해당하는 레시피가 존재하는지 확인
        if (savedRecipeRepository.existsById(indexNum)) {
            savedRecipeRepository.deleteById(indexNum); // 레시피 삭제
        } else {
            throw new IllegalArgumentException("해당 레시피를 찾을 수 없습니다. indexNum: " + indexNum);
        }
    }
}