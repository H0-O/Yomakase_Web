package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.MemberStock;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, MemberStock> {
    @Query("SELECT s.ingredientName FROM StockEntity s")
    List<String> findAllIngredientNames();

    @Query("SELECT s FROM StockEntity s ORDER BY s.useByDate ASC")
    List<StockEntity> findTop9ByOrderByUseByDateAsc(Pageable pageable);
}
