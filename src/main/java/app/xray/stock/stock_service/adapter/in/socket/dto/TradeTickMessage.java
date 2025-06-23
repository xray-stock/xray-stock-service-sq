package app.xray.stock.stock_service.adapter.in.socket.dto;

import app.xray.stock.stock_service.domain.TradeTick;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

public record TradeTickMessage(String stockId,
                               @Positive double price,
                               double changeRate,
                               @PositiveOrZero long volume,
                               String tickAt) {
    public static TradeTickMessage from(TradeTick tick) {
        return new TradeTickMessage(
                tick.getStockId(),
                tick.getPrice(),
                tick.getChangeRate(),
                tick.getVolume(),
                tick.getTickAt().toString()
        );
    }
}
