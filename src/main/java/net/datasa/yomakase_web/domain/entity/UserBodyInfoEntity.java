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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @MapsId
    @JoinColumn(name = "member_num")
    private MemberEntity member;

    private Integer weight;
    private Integer height;
}