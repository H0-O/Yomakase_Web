package net.datasa.yomakase_web.controller;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.service.CalendarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

/**
 * 캘린더 ajax요청을 처리하는 컨트롤러
 */
@RestController
@Slf4j
@RequestMapping("cal")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    /**
     * 식단 저장 메소드
     * @param arr
     * @param user 사용자 인증 정보
     */
    @PostMapping("dietSave")
    public void dietInputMethod(@RequestParam("dietArr") String[] arr, @AuthenticationPrincipal AuthenticatedUser user) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
        LocalDate date = LocalDate.parse(arr[3], formatter);

        CalendarDTO calDTO = new CalendarDTO();
        calDTO.setBName(arr[0]);
        calDTO.setLName(arr[1]);
        calDTO.setDName(arr[2]);
        calDTO.setInputDate(date);

        log.debug("식단 저장 배열: {},{},{},{}", arr[0], arr[1], arr[2], date);
        calendarService.mealSave(calDTO, user);

    }

    /**
     * 날짜, 사용자 인증정보로 DB에 저장된 식단을 찾는 메소드
     * @param selectedDay
     * @param user
     * @return
     */
    @PostMapping("dietList")
    public CalendarDTO dietListSelect(@RequestParam("selectedDay") String selectedDay,
                                      @AuthenticationPrincipal AuthenticatedUser user){
        //log.debug("식단 확인: {},{}", selectedDay, user.getUsername());
        LocalDate date = LocalDate.parse(selectedDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CalendarDTO calDTO = new CalendarDTO();
        calDTO.setInputDate(date);
        calDTO.setId(user.getUsername());
        log.debug("식단 확인 DTO : {}", calDTO);  //CalendarDTO(id=beta@yomakase.test, memberNum=0, inputDate=2024-09-01, bName=null, lName=null, dName=null, totalKcal=0, tooMuch=null, lack=null, recom=null, score=0)

        try {
            calDTO = calendarService.dietListSelect(calDTO);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.warn("RuntimeException: {}", e.getMessage());
        }

        return calDTO;
    }

    //영양소 정보 DB에서 가져와서 출력
    @PostMapping("nutrientList")
    public CalendarDTO nutrientListSelect(@RequestParam("selectedDay") String selectedDay,
                                          @AuthenticationPrincipal AuthenticatedUser user){
        log.debug("영양소 확인: {},{}", selectedDay, user.getUsername());

        LocalDate date = LocalDate.parse(selectedDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CalendarDTO calDTO = new CalendarDTO();
        calDTO.setInputDate(date);
        calDTO.setId(user.getUsername());
        try{
            calDTO = calendarService.nutrientListSelect(calDTO);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
            log.warn("RuntimeException: {}", e.getMessage());
        }

        return calDTO;
    }


    /**
     * 식단 점수를 DB에서 찾아와 조회하는 메서드
     * @param mouseoverDay
     * @param user
     * @return
     */
    @PostMapping("nutrientScore")
    public CalendarDTO nutrientScore(@RequestParam("mouseoverDay") String mouseoverDay,
                                     @AuthenticationPrincipal AuthenticatedUser user){
        log.debug("식단 점수 확인: {},{}", mouseoverDay, user.getUsername());

        LocalDate date = LocalDate.parse(mouseoverDay, DateTimeFormatter.ofPattern("yyyy-M-dd"));
        CalendarDTO calDTO = new CalendarDTO();
        calDTO.setInputDate(date);
        log.debug("확인: {}", calDTO.getInputDate());
        calDTO.setId(user.getUsername());
        try{
            calDTO = calendarService.nutrientListScore(calDTO);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
            if (e.getMessage().contains("MemberEntity")) {
                log.warn("Member not found");
                //return Collections.emptyMap(); // 빈 맵 반환
            } else if (e.getMessage().contains("CalendarEntity")) {
                log.warn("Calendar info not found : 사용자가 아직 식단을 입력하지 않은 날짜를 클릭함");
                //return Collections.emptyMap(); // 빈 맵 반환
            }
        } catch (RuntimeException e){
            e.printStackTrace();
            log.warn("RuntimeException: {}", e.getMessage());
        }


        return calDTO;
    }

}