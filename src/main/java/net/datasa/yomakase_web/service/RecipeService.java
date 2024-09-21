package net.datasa.yomakase_web.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
@Slf4j
public class RecipeService {

    public Map<String, Object> getRecipeRecommendation(List<String> allergies, List<String> ingredients) {
        String url = "http://localhost:8000/recipe-recommend";

        log.debug("요청 URL: {}", url);

        // 요청 데이터 생성
        Map<String, Object> requestData = new HashMap<>();
        requestData.put("allergies", allergies);
        requestData.put("ingredients", ingredients);
        log.debug("요청 데이터: {}", requestData);

        try {
            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.NORMAL) // 리다이렉션 처리
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .timeout(Duration.ofSeconds(10))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(requestData)))
                    .build();

            log.debug("HTTP 요청 전송 시작");

            // 서버로부터 응답 받기
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            log.debug("서버 응답 상태 코드: {}", response.statusCode());
            log.debug("서버 응답 본문: {}", response.body());

            // 응답 상태 코드가 500(Internal Server Error)인 경우 로그 출력 및 예외 처리
            if (response.statusCode() == 500) {
                log.error("서버 내부 오류 발생: {}", response.body());
                throw new RuntimeException("서버 내부 오류가 발생했습니다. 응답: " + response.body());
            }

            // JSON 응답인지 확인하고 파싱
            try {
                Map<String, Object> responseBody = new com.fasterxml.jackson.databind.ObjectMapper().readValue(response.body(), Map.class);
                log.debug("JSON 파싱 성공: {}", responseBody);
                return responseBody;
            } catch (Exception e) {
                log.error("JSON 파싱 오류 발생:", e);
                log.error("JSON 형식이 아닌 응답 본문: {}", response.body());
                throw new RuntimeException("서버 응답을 JSON으로 변환하는 데 실패했습니다. 응답: " + response.body(), e);
            }

        } catch (Exception e) {
            log.error("서버와의 통신 중 오류 발생:", e);
            throw new RuntimeException("서버와의 통신 중 문제가 발생했습니다.", e);
        }
    }
}

