package app.xray.stock.stock_service.adapter.in.socket.dto;

import app.xray.stock.stock_service.domain.TradeTick;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.time.Instant;
import java.util.List;

public record TradeTickMessage(String stockId,
                               String start,
                               String end,
                               List<TradeTickItem> tradeTickItems) {
    public static TradeTickMessage of(String stockId, Instant start, Instant end, List<TradeTick> ticks) {

        return new TradeTickMessage(
                stockId, start.toString(), end.toString(),
                ticks.stream().map(TradeTickItem::from).toList());
    }


    record TradeTickItem(@Positive double price,
                         double changeRate,
                         @PositiveOrZero long volume,
                         String tickAt) {
        public static TradeTickItem from(TradeTick tradeTick) {
            return new TradeTickItem(tradeTick.getPrice(), tradeTick.getChangeRate(), tradeTick.getVolume(),
                    tradeTick.getTickAt().toString());
        }
    }
}
