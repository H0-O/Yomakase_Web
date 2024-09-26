package net.datasa.yomakase_web.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 *  회원 정보 DTO
 *  9/3 최초 작성 (by 호영)
 */
@Builder // 빌더 패턴을 사용하여 객체를 생성할 수 있게 함
@Data // Lombok이 제공하는 어노테이션으로, getter, setter, toString, equals, hashCode 메서드를 자동 생성함
@AllArgsConstructor // 모든 필드를 포함하는 생성자를 자동 생성함
@NoArgsConstructor // 파라미터가 없는 기본 생성자를 자동 생성함
public class SubscriptionDTO {
    private Integer subscriptionId;
    private Integer memberNum;
    private LocalDate startDate;
    private LocalDate endDate;
}
