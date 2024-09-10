package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    @Column(nullable = false, unique = true, length = 50, name = "id")
    private String id; // 이메일 (SQL에서 id로 지정됨)

    @Column(nullable = false, length = 100, name = "pw")
    private String pw; // 비밀번호

    @Column(nullable = false, unique = true, length = 100, name = "name")
    private String name; // 닉네임

    @Column(nullable = false, name = "gen")
    private char gen; // 성별

    @Column(nullable = false, name = "birth_date")
    private LocalDate birthDate; // 생년월일

    @Column(nullable = false, length = 30, name = "user_role")
    private String userRole = "ROLE_USER"; // 사용자 권한, 기본값은 ROLE_USER

    // 회원과 구독의 1:1 관계 설정
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private SubscriptionEntity subscription;

    @Column(name="enabled", nullable = false)
    private Boolean enabled = true;

    // 회원과 알레르기 정보의 1:1 관계 설정
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private AllergyEntity allergy;

    // 회원과 신체 정보의 1:1 관계 설정
    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private UserBodyInfoEntity bodyInfo;

    @Override
    public String toString() {
        return "MemberEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                // allergy 필드를 직접적으로 참조하지 않도록 수정
                '}';
    }
}
