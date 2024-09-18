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

    @PostMapping("/diet")
    public ResponseEntity<String> dietInputMethod(
            @RequestBody Map<String, String> dietData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 로그로 토큰과 데이터 출력
            log.info("Received token: {}", token);
            log.info("Received diet data: {}", dietData);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);  // 'Bearer ' 이후의 실제 JWT 부분만 추출
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);  // JWT에서 사용자 식별자 추출
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // DateTimeFormatter를 사용하여 "24년 09월 04일" 문자열을 LocalDate로 변환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
                LocalDate inputDate = LocalDate.parse(dietData.get("date"), formatter);  // 'date' 키로 날짜 받음

                // CalendarDTO에 데이터를 설정
                CalendarDTO calDTO = new CalendarDTO();
                calDTO.setBName(dietData.get("breakfast"));  // 'breakfast' 키로 아침 데이터를 받음
                calDTO.setLName(dietData.get("lunch"));  // 'lunch' 키로 점심 데이터를 받음
                calDTO.setDName(dietData.get("dinner"));  // 'dinner' 키로 저녁 데이터를 받음
                calDTO.setInputDate(inputDate);

                // 데이터 저장 처리
                calendarService.dietSave(calDTO, memberNumFromToken);  // CalendarService의 dietSave 메서드 호출
                log.info("Diet saved successfully for memberNum: {}", memberNumFromToken);

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
    @GetMapping("/diet/{date}")
    public ResponseEntity<Map<String, String>> getDietForDate(
            @PathVariable("date") String date,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 토큰과 날짜 로그
            log.info("Received token: {}", token);
            log.info("Requested date: {}", date);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // DateTimeFormatter를 사용하여 "24년 09월 04일" 형식의 문자열을 LocalDate로 변환
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy년 MM월 dd일");
                LocalDate inputDate = LocalDate.parse(date, formatter);

                // 특정 날짜에 해당하는 식단 데이터를 조회
                Map<String, String> dietData = calendarService.getDietForDate(inputDate, memberNumFromToken);

                if (dietData != null) {
                    log.info("Diet data found for memberNum {}: {}", memberNumFromToken, dietData);
                    return new ResponseEntity<>(dietData, HttpStatus.OK);
                } else {
                    log.warn("No diet data found for the given date.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error fetching diet data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 특정 달의 식단 데이터를 가져오는 GET 요청 처리
    @GetMapping(value = "/diet/month/{yearMonth}", produces = "application/json; charset=UTF-8")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getDietForMonth(
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
                Map<String, List<Map<String, String>>> dietData = calendarService.getDietForMonth(inputYearMonth, memberNumFromToken);

                if (dietData != null) {
                    log.info("Diet data found for memberNum {}: {}", memberNumFromToken, dietData);
                    return new ResponseEntity<>(dietData, HttpStatus.OK);
                } else {
                    log.warn("No diet data found for the given month.");
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("Error fetching diet data: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
