package net.datasa.yomakase_web.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtExpirationInMs}")
    private long validityInMilliseconds; // 24시간

    private SecretKey secretKey;

    // 의존성 주입 후, SecretKey로 변환
    @PostConstruct
    public void init() {
        // 안전한 256비트 키 생성
        this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    // JWT 토큰 생성
    public String generateToken(AuthenticatedUser user) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(secretKey, SignatureAlgorithm.HS256) // SecretKey와 알고리즘 함께 사용
                .compact();
    }

    // JWT 토큰에서 사용자 이름 추출
    public String getUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey) // secretKey를 사용하여 서명 검증
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // JWT 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // secretKey를 사용하여 서명 검증
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
