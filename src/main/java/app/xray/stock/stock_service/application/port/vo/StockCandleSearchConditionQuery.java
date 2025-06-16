package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.type.CandleInterval;
import app.xray.stock.stock_service.common.validation.NoBlankSpace;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;

@Getter
public class StockCandleSearchConditionQuery extends SelfValidating<StockCandleSearchConditionQuery> {

    static final CandleInterval DEFAULT_CANDLE_INTERVAL = CandleInterval.ONE_MIN;
    static final Duration DEFAULT_TIME_GAP = Duration.of(1, ChronoUnit.HOURS);

    @NotNull(message = "stockId must not be null.")
    @NoBlankSpace(message = "stockId must not be contained blank space")
    private String stockId;
    private CandleInterval interval;
    private Instant start;
    private Instant end;

    public static StockCandleSearchConditionQuery withCondition(String stockId, CandleInterval interval, Instant start, Instant end) {
        StockCandleSearchConditionQuery query = new StockCandleSearchConditionQuery();
        query.stockId = stockId;
        query.interval = intervalOrDefault(interval);
        query.end = endOrDefault(end);
        query.start = startOrDefaultAfterInitEnd(start, query.end);

        query.validateSelf();
        return query;
    }

    private static CandleInterval intervalOrDefault(CandleInterval interval) {
        return Objects.requireNonNullElse(interval, DEFAULT_CANDLE_INTERVAL);
    }

    private static Instant endOrDefault(Instant end) {
        return Objects.requireNonNullElse(end, Instant.now());
    }

    private static Instant startOrDefaultAfterInitEnd(Instant start, Instant end) {
        if (start == null) {
            if (end == null) {
                throw new IllegalArgumentException("Both start and end cannot be null.");
            }
            return end.minus(DEFAULT_TIME_GAP);
        }
        if (!start.isBefore(end)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        return start;
    }
}
