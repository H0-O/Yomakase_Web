package net.datasa.yomakase_web.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service

// AuthenticatedUserDetailsService 클래스는 UserDetailsService 인터페이스를 구현하여
// 사용자 이름으로 사용자 정보를 로드하는 역할
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 사용자 이름(username)을 기반으로 사용자 정보를 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 전달받은 사용자 아이디(username)로 DB에서 사용자 정보 조회
        // 아이디가 없으면 예외
        MemberEntity entity = memberRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("아이디가 없습니다."));

        log.debug("조회정보:{}", entity);

        // 아이디가 있으면 조회된 정보로 UserDetails 객체를 생성해서 리턴
        AuthenticatedUser userDetails = AuthenticatedUser.builder()
                // 여기서는 일단, 임의 값을 넣음. 실제론 DB에서 가져옴
                .id(username)
                .password(entity.getPw())
                .roleName(entity.getUserRole())
                .enabled(entity.getEnabled())
                .build();

        log.debug("인증정보 : {}", userDetails);
        return userDetails;
        // Security가 이 정보를 받아서 password가 맞는지 확인해 줌
    }
}
