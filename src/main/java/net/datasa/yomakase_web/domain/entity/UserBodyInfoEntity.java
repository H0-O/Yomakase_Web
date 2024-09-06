package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_body_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBodyInfoEntity {

    @Id
    private int memberNum;

    @MapsId  // member_num을 외래 키이자 기본 키로 설정
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_num", referencedColumnName = "member_num")
    private MemberEntity member;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "height")
    private Integer height;
}
