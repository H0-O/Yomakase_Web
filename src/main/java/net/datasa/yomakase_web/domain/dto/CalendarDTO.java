package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalendarDTO {
    private int memberNum;
    private LocalDate inputDate;
    private String bName;
    private String lName;
    private String dName;
}
