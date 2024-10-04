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
import java.util.*;

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
    public void mealSave(CalendarDTO calDTO, Object identifier) {
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
        calEntity.setBName(calDTO.getBName());
        calEntity.setLName(calDTO.getLName());
        calEntity.setDName(calDTO.getDName());
        calEntity.setTooMuch(calDTO.getTooMuch());
        calEntity.setLack(calDTO.getLack());
        calEntity.setRecom(calDTO.getRecom());
        calEntity.setBKcal(calDTO.getBKcal());
        calEntity.setLKcal(calDTO.getLKcal());
        calEntity.setDKcal(calDTO.getDKcal());
        calEntity.setTotalKcal(calDTO.getTotalKcal());
        calEntity.setScore(calDTO.getScore());

        calendarRepository.save(calEntity);
    }

    public Map<String, String> getMealForDate(LocalDate date, Integer memberNum) {
        CalendarEntity calendarEntity = calendarRepository.findByInputDateAndMemberNum(date, memberNum);
        if (calendarEntity != null) {
            Map<String, String> mealData = new HashMap<>();
            mealData.put("breakfast", calendarEntity.getBName());
            mealData.put("lunch", calendarEntity.getLName());
            mealData.put("dinner", calendarEntity.getDName());
            return mealData;
        } else {
            return null;  // 해당 날짜에 대한 데이터가 없는 경우 null 반환
        }
    }
    public Map<String, List<Map<String, Object>>> getMealForMonth(YearMonth yearMonth, Integer memberNum) {
        // YearMonth의 첫 번째 날과 마지막 날을 구합니다.
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // 특정 회원의 해당 달 모든 식단 데이터를 가져옵니다.
        List<CalendarEntity> calendarEntities = calendarRepository.findAllByMemberNumAndInputDateBetween(memberNum, startDate, endDate);

        // 각 날짜별로 데이터를 Map에 넣습니다.
        Map<String, List<Map<String, Object>>> mealData = new HashMap<>(); // 수정된 타입

        for (CalendarEntity entity : calendarEntities) {
            String date = entity.getInputDate().toString(); // 날짜를 String으로 변환

            // 하루의 식단 데이터를 Map으로 만듭니다.
            Map<String, Object> dailyMeal = new HashMap<>(); // 수정된 타입
            dailyMeal.put("breakfast", entity.getBName());
            dailyMeal.put("lunch", entity.getLName());
            dailyMeal.put("dinner", entity.getDName());
            dailyMeal.put("score", entity.getScore()); // 점수는 Integer로 저장됨

            // 해당 날짜에 이미 데이터가 있으면 리스트에 추가, 없으면 새 리스트 생성
            mealData.computeIfAbsent(date, k -> new ArrayList<>()).add(dailyMeal);
        }

        return mealData;
    }
    public Map<String, String> getNutritionForDate(LocalDate date, Integer memberNum) {
        // 데이터베이스에서 해당 날짜와 사용자 번호로 영양소 데이터를 조회
        CalendarEntity calendarEntity = calendarRepository.findByInputDateAndMemberNum(date, memberNum);

        if (calendarEntity != null) {
            // 조회된 데이터를 Map에 담아 반환
            Map<String, String> nutritionData = new HashMap<>();
            nutritionData.put("totalCalories", String.valueOf(calendarEntity.getTotalKcal()));
            nutritionData.put("tooMuch", calendarEntity.getTooMuch());
            nutritionData.put("lack", calendarEntity.getLack());
            nutritionData.put("recommend", calendarEntity.getRecom());
            nutritionData.put("score", String.valueOf(calendarEntity.getScore()));

            return nutritionData;
        } else {
            // 해당 날짜에 대한 데이터가 없는 경우 null 반환
            return null;
        }
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

        //log.debug("캘린더복합키 조회: {}", calId);      //CalendarId(inputDate=2024-09-27, memberNum=1)

        // calendarRepository에서 복합키로 CalendarEntity 찾기
        CalendarEntity calEntity = calendarRepository.findById(calId).orElse(null);
        if (calEntity == null) {
            log.warn("No nutrient data found for calendar ID: {}", calId);
            return null;  // 데이터가 없을 경우 null 반환
        }
        //log.debug("식단 조회 서비스 : {}", calEntity);

        // 날짜와 memberNum으로 찾은 데이터를 CalendarDTO로 반환
        calDTO.setInputDate(calEntity.getInputDate());
        calDTO.setMemberNum(calEntity.getMemberNum());
        calDTO.setBName(calEntity.getBName());
        calDTO.setLName(calEntity.getLName());
        calDTO.setDName(calEntity.getDName());

        return calDTO;
    }

    //영양소 조회 메서드
    public CalendarDTO nutrientListSelect(CalendarDTO calDTO){
        // id를 통해 memberNum 조회
        MemberEntity memberEntity = memberRepository.findById(calDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member info not found"));

        // calendar 테이블의 복합키(프라이머리키)
        CalendarId calId = new CalendarId();
        calId.setInputDate(calDTO.getInputDate());
        calId.setMemberNum(memberEntity.getMemberNum());

        log.debug("영양소 조회용 캘린더 복합키 : {}", calId);

        // calendarRepository에서 복합키로 CalendarEntity 찾기
        CalendarEntity calEntity = calendarRepository.findById(calId)
                .orElseThrow(() -> new EntityNotFoundException("Calendar info not found : nutrient select part"));

        log.debug("영양소 조회 서비스 : {}", calEntity);
        // 날짜와 memberNum으로 찾은 데이터를 CalendarDTO로 반환
        calDTO.setInputDate(calEntity.getInputDate());
        calDTO.setMemberNum(calEntity.getMemberNum());
        calDTO.setTotalKcal(calEntity.getTotalKcal());
        calDTO.setTooMuch(calEntity.getTooMuch());
        calDTO.setLack(calEntity.getLack());
        calDTO.setRecom(calEntity.getRecom());
        calDTO.setScore(calEntity.getScore());

        return calDTO;
    }


    //식단점수 조회 메서드
    public CalendarDTO nutrientListScore(CalendarDTO calDTO){
        // id를 통해 memberNum 조회
        MemberEntity memberEntity = memberRepository.findById(calDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Member info not found"));

        // calendar 테이블의 복합키(프라이머리키)
        CalendarId calId = new CalendarId();
        calId.setInputDate(calDTO.getInputDate());
        calId.setMemberNum(memberEntity.getMemberNum());

        // calendarRepository에서 복합키로 CalendarEntity 찾기
        CalendarEntity calEntity = calendarRepository.findById(calId)
                .orElseThrow(() -> new EntityNotFoundException("Calendar Entity not found : nutrient select part"));

        log.debug("영양소 조회 서비스 : {}", calEntity);
        // 날짜와 memberNum으로 찾은 데이터를 CalendarDTO로 반환
        calDTO.setScore(calEntity.getScore());

        return calDTO;
    }
}

