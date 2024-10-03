package net.datasa.yomakase_web.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.service.MemberService;
import net.datasa.yomakase_web.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Controller
@AllArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final SubscriptionService subscriptionService;

    // 회원가입 폼을 보여주는 메소드
    @GetMapping("signupForm")
    public String signupForm() {
        return "signupForm";
    }

    // 회원가입 폼 제출을 처리하는 메소드
    @PostMapping("signupForm")
    public String join(@ModelAttribute("member") MemberDTO dto) {
        log.debug("전달된 회원정보: {}", dto);
        memberService.saveMember(dto);
        return "loginForm";
    }

    // ID 중복 확인 폼을 보여주는 메소드
    @GetMapping("/idCheck")
    public String idCheck() {
        return "idCheck";
    }

    // ID 중복 확인 요청 처리
    @PostMapping("idCheck")
    @ResponseBody
    public String idChecked(@RequestParam("searchId") String searchId) {
        boolean result = memberService.find(searchId);

        // 중복 여부에 따라 메시지 반환
        if (result) {
            return searchId + "은(는) 사용가능한 ID입니다.";
        } else {
            return searchId + "은(는) 이미 존재하는 ID입니다.";
        }
    }

    @GetMapping("loginForm")
    public String loginForm() {
        return "loginForm";
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(Authentication authentication) {
        try {
            // 현재 인증된 사용자의 이메일(아이디)을 가져옴
            String userEmail = authentication.getName();
            log.info("구독 요청 시작 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 구독 상태를 변경하기 위한 서비스 호출 (DB에서 해당 사용자의 구독 상태 업데이트)
            memberService.subscribeUser(userEmail);
            subscriptionService.subscribeUser(userEmail);
            log.info("사용자의 구독 상태 변경 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 사용자의 현재 권한을 가져옴
            MemberEntity member = memberService.findByEmail(userEmail);
            log.info("회원 정보 조회 완료 - 사용자 이메일: {}, 현재 ROLE: {}", userEmail, member.getUserRole()); // 로그 추가

            // 사용자의 기존 권한을 기반으로 새로운 권한(ROLE_SUBSCRIBER)을 추가
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
            updatedAuthorities.add(new SimpleGrantedAuthority(member.getUserRole())); // 새로운 ROLE 추가
            log.info("새로운 권한 추가 완료 - 사용자 이메일: {}, 추가된 ROLE: {}", userEmail, member.getUserRole()); // 로그 추가

            // 기존 인증 정보에 새로운 권한을 반영하여 새로운 Authentication 객체 생성
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    updatedAuthorities
            );
            log.info("새로운 Authentication 객체 생성 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // SecurityContext에 새로운 Authentication 설정 (권한 정보가 업데이트된 Authentication 객체로 교체)
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            log.info("SecurityContext에 새로운 Authentication 설정 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 성공 메시지를 포함한 응답을 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "구독 성공");
            log.info("구독 성공 메시지 반환 - 사용자 이메일: {}", userEmail); // 로그 추가

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            // 오류 발생 시 로그를 남기고 오류 응답을 반환
            log.error("구독 실패 - 사용자 이메일: {}, 오류: {}", authentication.getName(), e.getMessage(), e); // 로그 추가

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "구독 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(Authentication authentication) {
        try {
            // 사용자의 이메일을 가져옴 (인증된 사용자의 이름, 이메일이 ID로 사용될 경우)
            String userEmail = authentication.getName();
            log.info("Unsubscribe 요청 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 구독 취소 상태로 변경하는 서비스 호출 (DB에서 구독 상태 업데이트)
            memberService.unsubscribeUser(userEmail);
            subscriptionService.unsubscribeUser(userEmail);
            log.info("구독 취소 상태 변경 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 사용자의 현재 권한을 가져옴
            MemberEntity member = memberService.findByEmail(userEmail);
            log.info("회원 정보 조회 완료 - 사용자 이메일: {}, 현재 ROLE: {}", userEmail, member.getUserRole()); // 로그 추가

            // 사용자의 기존 권한에서 ROLE_SUBSCRIBER 제거
            List<GrantedAuthority> updatedAuthorities = new ArrayList<>(authentication.getAuthorities());
            updatedAuthorities.removeIf(authority -> authority.getAuthority().equals("ROLE_SUBSCRIBER")); // ROLE_SUBSCRIBER 제거
            log.info("기존 권한에서 ROLE_SUBSCRIBER 제거 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 기존 인증 정보에 새로운 권한을 반영하여 새로운 Authentication 객체 생성
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    updatedAuthorities
            );
            log.info("새로운 Authentication 객체 생성 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // SecurityContext에 새로운 Authentication 설정 (권한 정보가 업데이트된 Authentication 객체로 교체)
            SecurityContextHolder.getContext().setAuthentication(newAuth);
            log.info("SecurityContext에 새로운 Authentication 설정 완료 - 사용자 이메일: {}", userEmail); // 로그 추가

            // 성공 메시지를 포함한 응답을 반환
            Map<String, String> response = new HashMap<>();
            response.put("message", "구독 취소 성공");
            log.info("구독 취소 성공 메시지 반환 - 사용자 이메일: {}", userEmail); // 로그 추가
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // 오류 발생 시 예외 메시지를 로그에 기록하고, 실패 응답 반환
            log.error("구독 취소 실패 - 사용자 이메일: {}, 오류: {}", authentication.getName(), e.getMessage(), e); // 로그 추가

            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("message", "구독 취소 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

