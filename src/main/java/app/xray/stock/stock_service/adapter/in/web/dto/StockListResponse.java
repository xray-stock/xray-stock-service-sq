package app.xray.stock.stock_service.adapter.in.web.dto;

import java.util.List;

public record StockListResponse(
        List<StockItem> stocks
) {
    public record StockItem(
            int rank,
            String name,
            String symbol,
            int currentPrice,
            int changePrice,
            double changeRate,
            boolean isLimitUp // TODO 구현 - 개념 파악 필요
    ) {}
}
