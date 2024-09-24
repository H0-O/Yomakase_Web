package net.datasa.yomakase_web.controller;

import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.repository.StockRepository;
import net.datasa.yomakase_web.security.AuthenticatedUser;
import net.datasa.yomakase_web.security.AuthenticatedUserDetailsService;
import net.datasa.yomakase_web.service.HistoryService;
import net.datasa.yomakase_web.service.StockService;

import java.time.LocalDate;
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
	private final HistoryService historyService;


	@GetMapping("/stockData")
	public ResponseEntity<?> getStockData() {
		try {
			// 현재 로그인한 사용자의 정보를 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
			Integer memberNum = userDetails.getMemberNum();

			// 재고 데이터를 가져옴
			List<Map<String, Object>> stockData = stockService.getStockForMember(memberNum);

			// 성공적으로 데이터를 가져왔을 때 리스트 반환
			return new ResponseEntity<>(stockData, HttpStatus.OK);
		} catch (Exception e) {
			// 오류 발생 시 로그를 남길 수 있음
			log.error("Error fetching stock data: ", e);

			// 실패 시 오류 메시지를 문자열로 반환
			return new ResponseEntity<>("재고 데이터를 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * 소비 기한이 임박한 순서로 상위 9개의 재고 아이템을 가져온다.
	 * @return 재고 아이템과 이미지 경로를 포함한 리스트
	 */
	@GetMapping("/stockImg")
	public ResponseEntity<?> getTop9Stock() {
		try {
			// 현재 로그인한 사용자의 정보를 SecurityContext에서 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
			Integer memberNum = userDetails.getMemberNum();

			List<Map<String, String>> stockItems = stockService.getTop9StockItems(memberNum.intValue());
			return new ResponseEntity<>(stockItems, HttpStatus.OK);
		} catch (Exception e) {
			log.error("Error fetching top 9 stock images: ", e);
			return new ResponseEntity<>("이미지 데이터를 가져오는 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PostMapping("/stockDate")
	public ResponseEntity<String> updateStockDate(@RequestBody Map<String, String> requestData) {
		try {
			// 현재 로그인한 사용자의 정보를 SecurityContext에서 가져오기
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
			Integer memberNum = userDetails.getMemberNum();

			String ingredientName = requestData.get("ingredientName");
			LocalDate useByDate = LocalDate.parse(requestData.get("useByDate"));  // 날짜 형식 변환

			// Stock 서비스 호출
			stockService.updateStockDate(ingredientName, memberNum, useByDate);

			// 성공 메시지 반환
			return new ResponseEntity<>("소비기한이 성공적으로 업데이트되었습니다.", HttpStatus.OK);
		} catch (Exception e) {
			// 예외 발생 시 에러 메시지 반환
			return new ResponseEntity<>("소비기한 업데이트에 실패했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}


	@PostMapping("/consumeOrDiscard")
	public ResponseEntity<String> consumeOrDiscard(@RequestBody Map<String, String> requestData) {
		try {
			// 현재 로그인한 사용자의 정보를 가져옴
			Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
			AuthenticatedUser userDetails = (AuthenticatedUser) authentication.getPrincipal();
			Integer memberNum = userDetails.getMemberNum();

			// 요청 데이터에서 재료명과 처리 유형(소비 또는 버림) 추출
			String ingredientName = requestData.get("ingredientName");
			log.debug("Processing ingredientName: {}, memberNum: {}", ingredientName, memberNum);

			// History 저장
			historyService.saveHistory(requestData, memberNum);

			// Stock에서 isHaving 필드 업데이트
			stockService.updateStockIsHaving(ingredientName, memberNum);

			// 성공 시 응답 메시지 반환
			return new ResponseEntity<>("재료가 성공적으로 처리되었습니다.", HttpStatus.OK);
		} catch (Exception e) {
			// 예외 발생 시 에러 메시지 반환
			return new ResponseEntity<>("재료 처리 중 오류가 발생했습니다: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
