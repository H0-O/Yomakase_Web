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
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();            // 인증된 사용자가 있는지 확인
            if (authentication == null || !authentication.isAuthenticated()) {
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }

            String username = authentication.getName(); // 사용자 이름(이메일) 가져오기
            stockService.saveStock(ingredients, username);
            return new ResponseEntity<>("저장되었습니다!", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("저장에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/existing-ingredients")
    public ResponseEntity<List<String>> getExistingIngredients() {
        try {
            List<String> ingredients = stockService.getAllIngredientNames();
            return new ResponseEntity<>(ingredients, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
