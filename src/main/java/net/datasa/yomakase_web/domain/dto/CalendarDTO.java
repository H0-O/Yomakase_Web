package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CalendarDTO {
    private String id;
    private int memberNum;
    private LocalDate inputDate;
    private String bName;  // 아침 식사 이름
    private int bKcal;  // 아침 칼로리
    private String lName;  // 점심 식사 이름
    private int lKcal;  // 점심 칼로리
    private String dName;  // 저녁 식사 이름
    private int dKcal;  // 저녁 칼로리
    private String tooMuch;  // 과한 영양소
    private String lack;  // 부족한 영양소
    private String recom;  // 추천 음식
    private int totalKcal;  // 총 칼로리
    private int score;  // 영양 점수
}