package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import net.datasa.yomakase_web.domain.compositeK.CalendarId;

import java.time.LocalDate;

@Entity
@Table(name = "cal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@IdClass(CalendarId.class)
public class CalendarEntity {

    @Id
    @Column(name = "input_date", nullable = false)
    private LocalDate inputDate;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_num", nullable = false)
    private int memberNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_num", insertable = false, updatable = false)
    private MemberEntity member;

    @Column(name = "b_name", nullable = true)
    private String bName;

    @Column(name = "b_kcal", nullable = true)
    private int bKcal;

    @Column(name = "l_name", nullable = true)
    private String lName;

    @Column(name = "l_kcal", nullable = true)
    private int lKcal;

    @Column(name = "d_name", nullable = true)
    private String dName;

    @Column(name = "d_kcal", nullable = true)
    private int dKcal;

    @Column(name = "over", nullable = true)
    private String over;

    @Column(name = "lack", nullable = true)
    private String lack;

    @Column(name = "recom", nullable = true)
    private String recom;

    @Column(name = "total_kcal", nullable = true)
    private int totalKcal;

    @Column(name = "score", nullable = true)
    private int score;

}
