package net.datasa.yomakase_web.controller;

import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.repository.StockRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.security.AuthenticatedUserDetailsService;
import net.datasa.yomakase_web.service.StockService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
@Slf4j
public class StockRestController {

	private final StockService stockService;
	private final StockRepository stockRepository;
	private final AuthenticatedUserDetailsService userService; // 사용자 정보를 얻기 위한 서비스

	@PostMapping("/stockData")
	public List<StockDTO> getStocks() {
		// 현재 로그인한 사용자의 정보를 SecurityContext에서 가져오기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		// AuthenticatedUser로부터 memberNum 가져오기
		AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
		Integer memberNum = userDetails.getMemberNum(); // Integer로 받기

		if (memberNum == null) {
			throw new IllegalStateException("Member number is null");
		}

		return stockService.getStocksByMember(memberNum.intValue());
	}

	/**
	 * 소비 기한이 임박한 순서로 상위 9개의 재고 아이템을 가져온다.
	 * @return 재고 아이템과 이미지 경로를 포함한 리스트
	 */
	@GetMapping("/stockImg")
	public ResponseEntity<List<Map<String, String>>> getTop9Stock() {
		try {
			// 현재 로그인한 사용자의 정보를 SecurityContext에서 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

			// AuthenticatedUser로부터 memberNum 가져오기
			AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
			Integer memberNum = userDetails.getMemberNum(); // Integer로 받기

			List<Map<String, String>> stockItems = stockService.getTop9StockItems(memberNum.intValue());
			return new ResponseEntity<>(stockItems, HttpStatus.OK);
		} catch (Exception e) {
			// 예외 로깅
			e.printStackTrace();  // 예외 발생 시 로그 남기기
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/stockDate")
	public Map<String, Object> updateStockDate(@RequestBody StockDTO stockDTO) {
		Map<String, Object> response = new HashMap<>();
		try {
			stockService.updateStockDate(stockDTO.getIngredientName(), stockDTO.getMemberNum(), stockDTO.getUseByDate());
			response.put("success", true);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "날짜 업데이트에 실패했습니다.");
		}
		return response;
	}

}
