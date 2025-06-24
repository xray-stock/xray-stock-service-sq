package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.common.validation.NoBlankSpace;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.exception.StockDisabledException;
import app.xray.stock.stock_service.domain.exception.StockNotStartedException;
import app.xray.stock.stock_service.domain.type.MarketType;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Document(collection = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends SelfValidating<Stock> {

    @Id
    @NotNull
    private String id;
    @NotNull
    private MarketType marketType;
    @NotBlank
    @NoBlankSpace
    private String symbol;
    @NotBlank
    private String name;

    private Boolean enable;
    private Instant startedAt;
    private Instant endedAt;

    private Instant createAt;

    private TradeTick currentTradeTick;
    private Candle previousCandle;

    public static Stock create(MarketType marketType, String symbol, String name) {
        Stock stock = new Stock();
        stock.marketType = marketType;
        stock.symbol = symbol.trim();
        stock.name = name.trim();
        stock.createAt = Instant.now();
        stock.id = makeId(stock.marketType, stock.symbol);
        stock.validateSelf();
        return stock;
    }

    private static String makeId(MarketType marketType, String symbol) {
        return String.format("%s::%s", marketType, symbol);
    }

    public void start() {
        assertEnabled();
        startedAt = Instant.now();
        endedAt = null;
    }

    public void stop() {
        if (startedAt == null) {
            throw new StockNotStartedException(id);
        }
        endedAt = Instant.now();
    }

    public void enable(boolean enable) {
        this.enable = enable;
    }

    public void assertEnabled() {
        if (enable == null || !enable) {
            throw new StockDisabledException(id);
        }
    }

    public void updateCurrentTradeTick(TradeTick currentTradeTick) {
        this.currentTradeTick = currentTradeTick;
        if (previousCandle == null || previousCandle.getClose() == null) {
            this.currentTradeTick.clearChangeRate();
            return;
        }
        this.currentTradeTick.updateChangeRate(previousCandle.getClose());
    }

    public boolean needsToUpdatePreviousCandle() {
        if (previousCandle == null) return true;

        ZoneId zoneId = marketType.getZoneId();
        LocalDate currentDate = currentTradeTick.getTickAt().atZone(zoneId).toLocalDate();
        LocalDate expectedPreviousCandleDate = getPreviousBusinessDay(currentDate);

        LocalDate actualPreviousCandleDate = previousCandle.getTimeRange().end().atZone(zoneId).toLocalDate();
        return actualPreviousCandleDate.isBefore(expectedPreviousCandleDate);
    }

    public TimeRange getPreviousBusinessDayTimeRange() {
        if (currentTradeTick == null) {
            return null;
        }
        ZoneId zoneId = marketType.getZoneId();
        LocalDate previousBusinessDay =
                getPreviousBusinessDay(currentTradeTick.getTickAt().atZone(zoneId).toLocalDate());

        return TimeRange.forDay(previousBusinessDay, zoneId);
    }

    private LocalDate getPreviousBusinessDay(LocalDate date) {
        LocalDate d = date.minusDays(1);
        while (marketType.isHoliday(d)) {
            d = d.minusDays(1);
        }
        return d;
    }

    public void updatePreviousCandleWith(TimeRange previousTimeRange, List<TradeTick> previousTicks) {
        if (previousTicks.isEmpty()) return;
        TradeTicksCandleConverter converter = TradeTicksCandleConverter
                .forConverting(marketType, previousTimeRange, CandleIntervalType.ONE_DAY, previousTicks);
        converter.aggregate(false);
        this.previousCandle = converter.getCandles().getFirst();
    }
}

