package net.datasa.yomakase_web.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class CalendarController {

    //ajax요청을 처리하는 메소드
    //테스트용입니다
    @GetMapping("ajaxtest1")
    public void ajaxtest1() {
        log.debug("AjaxController의 ajaxtext1() 메소드 실행됨");
        throw new RuntimeException("오류");

    } //success로 설정한 그 함수로 리턴되어 간다. 리턴값이 있든 없든!
}
