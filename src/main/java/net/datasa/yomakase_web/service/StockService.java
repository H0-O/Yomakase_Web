package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.StockRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void saveStock(List<Map<String, String>> ingredients) {
        // 현재 로그인된 사용자의 ID를 가져옵니다.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName(); // 사용자 ID (이메일)
        // 이메일로 MemberEntity를 조회합니다.
        MemberEntity member = memberRepository.findById(username)
                .orElseThrow(() -> new RuntimeException("User not found")); // 사용자 없을 때 예외 처리

        Integer memberNum = member.getMemberNum(); // MemberEntity에서 member_num 값을 가져옵니다.

        for (Map<String, String> ingredient : ingredients) {
            String ingredientName = ingredient.get("ingredientName");
            String expirationDate = ingredient.get("expirationDate");
            log.info("Saving ingredient: {}, Expiration Date: {}", ingredientName, expirationDate); // 각 재료 로그 출력

            // 현재 날짜를 가져옵니다.
            LocalDate currentDate = LocalDate.now();

            // expirationDate를 일(day) 단위로 추가합니다.
            LocalDate useByDate = currentDate.plus(Integer.parseInt(expirationDate), ChronoUnit.DAYS);

            // StockEntity 객체를 생성합니다.
            StockEntity stockEntity = StockEntity.builder()
                    .ingredientName(ingredientName)
                    .isHaving(true)
                    .memberNum(memberNum)
                    .useByDate(useByDate) // 현재 날짜에 expirationDate를 더한 날짜를 사용
                    .build();

            stockRepository.save(stockEntity);
        }
    }

    public List<String> getAllIngredientNames() {
        return stockRepository.findAllIngredientNames();
    }
}
