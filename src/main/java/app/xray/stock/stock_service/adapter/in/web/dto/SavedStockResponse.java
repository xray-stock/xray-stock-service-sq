package app.xray.stock.stock_service.adapter.in.web.dto;

import app.xray.stock.stock_service.domain.Stock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SavedStockResponse {

    private String marketType;
    private String symbol;
    private String name;
    private Instant createdAt;

    public static SavedStockResponse from(Stock registered) {
        SavedStockResponse response = new SavedStockResponse();
        response.marketType = registered.getMarketType().name();
        response.symbol = registered.getSymbol();
        response.name = registered.getName();
        response.createdAt = registered.getCreateAt();
        return response;
    }
}
