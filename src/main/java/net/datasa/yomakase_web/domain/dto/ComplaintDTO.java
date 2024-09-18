package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintDTO {
    private int memberNum;  // 회원 고유 번호
    private String title;  // 제목
    private String category;  // 분류
    private String contents;  // 내용
    private LocalDate inputDate;  // 입력 날짜
}
