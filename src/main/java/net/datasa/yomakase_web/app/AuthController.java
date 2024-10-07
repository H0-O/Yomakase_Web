package net.datasa.yomakase_web.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.MemberInfoService;
import net.datasa.yomakase_web.service.MemberService;
import net.datasa.yomakase_web.service.SubscriptionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;  // 사용자 조회 로직을 처리하는 서비스
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final MemberInfoService memberInfoService;
    private final SubscriptionService subscriptionService;  // 구독 서비스 의존성 주입

    // 이메일 중복 확인
    @PostMapping("/emailCheck")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        log.debug("받은 데이터 {}", email);
        // 이메일 중복 확인 로직 호출
        boolean isDuplicate = memberService.find(email);

        // 중복 여부에 따라 메시지 반환
        if (isDuplicate) {
            return ResponseEntity.ok(email + "은(는) 사용 가능한 이메일입니다.");
        } else {
            return ResponseEntity.ok(email + "은(는) 이미 존재하는 이메일입니다.");
        }
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signUp(@RequestBody MemberDTO memberDTO) {
        log.debug("전달된 회원 정보1 : {}", memberDTO);

        // 회원 정보 저장
        memberService.saveMember(memberDTO);

        // 응답 반환
        return ResponseEntity.status(HttpStatus.CREATED).body("회원가입이 완료되었습니다.");
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        // 사용자 이메일로 사용자 정보 조회
        MemberEntity member = memberService.findByEmail(loginRequest.getId());

        // 비밀번호 검증
        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPw())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponseDTO(null, "로그인 실패", null));
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.generateToken(member);

        // 응답
        return ResponseEntity.ok(new LoginResponseDTO(member.getId(), "로그인 성공", token));
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCompleteUserInfo(@RequestHeader("Authorization") String token) {
        // 'Bearer '를 제거하고 공백을 제거
        if (token.startsWith("Bearer ")) {
            token = token.substring(7).trim();  // 토큰 앞뒤 공백 제거
        } else {
            log.warn("Invalid token format: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        // JWT 토큰에서 이메일 추출
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);

        // 사용자 정보 가져오기
        MemberDTO member = memberService.getUserByEmail(userEmail);

        // 사용자 알러지 정보 가져오기
        Map<String, Boolean> allergyInfo = memberInfoService.getAllergyByEmail(userEmail);

        // 생년월일을 통해 나이 계산
        LocalDate birthDate = member.getBirthDate(); // MemberDTO에서 생년월일 가져옴
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 필요한 정보만 추출하여 하나의 JSON 객체로 생성
        Map<String, Object> response = new HashMap<>();

        // 사용자 기본 정보
        response.put("memberNum", member.getMemberNum());
        response.put("id", member.getEmail());
        response.put("nickName", member.getNickname());
        response.put("gen", member.getGender());
        response.put("birthDate", birthDate);
        response.put("userRole", member.getUserRole());
        response.put("enabled", member.isEnabled());

        // 바디 정보
        response.put("height", member.getHeight());   // 키
        response.put("weight", member.getWeight());   // 몸무게
        response.put("age", age);                     // 나이 (계산된 값)
        response.put("gender", member.getGender());   // 성별

        // 알러지 정보
        response.put("allergies", allergyInfo);  // 알러지 정보 추가

        log.debug("통합 사용자 정보 : {}", response);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, String>> updateMember(
            @RequestHeader("Authorization") String token,
            @RequestBody MemberDTO memberDTO) {

        Map<String, String> response = new HashMap<>();

        try {
            // 'Bearer ' 접두어 제거
            if (token.startsWith("Bearer ")) {
                token = token.substring(7).trim();
            } else {
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // 토큰에서 사용자 이메일 추출
            String userEmail = jwtTokenProvider.getUsernameFromToken(token);

            // 이메일을 DTO에 설정하여 해당 사용자의 정보 업데이트
            memberDTO.setEmail(userEmail);

            // 회원 정보 업데이트 로직을 서비스 계층에서 처리
            memberService.updateUser(memberDTO);

            // 성공 메시지 반환
            response.put("message", "회원정보가 성공적으로 수정되었습니다.");
            return ResponseEntity.ok(response); // JSON 응답
        } catch (Exception e) {
            // 오류 발생 시
            response.put("message", "회원정보 수정에 실패했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response); // 오류 응답
        }
    }

    // 구독 상태 변경 메서드
    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, String>> subscribe(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, String> response = new HashMap<>();
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String userEmail = jwtTokenProvider.getUsernameFromToken(token.substring(7));
                memberService.subscribeUser(userEmail);
                subscriptionService.subscribeUser(userEmail);

                response.put("message", "구독 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "인증되지 않은 사용자입니다.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("구독 처리 중 오류 발생: {}", e.getMessage(), e);
            response.put("message", "서버 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 구독 취소 메서드
    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, String>> unsubscribe(@RequestHeader(value = "Authorization", required = false) String token) {
        Map<String, String> response = new HashMap<>();
        try {
            if (token != null && token.startsWith("Bearer ")) {
                String userEmail = jwtTokenProvider.getUsernameFromToken(token.substring(7));
                memberService.unsubscribeUser(userEmail);
                subscriptionService.unsubscribeUser(userEmail);

                response.put("message", "구독 취소 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "인증되지 않은 사용자입니다.");
                return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            log.error("구독 취소 중 오류 발생: {}", e.getMessage(), e);
            response.put("message", "서버 오류가 발생했습니다.");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, String>> deleteAccount(@RequestHeader("Authorization") String token) {
        Map<String, String> response = new HashMap<>();

        try {
            // 토큰이 유효한지 확인하고 'Bearer ' 접두어 제거
            if (token != null && token.startsWith("Bearer ")) {
                String userEmail = jwtTokenProvider.getUsernameFromToken(token.substring(7).trim());

                log.debug("현재 로그인한 사용자 이메일: {}", userEmail);

                // 회원 삭제 처리
                memberService.deleteMemberById(userEmail);
                log.debug("회원 탈퇴 성공: {}", userEmail);

                // 로그아웃 처리 (토큰이 만료되도록 설정하거나 클라이언트에서 삭제)
                // 클라이언트 측에서 토큰을 삭제하거나 서버에서 해당 토큰을 블랙리스트에 추가하는 방식으로 로그아웃 처리 가능
                response.put("message", "회원 탈퇴 및 로그아웃 성공");
                return ResponseEntity.ok(response);
            } else {
                response.put("message", "유효하지 않은 토큰입니다.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } catch (Exception e) {
            log.error("회원 탈퇴 실패: {}", e.getMessage(), e);
            response.put("message", "회원 탈퇴 실패");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
