package app.xray.stock.stock_service.domain.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class Candle extends SelfValidating<Candle> {

    @NotNull
    private final TimeRange timeRange;
    private final Double open;
    private final Double high;
    private final Double low;
    private final Double close;
    private final Long volume;

    private Candle(TimeRange timeRange, Double open, Double high, Double low, Double close, Long volume) {
        this.timeRange = timeRange;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
        validateSelf();
    }

    public static Candle create(TimeRange range, double open, double high, double low, double close, long volume) {
        return new Candle(range, open, high, low, close, volume);
    }

    public static Candle onlyRange(TimeRange range) {
        return new Candle(range, null, null, null, null, null);
    }
}
