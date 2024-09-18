package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.UserBodyInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserBodyInfoRepository extends JpaRepository<UserBodyInfoEntity, Integer> {
    Optional<UserBodyInfoEntity> findByMember(MemberEntity member);  // MemberEntity로 UserBodyInfoEntity를 찾는 메서드

}
