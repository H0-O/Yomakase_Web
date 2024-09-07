package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.AllergyEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.SubscriptionEntity;
import net.datasa.yomakase_web.domain.entity.UserBodyInfoEntity;
import net.datasa.yomakase_web.repository.AllergyRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.SubscriptionRepository;
import net.datasa.yomakase_web.repository.UserBodyInfoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final AllergyRepository allergyRepository;
    private final UserBodyInfoRepository userBodyInfoRepository;

    // ID를 조회하여 사용할 수 있는지 확인하는 메소드
    public boolean find(String searchId) {
        return !memberRepository.existsById(searchId);
    }

    // 회원 정보 저장
    public void saveMember(MemberDTO memberDTO) {
        log.info("memberDTO service: {}", memberDTO);

        // 1. MemberEntity 저장
        MemberEntity member = MemberEntity.builder()
                .id(memberDTO.getEmail())
                .pw(passwordEncoder.encode(memberDTO.getPassword()))  // 비밀번호 암호화
                .name(memberDTO.getNickname())
                .gen(memberDTO.getGender())
                .birthDate(memberDTO.getBirthdate())
                .userRole(memberDTO.getUserRole() != null ? memberDTO.getUserRole() : "ROLE_USER")
                .enabled(true)
                .build();
        memberRepository.save(member);

        // 2. AllergyEntity 저장
        AllergyEntity allergyEntity = AllergyEntity.builder()
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
        allergyRepository.save(allergyEntity);

        // 3. UserBodyInfoEntity 저장
        UserBodyInfoEntity userBodyInfoEntity = UserBodyInfoEntity.builder()
                .member(member)
                .height(memberDTO.getHeight())
                .weight(memberDTO.getWeight())
                .build();
        userBodyInfoRepository.save(userBodyInfoEntity);
    }

    // 로그인 인증 처리
    public boolean authenticate(String email, String password) {
        Optional<MemberEntity> member = memberRepository.findById(email);
        return member.isPresent() && passwordEncoder.matches(password, member.get().getPw());
    }

    // 구독 저장
    public void subscribeMember(String email) {
        Optional<MemberEntity> memberOpt = memberRepository.findById(email);
        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();

            // 구독 시작일과 종료일 설정
            LocalDate startDate = LocalDate.now();
            LocalDate endDate = startDate.plusMonths(1);

            // 구독 정보 저장
            SubscriptionEntity subscription = SubscriptionEntity.builder()
                    .member(member)
                    .startDate(startDate)
                    .endDate(endDate)
                    .build();
            subscriptionRepository.save(subscription);

            // 회원의 역할을 ROLE_SUBSCRIBER로 변경
            member.setUserRole("ROLE_SUBSCRIBER");
            memberRepository.save(member);
        }
    }

    // 회원 정보 수정
    public void update(MemberDTO dto) {
        Optional<MemberEntity> memberOpt = memberRepository.findById(dto.getEmail());
        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();
            member.setName(dto.getNickname());
            member.setBirthDate(dto.getBirthdate());
            member.setGen(dto.getGender());
            memberRepository.save(member);
        }
    }

    // 회원 정보 조회
    public MemberDTO getMember(String email) {
        Optional<MemberEntity> memberOpt = memberRepository.findById(email);
        if (memberOpt.isPresent()) {
            MemberEntity member = memberOpt.get();
            return MemberDTO.builder()
                    .email(member.getId())
                    .nickname(member.getName())
                    .birthdate(member.getBirthDate())
                    .gender(member.getGen())
                    .build();
        }
        return null;
    }
}
