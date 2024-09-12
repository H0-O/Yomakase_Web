package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.CalendarDTO;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.service.CalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("cal")
@RequiredArgsConstructor
public class CalendarController {

    private final CalendarService calendarService;

    //ajax요청을 처리하는 메소드
    @PostMapping("diet")
    public void dietInputMethod(@RequestParam("dietArr") String[] arr) {
//        throw new RuntimeException("오류");
        //calendarDTO.setMemberNum(user);
        for (String s : arr) {
            log.debug("배열요소 : {}", s);
            //AuthenticatedUser user = new AuthenticatedUser();
        } //success로 설정한 그 함수로 리턴되어 간다. 리턴값이 있든 없든!
        calendarService.dietSave(arr);
    }

}