package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.common.validation.NoBlankSpace;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Getter
public class StockCandleSearchConditionQuery extends SelfValidating<StockCandleSearchConditionQuery> {

    static final CandleIntervalType DEFAULT_CANDLE_INTERVAL = CandleIntervalType.ONE_MIN;
    static final Duration DEFAULT_TIME_GAP = Duration.of(1, ChronoUnit.HOURS);

    @NotNull(message = "stockId must not be null.")
    @NoBlankSpace(message = "stockId must not be contained blank space")
    private String stockId;
    private CandleIntervalType interval;
    private Instant start;
    private Instant end;

    public static StockCandleSearchConditionQuery withCondition(String stockId, String interval,
                                                                Instant start, Instant end) {
        StockCandleSearchConditionQuery query = new StockCandleSearchConditionQuery();
        query.stockId = stockId;
        query.interval = intervalOrDefault(CandleIntervalType.convertOrDefaultNull(interval));
        query.end = endOrDefaultWithTruncating(end);
        query.start = startOrDefaultAfterInitEndWithTruncating(start, query.end);

        query.validateSelf();
        return query;
    }

    private static CandleIntervalType intervalOrDefault(CandleIntervalType interval) {
        return Objects.requireNonNullElse(interval, DEFAULT_CANDLE_INTERVAL);
    }

    private static Instant endOrDefaultWithTruncating(Instant end) {
        return Objects.requireNonNullElse(end, Instant.now()).truncatedTo(TimeUnit.SECONDS.toChronoUnit());
    }

    private static Instant startOrDefaultAfterInitEndWithTruncating(
            Instant start, Instant truncatedEnd) {
        if (start == null) {
            if (truncatedEnd == null) {
                throw new IllegalArgumentException("Both start and end cannot be null.");
            }
            return truncatedEnd.minus(DEFAULT_TIME_GAP);
        }
        if (!start.isBefore(truncatedEnd)) {
            throw new IllegalArgumentException("Start time must be before end time.");
        }
        return start.truncatedTo(TimeUnit.SECONDS.toChronoUnit());
    }
}
