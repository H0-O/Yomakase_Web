package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "subscription")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private int subscriptionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member;  // 회원 정보

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;  // 구독 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;  // 구독 종료일
}
