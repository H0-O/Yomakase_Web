package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
    private Integer memberNum;

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
    @ToString.Exclude  // Lombok의 toString()에서 제외
    private AllergyEntity allergy;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Lombok의 toString()에서 제외
    private UserBodyInfoEntity bodyInfo;

    @Column(nullable = false, length = 30)
    private String userRole = "ROLE_USER";

    @Column(nullable = false)
    private Boolean enabled = true;

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SubscriptionEntity> subscriptions;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SavedRecipeEntity> savedRecipes = new ArrayList<>();  // 연관된 레시피들

    @Override
    public String toString() {
        return "MemberEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                // 중요한 필드만 포함하고, SavedRecipeEntity 등 참조 엔티티는 제외
                '}';
    }
}