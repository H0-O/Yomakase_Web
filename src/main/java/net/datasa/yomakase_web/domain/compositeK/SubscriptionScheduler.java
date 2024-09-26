package net.datasa.yomakase_web.domain.compositeK;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.service.SubscriptionService;
import net.datasa.yomakase_web.domain.entity.SubscriptionEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SubscriptionScheduler {

    private final SubscriptionService subscriptionService;

    // 매일 자정에 구독 갱신 시도 (매일 00:00에 실행)
    @Scheduled(cron = "0 0 0 * * ?")
    public void checkAndRenewSubscriptions() {
        log.info("정기 구독 갱신 검사 시작");

        // 모든 구독자 정보를 조회하여 갱신할 대상 필터링
        List<SubscriptionEntity> subscriptions = subscriptionService.findAllSubscriptions();

        for (SubscriptionEntity subscription : subscriptions) {
            // 각 구독자의 갱신 처리
            subscriptionService.renewSubscription(subscription);
        }

        log.info("정기 구독 갱신 검사 완료");
    }
}
