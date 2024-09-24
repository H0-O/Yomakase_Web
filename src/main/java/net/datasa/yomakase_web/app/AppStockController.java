package net.datasa.yomakase_web.app;

import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.HistoryDTO;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.HistoryService;
import net.datasa.yomakase_web.service.StockService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController  // REST API 처리를 위한 컨트롤러
@Slf4j
@RequestMapping("/api/stock")
@RequiredArgsConstructor
public class AppStockController {
    private final StockService stockService;
    private final HistoryService historyService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/save")
    public ResponseEntity<String> saveStockForApp(
            @RequestBody Map<String, String> ingredients, // Map<String, String>으로 변경
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 로그로 토큰과 데이터 출력
            log.info("Received token: {}", token);
            log.info("Received ingredients: {}", ingredients); // 수정된 데이터 타입

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // 'Bearer ' 이후의 실제 JWT 부분만 추출
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken); // JWT에서 사용자 식별자 추출
                log.info("Extracted memberName from token: {}", memberNumFromToken);

                // 데이터 저장 처리
                stockService.saveStock(ingredients, memberNumFromToken); // 앱 사용자 처리
                log.info("Stock saved successfully for memberNum: {}", memberNumFromToken);
                return new ResponseEntity<>("저장되었습니다!", HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("앱 저장 실패: {}", e.getMessage());
            return new ResponseEntity<>("저장에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/list")
    public ResponseEntity<?> getStockForApp(
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            // 로그로 토큰 출력
            log.info("Received token: {}", token);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // 'Bearer ' 이후의 실제 JWT 부분만 추출
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken); // JWT에서 사용자 식별자 추출
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // 스톡 데이터 가져오기
                List<Map<String, Object>> stockList = stockService.getStockForMember(memberNumFromToken); // 앱 사용자 처리
                log.info("Fetched stock list for memberNum: {}", memberNumFromToken);
                log.debug("Fetched stock list: {}", stockList);

                // 데이터가 없을 경우
                if (stockList.isEmpty()) {
                    return new ResponseEntity<>("스톡 데이터가 없습니다.", HttpStatus.NO_CONTENT);
                }

                return new ResponseEntity<>(stockList, HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("스톡 데이터 가져오기 실패: {}", e.getMessage());
            return new ResponseEntity<>("스톡 데이터를 가져오는 데 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/history")
    public ResponseEntity<String> saveHistoryForApp(
            @RequestBody Map<String, String> historyData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            log.info("Received token: {}", token);
            log.info("Received historyData: {}", historyData);

            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7);
                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken);
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // DTO 생성 및 서비스로 위임
                historyService.saveHistory(historyData, memberNumFromToken);
                stockService.updateStockIsHaving(historyData.get("ingredientName"), memberNumFromToken);

                return new ResponseEntity<>("소비 또는 버림이 성공적으로 기록되었습니다.", HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            log.error("앱 저장 실패: {}", e.getMessage());
            return new ResponseEntity<>("저장에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/updateUseByDate")
    public ResponseEntity<String> updateUseByDate(
            @RequestBody Map<String, String> requestData,
            @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            log.info("Received token: {}", token);
            log.info("Received requestData: {}", requestData);

            // 토큰을 사용한 사용자 정보 추출
            if (token != null && token.startsWith("Bearer ")) {
                String jwtToken = token.substring(7); // 'Bearer ' 이후의 실제 JWT 부분만 추출
                log.info("JWT Token (parsed): {}", jwtToken);

                Integer memberNumFromToken = jwtTokenProvider.getMemberNumFromToken(jwtToken); // JWT에서 사용자 식별자 추출
                log.info("Extracted memberNum from token: {}", memberNumFromToken);

                // requestData에서 값 추출
                String ingredientName = requestData.get("ingredientName");
                String newUseByDateStr = requestData.get("newUseByDate");

                if (ingredientName == null || newUseByDateStr == null) {
                    return new ResponseEntity<>("재료명 또는 새로운 소비기한이 누락되었습니다.", HttpStatus.BAD_REQUEST);
                }

                // newUseByDateStr을 LocalDate로 변환
                LocalDate newUseByDate = LocalDate.parse(newUseByDateStr);

                // 서비스 메서드 호출하여 소비기한 업데이트
                stockService.updateStockDate(ingredientName, memberNumFromToken, newUseByDate);

                return new ResponseEntity<>("소비기한이 성공적으로 업데이트되었습니다.", HttpStatus.OK);
            } else {
                log.warn("No or invalid token provided.");
                return new ResponseEntity<>("인증되지 않은 사용자입니다.", HttpStatus.UNAUTHORIZED);
            }
        } catch (JwtException e) {
            log.error("JWT 인증 실패: {}", e.getMessage());
            return new ResponseEntity<>("JWT 토큰이 유효하지 않습니다: " + e.getMessage(), HttpStatus.UNAUTHORIZED);
        } catch (RuntimeException e) {
            log.error("재고 항목 업데이트 실패: {}", e.getMessage());
            return new ResponseEntity<>("재고 항목 업데이트에 실패했습니다: " + e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("소비기한 업데이트 실패: {}", e.getMessage());
            return new ResponseEntity<>("소비기한 업데이트에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
