package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "allergy")
public class AllergyEntity {

    @Id
    @Column(name = "member_num")
    private int memberNum;

    @OneToOne
    @MapsId
    @JoinColumn(name = "member_num")
    private MemberEntity member;

    private boolean eggs;
    private boolean milk;
    private boolean buckwheat;
    private boolean peanut;
    private boolean soybean;
    private boolean wheat;
    private boolean mackerel;
    private boolean crab;
    private boolean shrimp;
    private boolean pork;
    private boolean peach;
    private boolean tomato;
    private boolean walnuts;
    private boolean chicken;
    private boolean beef;
    private boolean squid;
    private boolean shellfish;
    private boolean pineNut;
}
