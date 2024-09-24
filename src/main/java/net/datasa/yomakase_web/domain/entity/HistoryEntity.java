package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "history")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Integer historyId; // 자동 증가하는 기본 키

    @Column(name = "ingredient_name", nullable = false, length = 700)
    private String ingredientName; // 재료 이름

    @Column(name = "member_num", nullable = false)
    private Integer memberNum; // 회원 번호

    @Column(name = "date", nullable = false)
    private LocalDate date; // 날짜

    @Column(name = "type", nullable = false, length = 10)
    private String type; // 'c' -> 소비, 'b' -> 버림

    // 필요한 경우, Stock 엔티티와 관계 설정 (Optional)
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ingredient_name", referencedColumnName = "ingredient_name", insertable = false, updatable = false),
            @JoinColumn(name = "member_num", referencedColumnName = "member_num", insertable = false, updatable = false)
    })
    private StockEntity stockEntity;
}