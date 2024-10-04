package net.datasa.yomakase_web.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.HistoryDTO;
import net.datasa.yomakase_web.domain.entity.HistoryEntity;
import net.datasa.yomakase_web.repository.HistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
public class HistoryService {

    @Autowired
    private HistoryRepository historyRepository;

    public Map<String, Object> getWeeklyHistory(Integer memberNum) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(7);
        return getHistoryData(startDate, endDate, memberNum);
    }

    public Map<String, Object> getMonthlyHistory(Integer memberNum) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(1);
        return getHistoryData(startDate, endDate, memberNum);
    }

    public Map<String, Object> getYearlyHistory(Integer memberNum) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusYears(1);
        return getHistoryData(startDate, endDate, memberNum);
    }

    private Map<String, Object> getHistoryData(LocalDate startDate, LocalDate endDate, Integer memberNum) {
        // memberNum을 조건으로 추가하여 해당 사용자의 데이터만 가져옴
        List<Object[]> data = historyRepository.countByIngredientAndTypeAndMemberNum(startDate, endDate, memberNum);
        Map<String, Long> consumptionData = new HashMap<>();
        Map<String, Long> discardData = new HashMap<>();
        Set<String> ingredients = new HashSet<>();

        for (Object[] row : data) {
            String ingredient = (String) row[0];
            String type = (String) row[1];
            Long count = (Long) row[2];
            ingredients.add(ingredient);

            if ("c".equals(type)) {
                consumptionData.put(ingredient, count);
            } else if ("b".equals(type)) {
                discardData.put(ingredient, count);
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("ingredients", new ArrayList<>(ingredients));
        result.put("consumptionData", consumptionData);
        result.put("discardData", discardData);
        return result;
    }

    @Transactional
    public void saveHistory(Map<String, String> historyData, Integer memberNum) {
        String ingredientName = historyData.get("ingredientName");
        String type = historyData.get("type"); // 'c' -> 소비, 'b' -> 버림

        // HistoryDTO 생성
        HistoryDTO historyDTO = HistoryDTO.builder()
                .ingredientName(ingredientName)
                .memberNum(memberNum)
                .date(LocalDate.now()) // 현재 날짜를 저장
                .type(type)
                .build();

        // DTO를 엔티티로 변환하고 저장
        HistoryEntity history = HistoryEntity.builder()
                .ingredientName(historyDTO.getIngredientName())
                .memberNum(historyDTO.getMemberNum())
                .date(historyDTO.getDate())
                .type(historyDTO.getType())
                .build();

        historyRepository.save(history);
        log.info("History 데이터 저장 완료: {}", history);

    }
}