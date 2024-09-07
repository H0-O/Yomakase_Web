package net.datasa.yomakase_web.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDTO {

	private String ingredientName;
    private int memberNum;
    private boolean isHaving;
    private LocalDate useByDate;
    private LocalDateTime updateDate;
}
