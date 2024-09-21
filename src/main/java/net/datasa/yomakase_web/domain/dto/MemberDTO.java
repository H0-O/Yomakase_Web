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

    private Integer memberNum;  // 회원 고유 번호

    private String email;  // 회원 이메일(ID)

    private String password;  // 비밀번호

    private String nickname;  // 닉네임

    private char gender;  // 성별 ('M' 또는 'F')

    private LocalDate birthDate;  // 생년월일

    private Integer height;  // 키

    private Integer weight;  // 몸무게

    private boolean enabled;  // 계정 활성화 여부

    // 알레르기 정보 (boolean 타입)
    private boolean eggs;
    private boolean milk;
    private boolean buckwheat;
    private boolean peanut;
    private boolean soybean;
    private boolean wheat;
    private boolean mackerel;
    private boolean crab;
    private boolean shrimp;
    private boolean pork;
    private boolean peach;
    private boolean tomato;
    private boolean walnuts;
    private boolean chicken;
    private boolean beef;
    private boolean squid;
    private boolean shellfish;
    private boolean pineNut;

    private String userRole;  // 유저 권한 (ROLE_USER, ROLE_SUBSCRIBER, ROLE_ADMIN)

    private Map<String, Boolean> allergies;  // 알러지 정보를 담을 필드 (추가적인 알러지 정보 관리를 위해 사용)
}
