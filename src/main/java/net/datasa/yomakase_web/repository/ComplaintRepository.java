package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.ComplaintEntity;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<ComplaintEntity, Integer> {
    List<ComplaintEntity> findByMember(MemberEntity member);
}
