package net.datasa.yomakase_web.service;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.compositeK.CalendarId;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.CalendarRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CalendarService {
    private final CalendarRepository calendarRepository;
    private final MemberRepository memberRepository;


    /**
     * 식단 저장 메소드
     * @param calDTO    입력날짜, 아침, 점심, 저녁 메뉴 이름
     * @param user      사용자 인증 정보
     */
    public void dietSave(CalendarDTO calDTO, AuthenticatedUser user) {
        //사용자의 id를 가지고 memberNum을 가져오는 부분 필요
        MemberEntity memberEntity = memberRepository.findById(user.getUsername()).orElseThrow(null);

        CalendarEntity calEntity = new CalendarEntity();
        calEntity.setInputDate(calDTO.getInputDate());
        calEntity.setMemberNum(memberEntity.getMemberNum());
        calEntity.setBName(calDTO.getBName());
        calEntity.setLName(calDTO.getLName());
        calEntity.setDName(calDTO.getDName());

        calendarRepository.save(calEntity);
    }


    /**
     * 식단 조희 메서드
     * @param calDTO    사용자가 선택한 날짜, 사용자 아이디(이메일)
     * @return
     */
    public CalendarDTO dietListSelect(CalendarDTO calDTO) {
        // id를 통해 memberNum을 쓰려고 함
        MemberEntity memberEntity = memberRepository.findById(calDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member info not found"));

        // calendar 테이블의 복합키(프라이머리키)
        CalendarId calId = new CalendarId();
        calId.setInputDate(calDTO.getInputDate());
        calId.setMemberNum(memberEntity.getMemberNum());

        log.debug("캘린더복합키 조회: {}", calId);      //CalendarId(inputDate=2024-09-27, memberNum=1)
        // calendarRepository에서 복합키로 CalendarEntity 찾기
        CalendarEntity calEntity = calendarRepository.findById(calId)
                .orElseThrow(() -> new EntityNotFoundException("Calendar info not found"));

        // log.debug("식단 조회 서비스 : {}", calEntity.toString());
        // 날짜와 memberNum으로 찾은 데이터를 CalendarDTO로 반환
        calDTO.setInputDate(calEntity.getInputDate());
        calDTO.setMemberNum(calEntity.getMemberNum());
        calDTO.setBName(calEntity.getBName());
        calDTO.setLName(calEntity.getLName());
        calDTO.setDName(calEntity.getDName());

        return calDTO;
    }

}
