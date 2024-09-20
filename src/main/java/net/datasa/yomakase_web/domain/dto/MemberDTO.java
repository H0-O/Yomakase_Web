package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberDTO {

//    @JsonProperty("id")
    private String email;

//    @JsonProperty("password")
    private String password;

//    @JsonProperty("name")
    private String nickname;

//    @JsonProperty("gen")
    private char gender;

//    @JsonProperty("birth_date")
    private LocalDate birthDate;

//    @JsonProperty("height")
    private Integer height;  // 키

//    @JsonProperty("weight")
    private Integer weight;  // 몸무게

//    @JsonProperty("enabled")
    private boolean enabled;

    // 알레르기 정보 (boolean 타입)
//    @JsonProperty("eggs")
    private boolean eggs;

//    @JsonProperty("milk")
    private boolean milk;

//    @JsonProperty("buckwheat")
    private boolean buckwheat;

//    @JsonProperty("peanut")
    private boolean peanut;

//    @JsonProperty("soybean")
    private boolean soybean;

//    @JsonProperty("wheat")
    private boolean wheat;

//    @JsonProperty("mackerel")
    private boolean mackerel;

//    @JsonProperty("crab")
    private boolean crab;

//    @JsonProperty("shrimp")
    private boolean shrimp;

//    @JsonProperty("pork")
    private boolean pork;

//    @JsonProperty("peach")
    private boolean peach;

//    @JsonProperty("tomato")
    private boolean tomato;

//    @JsonProperty("walnuts")
    private boolean walnuts;

//    @JsonProperty("chicken")
    private boolean chicken;

//    @JsonProperty("beef")
    private boolean beef;

//    @JsonProperty("squid")
    private boolean squid;

//    @JsonProperty("shellfish")
    private boolean shellfish;

//    @JsonProperty("pineNut")
    private boolean pineNut;

//    @JsonProperty("user_role")
    private String userRole; // 유저 권한

    private Map<String, Boolean> allergies; // 알러지 정보를 담을 필드
}
