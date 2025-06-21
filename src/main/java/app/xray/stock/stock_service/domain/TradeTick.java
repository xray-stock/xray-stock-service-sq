package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.adapter.out.external.dto.TradeTickDataResponse;
import app.xray.stock.stock_service.common.validation.NoBlankSpace;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TimeSeries;
import org.springframework.data.mongodb.core.timeseries.Granularity;

import java.time.Instant;
import java.util.List;

@Document(collection = "trade_ticks")
@TimeSeries(timeField = "tickAt", metaField = "stockId", granularity = Granularity.SECONDS, expireAfter = "30d")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TradeTick extends SelfValidating<TradeTick> {

    @NotBlank
    @NoBlankSpace
    private String stockId;
    @Positive
    private double price;
    private double changeRate;
    @PositiveOrZero
    private long volume;
    @NotNull
    private Instant tickAt;

    public static TradeTick create(String stockId, double price, double changeRate, long volume, Instant tickAt) {
        TradeTick tick = new TradeTick();
        tick.stockId = stockId;
        tick.price = price;
        tick.changeRate = changeRate;
        tick.volume = volume;
        tick.tickAt = tickAt;
        tick.validateSelf();
        return tick;
    }

    public static List<TradeTick> fromResponses(String stockId, List<TradeTickDataResponse> responses) {
        return responses.stream().map(item -> TradeTick.fromResponse(stockId, item)).toList();
    }

    private static TradeTick fromResponse(String stockId, TradeTickDataResponse response) {
        TradeTick tick = new TradeTick();
        tick.stockId = stockId;
        tick.price = response.getPrice();
        tick.changeRate = response.getChangeRate();
        tick.volume = response.getVolume();
        tick.tickAt = response.getUpdatedAt();
        tick.validateSelf();
        return tick;
    }

    public void updateChangeRate(double close) {
        if (close == 0.0) {
            clearChangeRate();
            return;
        }
        this.changeRate = ((this.price - close) / close) * 100.0;
    }

    public void clearChangeRate() {
        this.changeRate = 0.0;
    }
}
