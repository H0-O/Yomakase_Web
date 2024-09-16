package net.datasa.yomakase_web.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${app.jwtExpirationInMs}")
    private long validityInMilliseconds; // 토큰 유효 시간 (밀리초)

    private SecretKey secretKey;

    // SecretKey 생성
    @Value("${app.jwtSecret}")
    private String secret;

    @PostConstruct
    public void init() {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());  // 고정된 키를 사용
    }

    // JWT 토큰 생성
    public String generateToken(MemberEntity member) {
        Date now = new Date();
        Date validity = new Date(now.getTime() + validityInMilliseconds);

        // 토큰에 추가할 클레임 설정
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberNum", member.getMemberNum());  // 예시로 memberNum을 넣음
        // 토큰 생성
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(member.getId()) // subject는 사용자의 ID (email)로 설정
                .setIssuedAt(now) // 토큰 발행 시간
                .setExpiration(validity) // 토큰 만료 시간
                .signWith(secretKey, SignatureAlgorithm.HS256) // 서명 알고리즘과 비밀키 설정
                .compact();
    }

    // JWT 토큰에서 사용자 ID (email) 추출
    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject(); // subject에서 사용자 ID(email)을 가져옴
    }

    // JWT 토큰에서 memberNum 추출
    public Integer getMemberNumFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("memberNum", Integer.class); // 'memberNum' 클레임에서 Integer 타입으로 추출
    }

    // JWT 토큰의 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey) // 서명 검증에 사용할 비밀키 설정
                    .build()
                    .parseClaimsJws(token); // 토큰 서명 및 유효성 검증
            return true; // 유효한 토큰이면 true 반환
        } catch (Exception e) {
            return false; // 유효하지 않은 토큰이면 false 반환
        }
    }
}
