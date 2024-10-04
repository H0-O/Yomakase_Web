package net.datasa.yomakase_web.controller;

import net.datasa.yomakase_web.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    // 주간 데이터
    @GetMapping("/history/weekly")
    @ResponseBody
    public Map<String, Object> getWeeklyHistory(@RequestParam Integer memberNum) {
        return historyService.getWeeklyHistory(memberNum);
    }

    // 월간 데이터
    @GetMapping("/history/monthly")
    @ResponseBody
    public Map<String, Object> getMonthlyHistory(@RequestParam Integer memberNum) {
        return historyService.getMonthlyHistory(memberNum);
    }

    // 연간 데이터
    @GetMapping("/history/yearly")
    @ResponseBody
    public Map<String, Object> getYearlyHistory(@RequestParam Integer memberNum) {
        return historyService.getYearlyHistory(memberNum);
    }
}