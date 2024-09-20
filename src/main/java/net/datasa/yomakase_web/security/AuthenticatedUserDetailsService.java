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
public class AuthenticatedUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    // 사용자 이름(username)을 기반으로 사용자 정보를 로드하는 메서드
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 전달받은 사용자 아이디(username)로 DB에서 사용자 정보 조회
        // 아이디가 없으면 예외 발생
        MemberEntity entity = memberRepository.findById(username)  // findByEmail로 수정
                .orElseThrow(() -> new UsernameNotFoundException("아이디가 없습니다."));

        log.debug("조회된 사용자 정보: {}", entity);

        // 조회된 사용자 정보를 기반으로 AuthenticatedUser 객체를 생성
        AuthenticatedUser userDetails = AuthenticatedUser.builder()
                .id(entity.getId())  // 실제 DB에서 가져온 아이디로 설정
                .password(entity.getPw())  // 비밀번호 설정
                .name(entity.getName())
                .roleName(entity.getUserRole())  // 역할(권한) 설정
                .enabled(entity.getEnabled())  // 계정 활성화 여부 설정
                .memberNum(entity.getMemberNum())  // memberNum 추가
                .build();

        if (userDetails.getMemberNum() == null) {
            throw new IllegalStateException("Member number is null");
        }

        log.debug("조회된 memberNum: {}", entity.getMemberNum());

        log.debug("생성된 인증 정보 : {}", userDetails);
        return userDetails;
    }
}
