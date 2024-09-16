package net.datasa.yomakase_web.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Long> {
    List<ComplaintEntity> findByMember(MemberEntity member);
}
