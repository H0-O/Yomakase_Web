package net.datasa.yomakase_web.repository;

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
public interface HistoryRepository extends JpaRepository<HistoryEntity, Integer> {

    // 기존 메서드에 memberNum을 필터로 추가
    @Query("SELECT h.ingredientName, h.type, COUNT(h) " +
            "FROM HistoryEntity h " +
            "WHERE h.date BETWEEN :startDate AND :endDate " +
            "AND h.memberNum = :memberNum " +
            "GROUP BY h.ingredientName, h.type")
    List<Object[]> countByIngredientAndTypeAndMemberNum(LocalDate startDate, LocalDate endDate, Integer memberNum);
}