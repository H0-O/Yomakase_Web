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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
