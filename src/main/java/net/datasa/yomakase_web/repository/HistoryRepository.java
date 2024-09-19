package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.HistoryId;
import net.datasa.yomakase_web.domain.entity.HistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 소비기록 관련 Repository
 */
@Repository
public interface HistoryRepository extends JpaRepository<HistoryEntity, HistoryId> {

    // 각 재료별로 소비와 버림의 횟수를 기간 내에서 집계하는 쿼리
    @Query("SELECT h.ingredientName, h.type, COUNT(h) " +
            "FROM HistoryEntity h " +
            "WHERE h.date BETWEEN :startDate AND :endDate " +
            "GROUP BY h.ingredientName, h.type")
    List<Object[]> countByIngredientAndType(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}