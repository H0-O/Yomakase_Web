package net.datasa.yomakase_web.repository;


import net.datasa.yomakase_web.domain.entity.SavedRecipeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SavedRecipeRepository extends JpaRepository<SavedRecipeEntity, Integer> {
}
