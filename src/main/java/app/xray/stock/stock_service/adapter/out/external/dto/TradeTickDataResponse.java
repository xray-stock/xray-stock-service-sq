package app.xray.stock.stock_service.adapter.out.external.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TradeTickDataResponse {
    private String symbol;
    private Double price;
    private Double changeRate;
    private Long volume;
    private Instant updatedAt;
    private Instant requestAt;
}
