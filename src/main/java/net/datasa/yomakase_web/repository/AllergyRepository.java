package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.AllergyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 알레르기 관련 Repository
 * 9/3 최초 작성 (by 호영)
 */
@Repository
public interface AllergyRepository extends JpaRepository<AllergyEntity, Integer> {
}
