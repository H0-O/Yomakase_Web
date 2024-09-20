package net.datasa.yomakase_web.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.domain.entity.MemberEntity;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import net.datasa.yomakase_web.repository.MemberRepository;
import net.datasa.yomakase_web.repository.StockRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockService {

    private final StockRepository stockRepository;
    private final MemberRepository memberRepository;
    private final Random random = new Random();

    @Transactional
    public void saveStock(List<Map<String, String>> ingredients, Object identifier) {
        Integer memberNum = null;

        if (identifier instanceof Integer) {
            // 앱에서 온 요청: memberNum이 Integer로 전달됨
            memberNum = (Integer) identifier;
        } else if (identifier instanceof String) {
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
            LocalDate useByDate = currentDate.plusDays(Integer.parseInt(expirationDate));

            StockEntity stockEntity = StockEntity.builder()
                    .ingredientName(ingredientName)
                    .isHaving(true)
                    .memberNum(memberNum)
                    .useByDate(useByDate)
                    .build();

            stockRepository.save(stockEntity);
        }
    }

    public List<Map<String, Object>> getStockForMember(Object identifier) {
        Integer memberNum = null;

        if (identifier instanceof Integer) {
            // 앱에서 온 요청: memberNum이 Integer로 전달됨
            memberNum = (Integer) identifier;
        } else if (identifier instanceof String) {
            // 이메일(아이디)를 사용해 memberNum을 조회
            MemberEntity member = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            memberNum = member.getMemberNum();
        }

        // memberNum을 사용하여 스톡 데이터 조회
        List<StockEntity> stockEntities = stockRepository.findByMemberNumAndIsHavingTrue(memberNum);

        // StockEntity를 Map 형식으로 변환
        List<Map<String, Object>> stockList = new ArrayList<>();

        // 현재 날짜/시간 가져오기
        LocalDateTime now = LocalDateTime.now();

        for (StockEntity stockEntity : stockEntities) {
            Map<String, Object> stockMap = new HashMap<>();
            stockMap.put("ingredientName", stockEntity.getIngredientName());

            // expirationDate를 계산 (업데이트 날짜와 현재 시간의 차이)
            LocalDateTime updateDate = stockEntity.getUpdateDate();
            long daysBetween = Duration.between(updateDate, now).toDays();

            stockMap.put("expirationDate", daysBetween); // 남은 일수(또는 경과 일수)를 저장

            stockList.add(stockMap);
        }

        return stockList;
    }
    public List<String> getAllIngredientNamesForMember(Object identifier) {
        Integer memberNum = null;

        if (identifier instanceof Integer) {
            // 앱에서 온 요청: memberNum이 Integer로 전달됨
            memberNum = (Integer) identifier;
        } else if (identifier instanceof String) {
            // 이메일(아이디)를 사용해 memberNum을 조회
            MemberEntity member = memberRepository.findById((String) identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            memberNum = member.getMemberNum();
        }

        // memberNum과 isHaving이 true인 ingredient_name 가져오기
        return stockRepository.findAllIngredientNamesByMemberNumAndIsHavingTrue(memberNum);
    }

    /**
     * 특정 회원(memberNum)의 재고 데이터를 테이블에 출력
     * isHaving 값이 0인 경우는 제외
     */
    public List<StockDTO> getStocksByMember(int memberNum) {
        // DB에서 해당 회원(memberNum)의 재고 데이터를 가져옵니다.
        List<StockEntity> stocks = stockRepository.findByMemberNum(memberNum);

        // 가져온 StockEntity 중에서 isHaving 값이 1인 경우만 DTO로 변환하여 반환합니다.
        return stocks.stream()
                .filter(stock -> stock.isHaving())  // isHaving이 true인 경우만 필터링
                .map(stock -> StockDTO.builder()
                        .ingredientName(stock.getIngredientName())
                        .memberNum(stock.getMemberNum())
                        .isHaving(stock.isHaving())
                        .useByDate(stock.getUseByDate())
                        .updateDate(stock.getUpdateDate())
                        .build())
                .collect(Collectors.toList());
    }

    // 이미지 파일명 리스트
    private final List<String> imageFileNames = List.of(
            "food-containerB.png",
            "food-containerG.png",
            "food-containerR.png",
            "food-containerY.png"
    );

//    /**
//     * 소비 기한이 임박한 순서로 상위 9개 재고 아이템을 가져오고 랜덤으로 이미지 경로를 설정한다.
//     *
//     * @param memberNum 로그인한 회원의 memberNum
//     * @return 재고 아이템의 리스트를 포함한 맵 리스트
//     */
//    public List<Map<String, String>> getTop9StockItems(int memberNum) {
//        Pageable pageable = PageRequest.of(0, 9); // 첫 페이지, 9개 항목
//
//        // memberNum에 맞는 재고 아이템을 조회
//        List<StockEntity> stocks = stockRepository.findTop9ByOrderByUseByDateAsc(memberNum, pageable);
//
//        // 이미지의 기본 경로
//        String imagePathBase = "img/";  // 실제로 저장된 이미지 경로를 설정
//
//        // List<StockEntity>를 List<Map>으로 변환하며 랜덤 이미지 경로를 설정
//        return stocks.stream()
//                .map(stock -> {
//                    // 랜덤으로 이미지 파일명 선택
//                    String imageFileName = getRandomImageFileName();
//                    return Map.of(
//                            "name", stock.getIngredientName(),
//                            "image", imageFileName // 이미지 파일명 반환
//                    );
//                })
//                .collect(Collectors.toList());
//    }

    /**
     * 소비 기한이 임박한 순서로 상위 9개 재고 아이템을 가져오고 랜덤으로 이미지 경로를 설정한다.
     *
     * @param memberNum 로그인한 회원의 memberNum
     * @return 재고 아이템의 리스트를 포함한 맵 리스트
     */
    public List<Map<String, String>> getTop9StockItems(int memberNum) {
        Pageable pageable = PageRequest.of(0, 9); // 첫 페이지, 9개 항목

        // memberNum에 맞는 재고 아이템을 조회
        List<StockEntity> stocks = stockRepository.findTop9ByMemberNumAndOrderByUseByDateAsc(memberNum, pageable);

        // 이미지의 기본 경로
        String imagePathBase = "img/";  // 실제로 저장된 이미지 경로를 설정

        // List<StockEntity>를 List<Map>으로 변환하며 랜덤 이미지 경로를 설정
        return stocks.stream()
                .map(stock -> {
                    // 랜덤으로 이미지 파일명 선택
                    String imageFileName = getRandomImageFileName();
                    return Map.of(
                            "name", stock.getIngredientName(),
                            "image", imageFileName // 이미지 파일명 반환
                    );
                })
                .collect(Collectors.toList());
    }

    /**
     * 이미지 파일명 리스트에서 랜덤으로 하나를 선택한다.
     *
     * @return 랜덤 이미지 파일명
     */
    private String getRandomImageFileName() {
        int index = random.nextInt(imageFileNames.size());
        return imageFileNames.get(index); // 랜덤 이미지 파일명 반환
    }

    /**
     * 해당되는 재고를 선택하고 날짜 업데이트하기
     * @param ingredientName 식재료 이름
     * @param memberNum 회원 일련번호
     * @param useByDate 업데이트 날짜
     */
    public void updateStockDate(String ingredientName, int memberNum, LocalDate useByDate) {
        // 재고 항목을 찾습니다.
        StockEntity stockEntity = stockRepository.findByIngredientNameAndMemberNum(ingredientName, memberNum)
                .orElseThrow(() -> new RuntimeException("재고 항목을 찾을 수 없습니다. ingredientName: " + ingredientName + ", memberNum: " + memberNum));

        // found stockEntity의 useByDate 업데이트
        stockEntity.setUseByDate(useByDate);

        // 업데이트된 엔티티 저장
        stockRepository.save(stockEntity);
    }

}
