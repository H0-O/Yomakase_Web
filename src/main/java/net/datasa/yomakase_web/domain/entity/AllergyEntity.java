package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "allergy")
public class AllergyEntity {

    @Id
    @Column(name = "member_num")  // 기본 키로 사용할 외래 키
    private int memberNum;

    @OneToOne
    @MapsId  // 외래 키를 기본 키로 사용
    @JoinColumn(name = "member_num")  // 'member_num'이 외래 키이자 기본 키로 사용됨
    private MemberEntity member;

    @Column(name = "eggs", nullable = false)
    private boolean eggs;

    @Column(name = "milk", nullable = false)
    private boolean milk;

    @Column(name = "buckwheat", nullable = false)
    private boolean buckwheat;

    @Column(name = "peanut", nullable = false)
    private boolean peanut;

    @Column(name = "soybean", nullable = false)
    private boolean soybean;

    @Column(name = "wheat", nullable = false)
    private boolean wheat;

    @Column(name = "mackerel", nullable = false)
    private boolean mackerel;

    @Column(name = "crab", nullable = false)
    private boolean crab;

    @Column(name = "shrimp", nullable = false)
    private boolean shrimp;

    @Column(name = "pork", nullable = false)
    private boolean pork;

    @Column(name = "peach", nullable = false)
    private boolean peach;

    @Column(name = "tomato", nullable = false)
    private boolean tomato;

    @Column(name = "walnuts", nullable = false)
    private boolean walnuts;

    @Column(name = "chicken", nullable = false)
    private boolean chicken;

    @Column(name = "beef", nullable = false)
    private boolean beef;

    @Column(name = "squid", nullable = false)
    private boolean squid;

    @Column(name = "shellfish", nullable = false)
    private boolean shellfish;

    @Column(name = "pine_nut", nullable = false)
    private boolean pineNut;
}
