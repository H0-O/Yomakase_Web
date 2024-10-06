package net.datasa.yomakase_web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@AllArgsConstructor
@RequestMapping("/member")
public class MemberRestController {

    private final MemberService memberService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        // 로그인한 사용자 정보 가져오기
        String username = userDetails.getUsername();
        log.debug("현재 로그인한 사용자 ID: {}", username);

        try {
            // 회원 삭제 처리
            memberService.deleteMemberById(username);
            log.debug("회원 탈퇴 성공: {}", username);

            // 로그아웃 처리
            request.logout(); // 세션 및 인증 정보 삭제
            log.debug("회원 로그아웃 성공: {}", username);

            return ResponseEntity.ok("회원 탈퇴 및 로그아웃 성공");
        } catch (Exception e) {
            log.error("회원 탈퇴 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 탈퇴 실패");
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateMember(@RequestBody MemberDTO memberDTO) {
        try {
            // 회원 정보 업데이트 로직 (서비스를 통해 처리)
            memberService.updateUser(memberDTO);

            // 성공 메시지 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "회원정보가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response); // JSON 응답
        } catch (Exception e) {
            // 오류 발생 시
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "회원정보 수정에 실패했습니다.");
            return ResponseEntity.status(500).body(errorResponse); // 오류 응답
        }
    }


}