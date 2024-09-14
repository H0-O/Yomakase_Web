package net.datasa.yomakase_web.app;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.security.JwtTokenProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    // 로그인 요청을 처리하는 메서드
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequest) {

        // 데이터베이스에서 사용자 정보 조회 (이미 AuthenticatedUserDetailsService에서 이 작업을 처리)
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getId(), loginRequest.getPassword());

        // 인증을 시도 (AuthenticationManager가 비밀번호 비교 포함)
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            AuthenticatedUser authenticatedUser = (AuthenticatedUser) authentication.getPrincipal();
            String token = jwtTokenProvider.generateToken(authenticatedUser); // JWT 토큰 생성


            LoginResponseDTO response = new LoginResponseDTO(authenticatedUser.getUsername(), "로그인 성공", token);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("로그인 실패: {}", e.getMessage());
            return ResponseEntity.status(401).body(new LoginResponseDTO(null, "로그인 실패",null));
        }
    }
}