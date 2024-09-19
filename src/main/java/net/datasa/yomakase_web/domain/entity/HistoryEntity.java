package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.datasa.yomakase_web.domain.compositeK.HistoryId;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@IdClass(HistoryId.class)
public class HistoryEntity implements Serializable {

    @Id
    @Column(name = "ingredient_name", nullable = false)
    private String ingredientName;

    @Id
    @Column(name = "member_num", nullable = false)
    private int memberNum;

    @Id
    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "type", nullable = false)
    private String type; // 'c' 소비, 'b' 버림

    // stock 테이블과의 복합 외래 키 설정
    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "ingredient_name", referencedColumnName = "ingredient_name", insertable = false, updatable = false),
            @JoinColumn(name = "member_num", referencedColumnName = "member_num", insertable = false, updatable = false)
    })
    private StockEntity stockEntity;
}