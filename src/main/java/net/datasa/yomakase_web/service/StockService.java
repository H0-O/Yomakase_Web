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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
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

//    /**
//     * 재고 데이터를 테이블에 출력
//     */
//    public List<StockDTO> getAllStocks() {
//        // DB에서 모든 재고 데이터를 가져옵니다.
//        List<StockEntity> stocks = stockRepository.findAll();
//
//        // 가져온 StockEntity 데이터를 StockDTO 형태로 변환합니다.
//        return stocks.stream()
//                .map(stock -> StockDTO.builder()
//                        .ingredientName(stock.getIngredientName())
//                        .memberNum(stock.getMemberNum())
//                        .isHaving(stock.isHaving())
//                        .useByDate(stock.getUseByDate())
//                        .updateDate(stock.getUpdateDate())
//                        .build())
//                .collect(Collectors.toList());
//    }

    /**
     * 특정 회원(memberNum)의 재고 데이터를 테이블에 출력
     */
    public List<StockDTO> getStocksByMember(int memberNum) {
        // DB에서 해당 회원(memberNum)의 재고 데이터를 가져옵니다.
        List<StockEntity> stocks = stockRepository.findByMemberNum(memberNum);

        // 가져온 StockEntity 데이터를 StockDTO 형태로 변환합니다.
        return stocks.stream()
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

    /**
     * 소비 기한이 임박한 순서로 상위 9개 재고 아이템을 가져오고 랜덤으로 이미지 경로를 설정한다.
     *
     * @return 재고 아이템의 리스트를 포함한 맵 리스트
     */
    public List<Map<String, String>> getTop9StockItems() {
        Pageable pageable = PageRequest.of(0, 9); // 첫 페이지, 9개 항목
        List<StockEntity> stocks = stockRepository.findTop9ByOrderByUseByDateAsc(pageable);

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
        return imageFileNames.get(index); // 예를 들어 '3.png'와 같은 파일명
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

    public List<String> getAllIngredientNames() {
        return stockRepository.findAllIngredientNames();
    }

}
