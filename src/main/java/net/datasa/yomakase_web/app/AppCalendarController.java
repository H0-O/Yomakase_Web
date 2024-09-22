package net.datasa.yomakase_web.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.CalendarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@RestController  // REST API 처리를 위한 컨트롤러
@Slf4j
@RequestMapping("/api/cal")
@RequiredArgsConstructor
public class AppCalendarController {

    private final CalendarService calendarService;  // CalendarService는 식단 데이터 저장 로직을 담당
    private final JwtTokenProvider jwtTokenProvider;  // JWT를 통한 인증 처리

    @PostMapping("/nutrition/result")
    public ResponseEntity<String> mealInputMethod(
            @RequestBody Map<String, Object> mealData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 로그로 토큰과 데이터 출력
            log.info("Received token: {}", token);
            log.info("Received meal data: {}", mealData);

            // 이 부분에서 전달된 데이터 확인
            if (mealData != null) {
                log.info("Data fields received: {}", mealData.keySet());  // 어떤 키들이 전송되었는지 확인
            }

            // 데이터를 제대로 받았는지 확인한 뒤 처리
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
                LocalDate inputDate = LocalDate.parse((String) mealData.get("date"), formatter);  // 'date' 필드를 파싱
                log.info("Received input_date: {}", mealData.get("date"));

                // 기대하는 필드 확인

                String breakfast = (String) mealData.get("breakfast");
                String lunch = (String) mealData.get("lunch");
                String dinner = (String) mealData.get("dinner");

                Map<String, Object> kalMap = (Map<String, Object>) mealData.get("kal");
                int breakfastCalories = (int) kalMap.get("b");
                int lunchCalories = (int) kalMap.get("l");
                int dinnerCalories = (int) kalMap.get("d");
                int totalCalories = (int) kalMap.get("t");

                List<String> overNutrients = (List<String>) mealData.get("over");
                List<String> lackNutrients = (List<String>) mealData.get("lack");
                String recommendation = (String) mealData.get("rec");
                int score = (int) mealData.get("score");

                // CalendarDTO에 데이터를 설정
                CalendarDTO calDTO = new CalendarDTO();
                calDTO.setInputDate(inputDate);
                calDTO.setBName(breakfast);
                calDTO.setLName(lunch);
                calDTO.setDName(dinner);

                calDTO.setBKcal(breakfastCalories);
                calDTO.setLKcal(lunchCalories);
                calDTO.setDKcal(dinnerCalories);
                calDTO.setTotalKcal(totalCalories);

                calDTO.setTooMuch(String.join(",", overNutrients));
                calDTO.setLack(String.join(",", lackNutrients));
                calDTO.setRecom(recommendation);
                calDTO.setScore(score);

                // 데이터 저장 처리
                calendarService.mealSave(calDTO, memberNumFromToken);
                log.info("Meal saved successfully for memberNum: {}", memberNumFromToken);

                return new ResponseEntity<>("식단이 저장되었습니다!", HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("식단 저장 실패: {}", e.getMessage());
            return new ResponseEntity<>("저장에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // 특정 날짜의 식단 데이터를 가져오는 GET 요청 처리
    @GetMapping("/meal/{date}")
    public ResponseEntity<Map<String, String>> getMealForDate(
            @PathVariable("date") String date,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 토큰과 날짜 로그
            log.debug("받은 token: {}", token);
            log.debug("받은 date: {}", date);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                log.debug("JWT 토큰 (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.debug("Extracted memberNum from token: {}", memberNumFromToken);

                // DateTimeFormatter를 사용하여 "24년 09월 04일" 형식의 문자열을 LocalDate로 변환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
                LocalDate inputDate = LocalDate.parse(date, formatter);

                // 특정 날짜에 해당하는 식단 데이터를 조회
                Map<String, String> mealData = calendarService.getMealForDate(inputDate, memberNumFromToken);

                if (mealData != null) {
                    log.debug("meal data found for memberNum {}: {}", memberNumFromToken, mealData);
                    return new ResponseEntity<>(mealData, HttpStatus.OK);
                } else {
                    log.warn("No meal data found for the given date.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error fetching meal data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 특정 달의 식단 데이터를 가져오는 GET 요청 처리
    @GetMapping(value = "/meal/month/{yearMonth}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, List<Map<String, Object>>>> getMealForMonth(
            @PathVariable("yearMonth") String yearMonth,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 토큰과 날짜 로그
            log.info("Received token: {}", token);
            log.info("Requested YearMonth: {}", yearMonth);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // "yyyy-MM" 형식의 YearMonth를 파싱
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                YearMonth inputYearMonth = YearMonth.parse(yearMonth, formatter);

                // 특정 달에 해당하는 모든 식단 데이터를 조회
                Map<String, List<Map<String, Object>>> mealData = calendarService.getMealForMonth(inputYearMonth, memberNumFromToken);

                if (mealData != null) {
                    log.info("Meal data found for memberNum {}: {}", memberNumFromToken, mealData);
                    return new ResponseEntity<>(mealData, HttpStatus.OK);
                } else {
                    log.warn("No meal data found for the given month.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error fetching meal data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/nutrition/{date}")
    public ResponseEntity<Map<String, String>> getNutritionForDate(
            @PathVariable("date") String date,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 토큰과 날짜 로그
            log.debug("Received token: {}", token);
            log.debug("Received date: {}", date);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                log.debug("Parsed JWT token: {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.debug("Extracted memberNum from token: {}", memberNumFromToken);

                // DateTimeFormatter를 사용하여 "24년 09월 04일" 형식의 문자열을 LocalDate로 변환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
                LocalDate inputDate = LocalDate.parse(date, formatter);

                // 특정 날짜에 해당하는 영양소 데이터를 조회
                Map<String, String> nutritionData = calendarService.getNutritionForDate(inputDate, memberNumFromToken);

                if (nutritionData != null) {
                    log.debug("Nutrition data found for memberNum {}: {}", memberNumFromToken, nutritionData);
                    return new ResponseEntity<>(nutritionData, HttpStatus.OK);
                } else {
                    log.warn("No nutrition data found for the given date.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error fetching nutrition data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
