package net.datasa.yomakase_web.app;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.MemberInfoService;
import net.datasa.yomakase_web.service.MemberService;
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

    // 이메일 중복 확인
    @PostMapping("/emailCheck")
    public ResponseEntity<?> checkEmail(@RequestBody Map<String, String> requestData) {
        String email = requestData.get("email");
        log.debug("받은 데이터 {}",email);
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
    // 사용자 정보 가져오기
    @GetMapping("/me")
    public ResponseEntity<MemberDTO> getMe(@RequestHeader("Authorization") String token) {
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);
        MemberDTO member = memberService.getUserByEmail(userEmail);
        return ResponseEntity.ok(member);
    }
    // 사용자 알러지 정보
    @GetMapping("/allergy")
    public ResponseEntity<Map<String, Boolean>> getMyAllergy(@RequestHeader("Authorization") String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // 'Bearer ' 이후의 실제 JWT 부분만 추출
        } else {
            log.warn("Invalid token format: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);
        Map<String, Boolean> member = memberInfoService.getAllergyByEmail(userEmail);

        log.debug(member.toString());
        return ResponseEntity.ok(member);
    }
    // 사용자 바디 정보
    @GetMapping("/bodyInfo")
    public ResponseEntity<Map<String, Object>> getBodyInfo(@RequestHeader("Authorization") String token) {

        // 토큰에서 'Bearer ' 부분을 제거하고 로깅
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);  // 'Bearer ' 이후의 실제 JWT 부분만 추출
        } else {
            log.warn("Invalid token format: {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }

        String userEmail = jwtTokenProvider.getUsernameFromToken(token);
        MemberDTO member = memberService.getUserByEmail(userEmail);

        // 생년월일을 통해 나이 계산
        LocalDate birthDate = member.getBirthDate(); // MemberDTO에서 생년월일 가져옴
        int age = Period.between(birthDate, LocalDate.now()).getYears();

        // 필요한 정보만 추출
        Map<String, Object> response = new HashMap<>();
        response.put("height", member.getHeight());   // 키
        response.put("weight", member.getWeight());   // 몸무게
        response.put("age", age);                     // 나이 (계산된 값)
        response.put("gender", member.getGender());   // 성별

        log.debug("데이터 확인 : {}", response);

        return ResponseEntity.ok(response);
    }
}
