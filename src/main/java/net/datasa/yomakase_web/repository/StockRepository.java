package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.MemberStock;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<StockEntity, MemberStock> {

    // memberNum과 isHaving이 true인 ingredient_name만 조회
    @Query("SELECT s.ingredientName FROM StockEntity s WHERE s.memberNum = :memberNum AND s.isHaving = true")
    List<String> findAllIngredientNamesByMemberNumAndIsHavingTrue(@Param("memberNum") Integer memberNum);

    // memberNum과 isHaving이 true인 StockEntity 리스트를 useByDate 기준으로 오름차순 정렬하여 조회
    List<StockEntity> findByMemberNumAndIsHavingTrueOrderByUseByDateAsc(Integer memberNum);

    // memberNum을 기준으로 상위 9개, isHaving 값이 true인 경우, 오름차순으로 출력
    @Query("SELECT s FROM StockEntity s WHERE s.memberNum = ?1 AND s.isHaving = true ORDER BY s.useByDate ASC")
    List<StockEntity> findTop9ByMemberNumAndIsHavingTrueOrderByUseByDateAsc(int memberNum, Pageable pageable);

    // ingredientName과 memberNum을 기준으로 재고 데이터 조회
    Optional<StockEntity> findByIngredientNameAndMemberNum(String ingredientName, Integer memberNum);

    // memberNum을 기준으로 재고 데이터 조회
    List<StockEntity> findByMemberNum(int memberNum);

    @Modifying
    @Query(value = "UPDATE stock s SET s.is_having = :isHaving WHERE s.ingredient_name = :ingredientName AND s.member_num = :memberNum", nativeQuery = true)
    void updateIsHavingByIngredientAndMember(@Param("ingredientName") String ingredientName,
                                             @Param("memberNum") Integer memberNum,
                                             @Param("isHaving") int isHaving);
}
