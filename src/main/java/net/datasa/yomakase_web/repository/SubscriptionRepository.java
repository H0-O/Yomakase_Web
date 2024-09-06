package net.datasa.yomakase_web.repository;

import net.datasa.yomakase_web.domain.entity.SubscriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionEntity, Integer> {
    SubscriptionEntity findByMember_MemberNum(int memberNum);
}