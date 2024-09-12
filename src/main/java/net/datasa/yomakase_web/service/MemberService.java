package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.AllergyEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.UserBodyInfoEntity;
import net.datasa.yomakase_web.repository.AllergyRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.UserBodyInfoRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final AllergyRepository allergyRepository;
    private final UserBodyInfoRepository userBodyInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveMember(MemberDTO memberDTO) {
        // 1. MemberEntity 저장
        MemberEntity member = MemberEntity.builder()
                .id(memberDTO.getEmail())
                .pw(passwordEncoder.encode(memberDTO.getPassword()))
                .name(memberDTO.getNickname())
                .gen(memberDTO.getGender())
                .birthDate(memberDTO.getBirthdate())
                .userRole(memberDTO.getUserRole() != null ? memberDTO.getUserRole() : "ROLE_USER")
                .enabled(true)
                .build();
        memberRepository.save(member);

        // 2. AllergyEntity 저장
        AllergyEntity allergy = AllergyEntity.builder()
                .member(member)
                .eggs(memberDTO.isEggs())
                .milk(memberDTO.isMilk())
                .buckwheat(memberDTO.isBuckwheat())
                .peanut(memberDTO.isPeanut())
                .soybean(memberDTO.isSoybean())
                .wheat(memberDTO.isWheat())
                .mackerel(memberDTO.isMackerel())
                .crab(memberDTO.isCrab())
                .shrimp(memberDTO.isShrimp())
                .pork(memberDTO.isPork())
                .peach(memberDTO.isPeach())
                .tomato(memberDTO.isTomato())
                .walnuts(memberDTO.isWalnuts())
                .chicken(memberDTO.isChicken())
                .beef(memberDTO.isBeef())
                .squid(memberDTO.isSquid())
                .shellfish(memberDTO.isShellfish())
                .pineNut(memberDTO.isPineNut())
                .build();
        allergyRepository.save(allergy);

        // 3. UserBodyInfoEntity 저장
        UserBodyInfoEntity bodyInfo = UserBodyInfoEntity.builder()
                .member(member)
                .height(memberDTO.getHeight())
                .weight(memberDTO.getWeight())
                .build();
        userBodyInfoRepository.save(bodyInfo);
    }

    // ID 중복 확인
    public boolean find(String searchId) {
        return !memberRepository.existsById(searchId);  // 존재하지 않으면 true 반환
    }

    // ID 구독상태로 변경
    public void subscribeUser(String userEmail) throws Exception {
        MemberEntity member = memberRepository.findById(userEmail)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        member.setUserRole("ROLE_SUBSCRIBER"); // 구독 상태로 변경
        memberRepository.save(member);
    }

    // ID 미구독 상태로 변경
    public void unsubscribeUser(String userEmail) throws Exception {
        // 이메일을 기반으로 사용자를 찾아 구독 취소 처리
        MemberEntity member = memberRepository.findById(userEmail)
                .orElseThrow(() -> new Exception("사용자를 찾을 수 없습니다."));

        member.setUserRole("ROLE_USER"); // 역할을 ROLE_USER로 변경
        memberRepository.save(member); // 변경된 사용자 정보 저장
    }
    /**
     * メールアドレスでユーザーを探すメソッド
     * @param email メールアドレス
     * @return MemberEntity ユーザーエンティティ
     * @throws UsernameNotFoundException ユーザーが見つからない場合に例外をスロー
     */
    public MemberEntity findByEmail(String email) {
        // メールアドレスでDBからユーザーを探す。見つからない場合は例外をスロー
        return memberRepository.findById(email)
                .orElseThrow(() -> new UsernameNotFoundException("ユーザーが見つかりません: " + email));
    }

    // 회원 삭제 서비스
    public void deleteMemberById(String username) {
        // 주어진 사용자 ID로 회원 정보 삭제
        Optional<MemberEntity> member = memberRepository.findById(username);
        if (member.isPresent()) {
            memberRepository.delete(member.get());
            log.debug("회원 정보 삭제 완료: {}", username);
        } else {
            log.error("회원 정보를 찾을 수 없음: {}", username);
            throw new RuntimeException("회원 정보를 찾을 수 없습니다.");
        }
    }
}
