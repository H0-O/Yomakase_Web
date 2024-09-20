package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 회원 관련 Repository
 * 9/3 최초 작성 (by 호영)
 */
@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {
    // 이메일로 회원 정보 조회
    Optional<MemberEntity> findById(String id);

    // memberNum을 기준으로 MemberEntity 조회
    Optional<MemberEntity> findByMemberNum(Integer memberNum);

    // 이메일을 기준으로 회원이 존재하는지 확인하는 메서드
    boolean existsById(String email);
}
