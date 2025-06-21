package app.xray.stock.stock_service.adapter.in.web.dto;

import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.TradeTick;
import app.xray.stock.stock_service.domain.vo.Candle;

import java.util.List;

public record StockListResponse(
        List<StockItem> stocks
) {
    public static StockListResponse from(List<Stock> stocks) {
        List<StockItem> items = new java.util.ArrayList<>();

        for (int i = 0; i < stocks.size(); i++) {
            Stock stock = stocks.get(i);
            TradeTick tick = stock.getCurrentTradeTick();
            Candle candle = stock.getPreviousCandle();

            double currentPrice = tick != null ? tick.getPrice() : 0.0;
            Double close = candle != null ? candle.getClose() : null;
            int changePrice = (close != null) ? (int) (currentPrice - close) : 0;
            double changeRate = tick != null ? tick.getChangeRate() : 0.0;
            boolean isLimitUp = stock.getMarketType().isLimitUp(changeRate);

            items.add(new StockItem(
                    i + 1, // rank는 정렬된 순서대로 1부터
                    stock.getName(),
                    stock.getSymbol(),
                    (int) currentPrice,
                    changePrice,
                    changeRate,
                    isLimitUp
            ));
        }
        return new StockListResponse(items);
    }

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
