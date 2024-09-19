package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.CalendarId;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository <CalendarEntity, CalendarId> {

    // 특정 회원의 특정 날짜에 대한 Cal 엔티티를 찾는 메서드
    CalendarEntity findByInputDateAndMemberNum(LocalDate inputDate, int memberNum);

    // 특정 회원의 모든 Cal 엔티티를 찾는 메서드
    List<CalendarEntity> findByMemberNum(int memberNum);

    // 특정 날짜의 모든 Cal 엔티티를 찾는 메서드
    List<CalendarEntity> findByInputDate(LocalDate inputDate);

    // 특정 회원의 지정된 날짜 범위 내 모든 식단 데이터를 가져옵니다.
    List<CalendarEntity> findAllByMemberNumAndInputDateBetween(Integer memberNum, LocalDate startDate, LocalDate endDate);
}
