package net.datasa.yomakase_web.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PaymentService {

    // 가상의 결제 성공 여부 확인 메서드
    public boolean isPaymentSuccessful(String userEmail) {
        log.info("결제 상태 확인 중 - 사용자 이메일: {}", userEmail);

        // 현재는 가상으로 결제 성공 여부를 무작위로 반환하도록 설정
        boolean paymentSuccessful = Math.random() > 0.1; // 90% 확률로 성공, 10% 확률로 실패

        if (paymentSuccessful) {
            log.info("결제 성공 - 사용자 이메일: {}", userEmail);
        } else {
            log.error("결제 실패 - 사용자 이메일: {}", userEmail);
        }

        return paymentSuccessful;
    }
}
