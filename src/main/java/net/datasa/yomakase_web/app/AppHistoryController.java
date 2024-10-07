package net.datasa.yomakase_web.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.HistoryService;
import net.datasa.yomakase_web.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/history")
public class AppHistoryController {

    private final HistoryService historyService;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberService memberService;

    // 주간 소비 기록 데이터
    @GetMapping("/weekly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWeeklyHistory(@RequestHeader("Authorization") String token) {
        return getHistoryData(token, "weekly");
    }

    // 월간 소비 기록 데이터
    @GetMapping("/monthly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getMonthlyHistory(@RequestHeader("Authorization") String token) {
        return getHistoryData(token, "monthly");
    }

    // 연간 소비 기록 데이터
    @GetMapping("/yearly")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getYearlyHistory(@RequestHeader("Authorization") String token) {
        return getHistoryData(token, "yearly");
    }

    // 공통 데이터 로드 메서드
    private ResponseEntity<Map<String, Object>> getHistoryData(String token, String period) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            } else {
                log.warn("잘못된 토큰 형식: {}", token);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            // JWT 토큰에서 이메일 추출
            String userEmail = jwtTokenProvider.getUsernameFromToken(token);

            // 이메일을 통해 사용자 정보를 조회하여 memberNum 가져오기
            MemberDTO member = memberService.getUserByEmail(userEmail);
            if (member == null) {
                log.warn("사용자 정보가 존재하지 않습니다: {}", userEmail);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }

            // 서비스 계층에서 소비 기록 데이터 가져오기
            Map<String, Object> historyData;
            switch (period) {
                case "weekly":
                    historyData = historyService.getWeeklyHistory(member.getMemberNum());
                    break;
                case "monthly":
                    historyData = historyService.getMonthlyHistory(member.getMemberNum());
                    break;
                case "yearly":
                    historyData = historyService.getYearlyHistory(member.getMemberNum());
                    break;
                default:
                    log.warn("잘못된 기간 요청: {}", period);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }

            return ResponseEntity.ok(historyData);
        } catch (Exception e) {
            log.error("소비 기록을 가져오는 중 오류 발생: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
