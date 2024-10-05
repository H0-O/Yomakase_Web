package net.datasa.yomakase_web.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.SubscriptionEntity;
import net.datasa.yomakase_web.repository.SubscriptionRepository;
import net.datasa.yomakase_web.security.AuthenticatedUserDetailsService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final MemberService memberService;
    private final PaymentService paymentService; // 결제 상태 확인을 위한 서비스 추가
    private final AuthenticatedUserDetailsService authenticatedUserDetailsService;
    @Transactional
    public void subscribeUser(String userEmail) {
        MemberEntity member = memberService.findByEmail(userEmail);

        // 구독 정보 생성 및 저장
        SubscriptionEntity subscription = new SubscriptionEntity();
        subscription.setMember(member);
        subscription.setStartDate(LocalDate.now());
        subscription.setEndDate(LocalDate.now().plusMonths(1)); // 예시로 1개월 후로 설정

        subscriptionRepository.save(subscription);
        log.info("구독 정보 저장 완료 - 사용자 이메일: {}", userEmail);

        updateUserRoles(member);
    }

    @Transactional
    public void unsubscribeUser(String userEmail) {
        MemberEntity member = memberService.findByEmail(userEmail);

        // 해당 사용자의 구독 정보 삭제
        Optional<SubscriptionEntity> subscription = subscriptionRepository.findByMember(member);
        subscription.ifPresent(subscriptionRepository::delete);
        log.info("구독 정보 삭제 완료 - 사용자 이메일: {}", userEmail);

        updateUserRoles(member);
    }

    // 구독 자동 갱신 시도
    @Transactional
    public void renewSubscription(SubscriptionEntity subscription) {
        String userEmail = subscription.getMember().getId();
        log.info("구독 갱신 시도 - 사용자 이메일: {}, 구독 종료일: {}", userEmail, subscription.getEndDate());

        try {
            // 구독 종료일이 오늘이거나 이미 지난 경우에만 갱신 시도
            if (subscription.getEndDate().isBefore(LocalDate.now()) || subscription.getEndDate().isEqual(LocalDate.now())) {
                // 결제 성공 여부 확인
                if (paymentService.isPaymentSuccessful(userEmail)) {
                    // 결제가 성공하면 구독 종료일을 한 달 연장
                    subscription.setEndDate(subscription.getEndDate().plusMonths(1));
                    subscriptionRepository.save(subscription);
                    log.info("구독 갱신 성공 - 사용자 이메일: {}", userEmail);
                } else {
                    // 결제가 실패하면 구독 취소
                    unsubscribeUser(userEmail);
                    memberService.unsubscribeUser(userEmail);
                    log.info("결제 실패로 구독 취소 - 사용자 이메일: {}", userEmail);
                }
            }
        } catch (Exception e) {
            // 예외 처리: 로그 기록 및 필요한 경우 추가 작업 수행
            log.error("구독 갱신 중 오류 발생 - 사용자 이메일: {}, 오류: {}", userEmail, e.getMessage(), e);
        }
    }
    // 사용자 권한을 즉시 업데이트하는 메서드
    private void updateUserRoles(MemberEntity member) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = authenticatedUserDetailsService.loadUserByUsername(member.getId());

        // 새로운 권한으로 인증 정보 생성
        Authentication newAuth = new UsernamePasswordAuthenticationToken(userDetails, authentication.getCredentials(), userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        log.info("사용자 권한 업데이트 완료 - 사용자 이메일: {}", member.getId());
    }
    public List<SubscriptionEntity> findAllSubscriptions() {
        // 구독 정보를 모두 조회하여 반환
        return subscriptionRepository.findAll();
    }
}