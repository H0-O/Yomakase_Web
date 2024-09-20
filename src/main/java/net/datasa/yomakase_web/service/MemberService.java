package net.datasa.yomakase_web.service;

import jakarta.persistence.EntityNotFoundException;
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
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
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

    // 암호화
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void saveMember(MemberDTO memberDTO) {
        // 1. MemberEntity 저장
        MemberEntity member = MemberEntity.builder()
                .id(memberDTO.getEmail())
                .pw(passwordEncoder.encode(memberDTO.getPassword()))
                .name(memberDTO.getNickname())
                .gen(memberDTO.getGender())
                .birthDate(memberDTO.getBirthDate())
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

    // 이메일을 통해 사용자 정보를 가져오는 메서드
    public MemberDTO getUserByEmail(String email) {
        MemberEntity memberEntity = memberRepository.findById(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // MemberEntity를 memberDTO로 변환
        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setEmail(memberEntity.getId());
        memberDTO.setNickname(memberEntity.getName());
        memberDTO.setBirthDate(memberEntity.getBirthDate());
        memberDTO.setGender(memberEntity.getGen());
        memberDTO.setEggs(memberEntity.getAllergy().isEggs());
        memberDTO.setMilk(memberEntity.getAllergy().isMilk());
        memberDTO.setBuckwheat(memberEntity.getAllergy().isBuckwheat());
        memberDTO.setPeanut(memberEntity.getAllergy().isPeanut());
        memberDTO.setSoybean(memberEntity.getAllergy().isSoybean());
        memberDTO.setWheat(memberEntity.getAllergy().isWheat());
        memberDTO.setMackerel(memberEntity.getAllergy().isMackerel());
        memberDTO.setCrab(memberEntity.getAllergy().isCrab());
        memberDTO.setShrimp(memberEntity.getAllergy().isShrimp());
        memberDTO.setPork(memberEntity.getAllergy().isPork());
        memberDTO.setPeach(memberEntity.getAllergy().isPeach());
        memberDTO.setTomato(memberEntity.getAllergy().isTomato());
        memberDTO.setWalnuts(memberEntity.getAllergy().isWalnuts());
        memberDTO.setChicken(memberEntity.getAllergy().isChicken());
        memberDTO.setBeef(memberEntity.getAllergy().isBeef());
        memberDTO.setSquid(memberEntity.getAllergy().isSquid());
        memberDTO.setShellfish(memberEntity.getAllergy().isShellfish());
        memberDTO.setPineNut(memberEntity.getAllergy().isPineNut());
        memberDTO.setHeight(memberEntity.getBodyInfo().getHeight());
        memberDTO.setWeight(memberEntity.getBodyInfo().getWeight());


    // 필요한 필드만 선택적으로 포함
        return memberDTO;
}

    public void updateUser(MemberDTO memberDTO) {
        // 기존 회원을 ID(email)로 조회
        MemberEntity entity = memberRepository.findById(memberDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("없는 ID"));

        // 비밀번호가 비어있지 않으면 비밀번호를 수정
        if (memberDTO.getPassword() != null && !memberDTO.getPassword().isEmpty()) {
            entity.setPw(bCryptPasswordEncoder.encode(memberDTO.getPassword()));
        }

        // 성별과 생년월일 업데이트
        entity.setGen(memberDTO.getGender());
        entity.setBirthDate(memberDTO.getBirthDate());

        // 회원 정보를 업데이트
        memberRepository.save(entity);

        // 기존의 UserBodyInfoEntity를 가져와 업데이트 또는 새로 생성
        UserBodyInfoEntity bodyInfo = userBodyInfoRepository.findByMember(entity)
                .orElseGet(() -> UserBodyInfoEntity.builder().member(entity).build());  // 없으면 새로 생성
        bodyInfo.setHeight(memberDTO.getHeight());
        bodyInfo.setWeight(memberDTO.getWeight());
        userBodyInfoRepository.save(bodyInfo);

        // 기존의 AllergyEntity를 가져와 업데이트 또는 새로 생성
        AllergyEntity allergy = allergyRepository.findByMember(entity)
                .orElseGet(() -> AllergyEntity.builder().member(entity).build());  // 없으면 새로 생성
        allergy.setEggs(memberDTO.isEggs());
        allergy.setMilk(memberDTO.isMilk());
        allergy.setBuckwheat(memberDTO.isBuckwheat());
        allergy.setPeanut(memberDTO.isPeanut());
        allergy.setSoybean(memberDTO.isSoybean());
        allergy.setWheat(memberDTO.isWheat());
        allergy.setMackerel(memberDTO.isMackerel());
        allergy.setCrab(memberDTO.isCrab());
        allergy.setShrimp(memberDTO.isShrimp());
        allergy.setPork(memberDTO.isPork());
        allergy.setPeach(memberDTO.isPeach());
        allergy.setTomato(memberDTO.isTomato());
        allergy.setWalnuts(memberDTO.isWalnuts());
        allergy.setChicken(memberDTO.isChicken());
        allergy.setBeef(memberDTO.isBeef());
        allergy.setSquid(memberDTO.isSquid());
        allergy.setShellfish(memberDTO.isShellfish());
        allergy.setPineNut(memberDTO.isPineNut());
        allergyRepository.save(allergy);
    }

}
