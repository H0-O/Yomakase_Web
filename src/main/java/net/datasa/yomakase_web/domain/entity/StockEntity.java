package net.datasa.yomakase_web.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.datasa.yomakase_web.domain.compositeK.MemberStock;

@Data
@Builder
@Entity
@Table(name = "stock")
@NoArgsConstructor
@AllArgsConstructor
@IdClass(MemberStock.class)
public class StockEntity {
	
    @Id
    @Column(name = "ingredient_name", nullable = false, length = 700)
    private String ingredientName;

    @Id
    @Column(name = "member_num", nullable = false)
    private int memberNum;

    @Column(name = "is_having", nullable = false)
    private boolean isHaving = true;

    @Column(name = "use_by_date", nullable = false)
    private LocalDate useByDate;

    @Column(name = "update_date", nullable = false, updatable = false, insertable = false,
            columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP")
    private LocalDateTime updateDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", insertable = false, updatable = false)
    private MemberEntity member;



}
