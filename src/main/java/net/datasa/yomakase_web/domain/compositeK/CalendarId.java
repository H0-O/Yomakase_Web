package net.datasa.yomakase_web.domain.compositeK;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * CalendarEntity 클래스의 복합키
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CalendarId implements Serializable {

    private LocalDate inputDate;
    private int memberNum;
}
