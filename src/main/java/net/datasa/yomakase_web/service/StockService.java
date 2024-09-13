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

    /**
     * 재고 데이터를 테이블에 출력
     */
    public List<StockDTO> getAllStocks() {
        // DB에서 모든 재고 데이터를 가져옵니다.
        List<StockEntity> stocks = stockRepository.findAll();

        // 가져온 StockEntity 데이터를 StockDTO 형태로 변환합니다.
        return stocks.stream()
                .map(stock -> StockDTO.builder().ingredientName(stock.getIngredientName())
                        .memberNum(stock.getMemberNum()).isHaving(stock.isHaving()).useByDate(stock.getUseByDate())
                        .updateDate(stock.getUpdateDate()).build())
                .collect(Collectors.toList());
    }

    // 이미지 파일명 리스트
    private final List<String> imageFileNames = List.of(
            "food-container1.png",
            "food-container2.png",
            "food-container3.png"
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


    public List<String> getAllIngredientNames() {
        return stockRepository.findAllIngredientNames();
    }

}
