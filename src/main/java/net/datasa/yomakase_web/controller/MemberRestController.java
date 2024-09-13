package net.datasa.yomakase_web.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/member")
public class MemberRestController {

    private final MemberService memberService;
    private final HttpServletRequest request;
    private final HttpServletResponse response;

    public MemberRestController(MemberService memberService, HttpServletRequest request, HttpServletResponse response) {
        this.memberService = memberService;
        this.request = request;
        this.response = response;
    }

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

    // 회원 정보 수정 요청을 처리하는 메서드
    @PostMapping("/update")
    public String updateProfile(@ModelAttribute MemberDTO member) {
        log.debug("수정할 회원 정보: " + member.toString());  // 수정할 회원 정보 로그 출력

        // 서비스에서 회원 정보 업데이트 로직 실행
        memberService.updateUser(member);

        log.debug("회원 정보가 성공적으로 업데이트되었습니다.");  // 업데이트 완료 로그 출력

        // 수정 완료 후 다시 프로필 화면으로 리다이렉트
        return "redirect:/";
    }

}