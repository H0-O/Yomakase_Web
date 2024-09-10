package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "member")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_num")
    private int memberNum;

    @Column(nullable = false, unique = true, length = 50)
    private String id;  // 이메일

    @Column(nullable = false, length = 100)
    private String pw;  // 비밀번호

    @Column(nullable = false, unique = true, length = 100)
    private String name;  // 닉네임

    @Column(nullable = false)
    private char gen;  // 성별

    @Column(nullable = false)
    private LocalDate birthDate;  // 생년월일

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private AllergyEntity allergy;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private UserBodyInfoEntity bodyInfo;

    @Column(nullable = false, length = 30)
    private String userRole = "ROLE_USER";

    @Column(nullable = false)
    private Boolean enabled = true;
}