package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import net.datasa.yomakase_web.domain.dto.SavedRecipeDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.SavedRecipeEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.SavedRecipeRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SavedRecipeService {

    private final SavedRecipeRepository savedRecipeRepository;
    private final MemberRepository memberRepository;

    public SavedRecipeDTO saveRecipe(SavedRecipeDTO savedRecipeDTO) {
        // MemberEntity 가져오기 (memberNum을 사용)
        MemberEntity member = memberRepository.findById(savedRecipeDTO.getMemberNum())
                .orElseThrow(() -> new IllegalArgumentException("Member not found with id: " + savedRecipeDTO.getMemberNum()));

        // SavedRecipeEntity 생성
        SavedRecipeEntity savedRecipeEntity = SavedRecipeEntity.builder()
                .member(member)
                .foodName(savedRecipeDTO.getFoodName())
                .recipeUrl(savedRecipeDTO.getRecipeUrl())
                .savedRecipe(savedRecipeDTO.getSavedRecipe())
                .build();

        // 엔티티 저장
        SavedRecipeEntity savedEntity = savedRecipeRepository.save(savedRecipeEntity);

        // 저장된 엔티티를 DTO로 변환하여 반환
        return SavedRecipeDTO.builder()
                .indexNum(savedEntity.getIndexNum())
                .memberNum(savedEntity.getMember().getMemberNum())
                .foodName(savedEntity.getFoodName())
                .recipeUrl(savedEntity.getRecipeUrl())
                .savedRecipe(savedEntity.getSavedRecipe())
                .build();
    }
}