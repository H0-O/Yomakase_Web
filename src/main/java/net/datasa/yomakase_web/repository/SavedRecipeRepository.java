package net.datasa.yomakase_web.repository;


import net.datasa.yomakase_web.domain.entity.SavedRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipeEntity, Integer> {
    // memberNum을 기준으로 저장된 레시피 조회
    List<SavedRecipeEntity> findByMemberMemberNum(Integer memberNum);
}
