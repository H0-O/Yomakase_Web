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
    private String ingredientName;
    private int memberNum;
    private LocalDate date;
    private String type; // 'c' 소비, 'b' 버림
}
