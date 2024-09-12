package net.datasa.yomakase_web.controller;

import net.datasa.yomakase_web.domain.compositeK.MemberStock;
import net.datasa.yomakase_web.domain.dto.StockDTO;
import net.datasa.yomakase_web.domain.entity.StockEntity;
import net.datasa.yomakase_web.repository.StockRepository;
import net.datasa.yomakase_web.service.StockService;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/rest")
@RequiredArgsConstructor
@Slf4j
public class StockRestController {

	private final StockService stockService;
	private final StockRepository stockRepository;

	@PostMapping("/stockData")
	public List<StockDTO> getStocks() {
		// DB에서 데이터 가져오기
		return stockService.getAllStocks();
	}

	@GetMapping("/stockImg")
	public ResponseEntity<List<Map<String, String>>> getAllStock() {
		try {
			List<Map<String, String>> stockItems = stockService.getAllStockItems();
			return new ResponseEntity<>(stockItems, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
