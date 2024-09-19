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
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
     * @param identifier     사용자 인증 정보
     */
    public void dietSave(CalendarDTO calDTO, Object identifier) {
        Integer memberNum = null;

        // identifier가 Integer일 경우: 앱에서 전달된 memberNum 처리
        if (identifier instanceof Integer) {
            memberNum = (Integer) identifier;
        }
        // identifier가 String일 경우: 이메일(아이디)로 사용자를 조회하여 memberNum 처리
        else if (identifier instanceof String) {
            MemberEntity member = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            memberNum = member.getMemberNum();
        }
        // CalendarEntity 생성 및 데이터 설정
        CalendarEntity calEntity = new CalendarEntity();
        calEntity.setInputDate(calDTO.getInputDate());
        calEntity.setMemberNum(memberNum);  // 사용자 memberNum 설정
        calEntity.setBName(calDTO.getBName());  // 아침 데이터 설정
        calEntity.setLName(calDTO.getLName());  // 점심 데이터 설정
        calEntity.setDName(calDTO.getDName());  // 저녁 데이터 설정
        //사용자의 id를 가지고 memberNum을 가져오는 부분 필요
        //MemberEntity memberEntity =  memberRepository.findById(user.getUsername()).orElseThrow(null);

        calendarRepository.save(calEntity);
    }

    public Map<String, String> getDietForDate(LocalDate date, Integer memberNum) {
        CalendarEntity calendarEntity = calendarRepository.findByInputDateAndMemberNum(date, memberNum);
        if (calendarEntity != null) {
            Map<String, String> dietData = new HashMap<>();
            dietData.put("breakfast", calendarEntity.getBName());
            dietData.put("lunch", calendarEntity.getLName());
            dietData.put("dinner", calendarEntity.getDName());
            return dietData;
        } else {
            return null;  // 해당 날짜에 대한 데이터가 없는 경우 null 반환
        }
    }
    public Map<String, List<Map<String, String>>> getDietForMonth(YearMonth yearMonth, Integer memberNum) {
        // YearMonth의 첫 번째 날과 마지막 날을 구합니다.
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 특정 회원의 해당 달 모든 식단 데이터를 가져옵니다.
        List<CalendarEntity> calendarEntities = calendarRepository.findAllByMemberNumAndInputDateBetween(memberNum, startDate, endDate);

        // 각 날짜별로 데이터를 Map에 넣습니다.
        Map<String, List<Map<String, String>>> dietData = new HashMap<>();

        for (CalendarEntity entity : calendarEntities) {
            String date = entity.getInputDate().toString(); // 날짜를 String으로 변환

            // 하루의 식단 데이터를 Map으로 만듭니다.
            Map<String, String> dailyDiet = new HashMap<>();
            dailyDiet.put("breakfast", entity.getBName());
            dailyDiet.put("lunch", entity.getLName());
            dailyDiet.put("dinner", entity.getDName());

            // 해당 날짜에 이미 데이터가 있으면 리스트에 추가, 없으면 새 리스트 생성
            dietData.computeIfAbsent(date, k -> new ArrayList<>()).add(dailyDiet);
        }

        return dietData;
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
