package net.datasa.yomakase_web.domain.compositeK;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class HistoryId implements Serializable {
    private String ingredientName;
    private int memberNum;
    private LocalDate date;
}