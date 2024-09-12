package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CalendarDTO {
    private LocalDate inputDate;
    private String bName;
    private String lName;
    private String dName;
}
