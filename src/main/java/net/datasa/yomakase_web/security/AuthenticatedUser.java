package net.datasa.yomakase_web.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

// 사용자 인증 정보 객체
public class AuthenticatedUser implements UserDetails {
    // 인증 관련 정보
    String id;           // 필수
    String password;     // 필수
    boolean enabled;     // 필수
    String roleName;     // 필수. 내가 정하는 이름(예를 들어, 권한 줄 때 구분하기 위해)
    // 선택 부분은 사용하고 싶은 객체를 선언해서 사용할 수 있고,

    // 계정이 만료되지 않았는지 여부를 반환(리턴
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    // 자격 증명이 만료되지 않았는지 여부를 반환(리턴)
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // 계정이 잠겨있지 않은지 여부를 반환(리턴)
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    // 계정이 활성화되어 있는지 여부를 반환(정상 아이디인지 여부)
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    // 사용자의 권한(역할)을 반환(리턴)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority(roleName));
    }

    // 사용자의 비밀번호를 반환(리턴)
    @Override
    public String getPassword() {
        return password;
    }

    // 사용자의 이름을 반환(리턴). 여기서는 빈 문자열을 반환(리턴)
    @Override
    public String getUsername() {
        return id;
    }
}
