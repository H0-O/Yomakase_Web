package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.AllergyEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 알레르기 관련 Repository
 * 9/3 최초 작성 (by 호영)
 */
@Repository
public interface AllergyRepository extends JpaRepository<AllergyEntity, Integer> {
    Optional<AllergyEntity> findByMember(MemberEntity member);  // MemberEntity로 AllergyEntity를 찾는 메서드
}
