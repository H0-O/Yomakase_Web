package net.datasa.yomakase_web.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "complaint_num")
    private int complaintNum;  // 문의사항 고유 번호

    @ManyToOne
    @JoinColumn(name = "member_num", nullable = false)
    private MemberEntity member;  // 회원 고유 번호 (FK)

    @Column(name = "title", length = 200, nullable = false)
    private String title;  // 제목

    @Column(name = "category", length = 10, nullable = false)
    private String category;  // 분류

    @Column(name = "contents", columnDefinition = "MEDIUMTEXT", nullable = false)
    private String contents;  // 내용

    @Column(name = "input_date", nullable = false)
    private LocalDateTime inputDate;  // 입력 날짜
}
