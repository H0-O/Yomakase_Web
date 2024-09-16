package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.StockRepository;
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
    public void saveStock(List<Map<String, String>> ingredients, Object identifier) {
        Integer memberNum= null;

        if (identifier instanceof Integer) {
            // 앱에서 온 요청: memberNum이 Integer로 전달됨
            memberNum = (Integer) identifier;
        }else if (identifier instanceof String) {
            // 이메일(아이디)를 사용해 memberNum을 조회
            MemberEntity member = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            memberNum = member.getMemberNum();
        }
        for (Map<String, String> ingredient : ingredients) {
            String ingredientName = ingredient.get("ingredientName");
            String expirationDate = ingredient.get("expirationDate");
            log.info("Saving ingredient: {}, Expiration Date: {}", ingredientName, expirationDate);

            LocalDate currentDate = LocalDate.now();
            LocalDate useByDate = currentDate.plus(Integer.parseInt(expirationDate), ChronoUnit.DAYS);

            StockEntity stockEntity = StockEntity.builder()
                    .ingredientName(ingredientName)
                    .isHaving(true)
                    .memberNum(memberNum)
                    .useByDate(useByDate)
                    .build();

            stockRepository.save(stockEntity);
        }
    }


    public List<String> getAllIngredientNames() {
        return stockRepository.findAllIngredientNames();
    }
}
