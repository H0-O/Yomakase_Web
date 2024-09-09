package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import net.datasa.yomakase_web.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void saveStock(List<String> ingredients) {
        int memberNum = getCurrentMemberNum(); // 실제 사용자 번호를 가져오는 로직 필요

        for (String ingredient : ingredients) {
            StockEntity stockEntity = StockEntity.builder()
                    .ingredientName(ingredient)
                    .memberNum(memberNum)
                    .isHaving(true) // 기본값 설정
                    .useByDate(LocalDate.now().plusDays(7)) // 임시 유통기한 설정 (7일 후)
                    .build();

            stockRepository.save(stockEntity);
        }
    }

    // 현재 사용자(memberNum) 로직을 구현하거나 현재 사용자 정보를 받아오는 방법을 사용
    private int getCurrentMemberNum() {
        // 로그인된 회원 정보를 가져오는 로직을 추가해야 함.
        return 1; // 예시로 1번 회원으로 설정
    }
}
