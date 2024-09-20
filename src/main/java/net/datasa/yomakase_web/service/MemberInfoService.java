package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.MemberDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.repository.AllergyRepository;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.UserBodyInfoRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MemberInfoService {
    private final MemberRepository memberRepository;
    private final AllergyRepository allergyRepository;
    private final UserBodyInfoRepository userBodyInfoRepository;
    private final PasswordEncoder passwordEncoder;

    public MemberDTO getAllergyByEmail(Object identifier) {
        MemberEntity memberEntity;

        // identifier가 Integer인 경우 (memberNum 직접 전달)
        if (identifier instanceof Integer) {
            memberEntity = memberRepository.findByMemberNum((Integer) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
        // identifier가 String인 경우 (이메일로 memberNum 조회)
        else if (identifier instanceof String) {
            memberEntity = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            throw new IllegalArgumentException("Invalid identifier type");
        }

        // MemberEntity를 memberDTO로 변환
        MemberDTO memberDTO = new MemberDTO();

        // 알러지 정보를 Map에 저장
        Map<String, Boolean> allergies = new HashMap<>();
        allergies.put("Eggs", memberEntity.getAllergy().isEggs());
        allergies.put("Milk", memberEntity.getAllergy().isMilk());
        allergies.put("Buckwheat", memberEntity.getAllergy().isBuckwheat());
        allergies.put("Peanut", memberEntity.getAllergy().isPeanut());
        allergies.put("Soybean", memberEntity.getAllergy().isSoybean());
        allergies.put("Wheat", memberEntity.getAllergy().isWheat());
        allergies.put("Mackerel", memberEntity.getAllergy().isMackerel());
        allergies.put("Crab", memberEntity.getAllergy().isCrab());
        allergies.put("Shrimp", memberEntity.getAllergy().isShrimp());
        allergies.put("Pork", memberEntity.getAllergy().isPork());
        allergies.put("Peach", memberEntity.getAllergy().isPeach());
        allergies.put("Tomato", memberEntity.getAllergy().isTomato());
        allergies.put("Walnuts", memberEntity.getAllergy().isWalnuts());
        allergies.put("Chicken", memberEntity.getAllergy().isChicken());
        allergies.put("Beef", memberEntity.getAllergy().isBeef());
        allergies.put("Squid", memberEntity.getAllergy().isSquid());
        allergies.put("Shellfish", memberEntity.getAllergy().isShellfish());
        allergies.put("Pine Nut", memberEntity.getAllergy().isPineNut());

        // 알러지 정보를 MemberDTO에 설정
        memberDTO.setAllergies(allergies);

        return memberDTO;
    }

}
