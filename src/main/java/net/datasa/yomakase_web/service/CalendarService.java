package net.datasa.yomakase_web.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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



        /*CalendarEntity calEntity = CalendarEntity.builder()
                //.member.setMemberId(user.getUsername()) 와 멤버 아이디가 있으니까 아이디가지고 num찾아내는 메소드 만들어서 그걸 꺼내써야되네
                //.member.setId(user.getUsername())
               // .inputDate(calendarDTO.getInputDate())

                .bName(calDTO.getBName())
                .lName(calDTO.getLName())
                .dName(calDTO.getDName())
                .build();*/
//        CalendarEntity calEntity = new CalendarEntity();
//        calEntity.setInputDate(calDTO.getInputDate());
//        calEntity.setMemberNum(memberEntity.getMemberNum());
//        calEntity.setBName(calDTO.getBName());
//        calEntity.setLName(calDTO.getLName());
//        calEntity.setDName(calDTO.getDName());
       /* calEntity.setBName(arr[0]);
        calEntity.setLName(arr[1]);
        calEntity.setDName(arr[2]);
        */
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

    
}
