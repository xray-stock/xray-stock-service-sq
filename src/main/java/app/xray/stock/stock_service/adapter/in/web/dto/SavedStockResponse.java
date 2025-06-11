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

    private String id;
    private String marketType;
    private String symbol;
    private String name;
    private Instant createdAt;

    public static SavedStockResponse from(Stock saved) {
        SavedStockResponse response = new SavedStockResponse();
        response.id = saved.getId();
        response.marketType = saved.getMarketType().name();
        response.symbol = saved.getSymbol();
        response.name = saved.getName();
        response.createdAt = saved.getCreateAt();
        return response;
    }
}
