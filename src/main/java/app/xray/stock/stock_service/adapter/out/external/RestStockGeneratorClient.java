package app.xray.stock.stock_service.adapter.out.external;

import app.xray.stock.stock_service.application.port.out.StockGeneratorClient;
import app.xray.stock.stock_service.common.config.StockGeneratorApiProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

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
}
