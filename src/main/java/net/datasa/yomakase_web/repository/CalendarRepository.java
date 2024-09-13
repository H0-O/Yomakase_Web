package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.compositeK.CalendarId;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CalendarRepository extends JpaRepository <CalendarEntity, CalendarId> {

   /* // 특정 회원의 특정 날짜에 대한 Cal 엔티티를 찾는 메서드
    Cal findByInputDateAndMemberNum(LocalDate inputDate, int memberNum);

    // 특정 회원의 모든 Cal 엔티티를 찾는 메서드
    List<Cal> findByMemberNum(int memberNum);

    // 특정 날짜의 모든 Cal 엔티티를 찾는 메서드
    List<Cal> findByInputDate(LocalDate inputDate);*/

}
