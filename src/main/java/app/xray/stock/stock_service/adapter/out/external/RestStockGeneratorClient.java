package app.xray.stock.stock_service.adapter.out.external;

import app.xray.stock.stock_service.adapter.out.external.dto.TradeTickDataResponse;
import app.xray.stock.stock_service.application.port.out.StockGeneratorClient;
import app.xray.stock.stock_service.common.config.StockGeneratorApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class RestStockGeneratorClient implements StockGeneratorClient {

    private final StockGeneratorApiProperties properties;
    private RestClient restClient;

    @PostConstruct
    void init() {
        restClient = RestClient.create(properties.getBaseUrl());
    }

    @Override
    public boolean checkStockTick(String symbol) {
        try {
            return restClient.get()
                    .uri("/stocks/{symbol}/check", symbol)
                    .retrieve()
                    .toBodilessEntity().getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            // 404 → 없는 경우 false 로 처리
            return false;
        }
    }

    @Override
    public List<TradeTickDataResponse> getRangeTradeTicks(String symbol, Instant start, Instant end) {
        try {
            return restClient.get()
                    .uri("/stocks/{symbol}/trade-ticks/range?from=%s&to=%s".formatted(start, end), symbol)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<TradeTickDataResponse>>() {
                    });
            // TODO 수집 실패에 대한 현재 수집 실패시 오류 뜨고 있음, fallback? 대응을 해야하나?
            // 오류 내용은 다음과 같다
        } catch (HttpClientErrorException.NotFound e) {
            // 404 → 없는 경우 false 로 처리
            log.warn("[RestStockGeneratorClient.getRangeTradeTicks] {} No tick data found (404). range={}~{}",
                    symbol, start, end);
            return List.of();
        } catch (ResourceAccessException e) {
            // 제너레이터 서버가 꺼진 경우 등
            log.warn("[RestStockGeneratorClient.getRangeTradeTicks] {} Tick generator unreachable: {}",
                    symbol, e.getMessage());
            return List.of(); // 또는 throw new Custom exception
        } catch (Exception e) {
            log.error("[RestStockGeneratorClient.getRangeTradeTicks] {} Unexpected error while collecting ticks: {}"
                    , symbol, e.getMessage(), e);
            return List.of();
        }
    }
}
