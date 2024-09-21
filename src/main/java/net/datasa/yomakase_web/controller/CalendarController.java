package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.domain.entity.CalendarEntity;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.service.CalendarService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

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
//        throw new RuntimeException("오류");
        //calendarDTO.setMemberNum(user);

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

    //날짜, 사용자 인증정보로 DB에 저장된 식단을 찾는 메소드
    @PostMapping("dietList")
    public CalendarDTO dietListSelect(@RequestParam("selectedDay") String selectedDay,
                                      @AuthenticationPrincipal AuthenticatedUser user){
        log.debug("식단 확인: {},{}", selectedDay, user.getUsername());

        LocalDate date = LocalDate.parse(selectedDay, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        CalendarDTO calDTO = new CalendarDTO();
        calDTO.setInputDate(date);
        calDTO.setId(user.getUsername());
        calDTO = calendarService.dietListSelect(calDTO);
        return calDTO;
    }

}