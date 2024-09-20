package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.MemberStock;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, MemberStock> {

    // memberNum과 isHaving이 true인 ingredient_name만 조회
    @Query("SELECT s.ingredientName FROM StockEntity s WHERE s.memberNum = :memberNum AND s.isHaving = true")
    List<String> findAllIngredientNamesByMemberNumAndIsHavingTrue(@Param("memberNum") Integer memberNum);
    // memberNum과 isHaving이 true인 StockEntity 리스트 조회
    List<StockEntity> findByMemberNumAndIsHavingTrue(Integer memberNum);

    @Query("SELECT s FROM StockEntity s ORDER BY s.useByDate ASC")
    List<StockEntity> findTop9ByOrderByUseByDateAsc(Pageable pageable);

    Optional<StockEntity> findByIngredientNameAndMemberNum(String ingredientName, Integer memberNum);

    List<StockEntity> findByMemberNum(int memberNum);
}
