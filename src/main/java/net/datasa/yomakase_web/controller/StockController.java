package net.datasa.yomakase_web.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

@Controller
@Slf4j
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/save")
    public ResponseEntity<String> saveStock(@RequestBody List<Map<String, String>> ingredients) {
        try {
            // Spring Security에서 인증된 사용자 정보 가져오기
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            stockService.saveStock(ingredients, email);
            return new ResponseEntity<>("저장되었습니다!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/existing-ingredients")
    public ResponseEntity<List<String>> getExistingIngredients() {
        try {
            // Spring Security에서 인증된 사용자 정보 가져오기
            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            // 해당 사용자의 ingredient_name 목록 가져오기
            List<String> ingredients = stockService.getAllIngredientNamesForMember(email);

            return new ResponseEntity<>(ingredients, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}