package net.datasa.yomakase_web.app;

import lombok.RequiredArgsConstructor;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import net.datasa.yomakase_web.service.MemberService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final MemberService memberService;  // 사용자 조회 로직을 처리하는 서비스
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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
    public ResponseEntity<MemberDTO> getMe(@RequestHeader("Authorization") String token) {
        String userEmail = jwtTokenProvider.getUsernameFromToken(token);
        MemberDTO member = memberService.getUserByEmail(userEmail);
        return ResponseEntity.ok(member);
    }
}
