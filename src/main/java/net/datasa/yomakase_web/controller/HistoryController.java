package net.datasa.yomakase_web.controller;

import net.datasa.yomakase_web.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
public class HistoryController {

    @Autowired
    private HistoryService historyService;

    @GetMapping("/history/weekly")
    @ResponseBody
    public Map<String, Object> getWeeklyHistory() {
        return historyService.getWeeklyHistory();
    }

    @GetMapping("/history/monthly")
    @ResponseBody
    public Map<String, Object> getMonthlyHistory() {
        return historyService.getMonthlyHistory();
    }

    @GetMapping("/history/yearly")
    @ResponseBody
    public Map<String, Object> getYearlyHistory() {
        return historyService.getYearlyHistory();
    }
}