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
public class HistoryDTO {
    private Integer historyId; // 자동 증가하는 기본 키
    private String ingredientName; // 재료 이름
    private Integer memberNum; // 회원 번호
    private LocalDate date; // 날짜
    private String type; // 'c' -> 소비, 'b' -> 버림
}
