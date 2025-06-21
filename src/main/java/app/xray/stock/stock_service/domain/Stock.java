package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.common.validation.NoBlankSpace;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.exception.StockDisabledException;
import app.xray.stock.stock_service.domain.exception.StockNotStartedException;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.*;
import java.util.List;
import java.util.Locale;

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

        ZoneId zoneId = marketType.zoneId;
        LocalDate currentDate = currentTradeTick.getTickAt().atZone(zoneId).toLocalDate();
        LocalDate expectedPreviousCandleDate = getPreviousBusinessDay(currentDate);

        LocalDate actualPreviousCandleDate = previousCandle.getTimeRange().end().atZone(zoneId).toLocalDate();
        return actualPreviousCandleDate.isBefore(expectedPreviousCandleDate);
    }

    private LocalDate getPreviousBusinessDay(LocalDate date) {
        LocalDate d = date.minusDays(1);
        while (isHoliday(d)) {
            d = d.minusDays(1);
        }
        return d;
    }

    private boolean isHoliday(LocalDate date) {
        return isWeekend(date) || isNationalHoliday(date);
        // 추가로 공휴일 리스트 포함하려면 이 메서드 확장 가능
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isNationalHoliday(LocalDate date) {
        return List.of(
                LocalDate.of(2025, 1, 1),   // 신정
                LocalDate.of(2025, 3, 1),   // 삼일절
                LocalDate.of(2025, 6, 6)    // 현충일 등등
        ).contains(date);
    }

    public void updatePreviousCandleWith(List<TradeTick> yesterdayTicks) {
        if (yesterdayTicks.isEmpty()) return;
        TimeRange range = getYesterdayTimeRange(currentTradeTick);
        TradeTicksCandleConverter converter = TradeTicksCandleConverter
                .forConverting(marketType, range, CandleIntervalType.ONE_DAY, yesterdayTicks);
        converter.aggregate(false);
        this.previousCandle = converter.getCandles().getFirst();
    }

    private TimeRange getYesterdayTimeRange(TradeTick baseTick) {
        ZoneId zoneId = marketType.getZoneId();
        LocalDate yesterday = baseTick.getTickAt().atZone(zoneId).toLocalDate().minusDays(1);
        Instant start = yesterday.atStartOfDay(zoneId).toInstant();
        Instant end = yesterday.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
        return TimeRange.of(start, end);
    }


    /**
     * MarketType 는 시장 구분을 정의하는 enum 입니다.
     * <pre>
     * - decimalPlaces: 해당 시장에서 사용하는 기본 소숫점 자리수
     * </pre>
     */
    @Getter
    @RequiredArgsConstructor
    public enum MarketType {
        KOSPI(
                Locale.KOREA,
                ZoneId.of("Asia/Seoul"),
                0,
                LocalTime.of(9, 0),
                LocalTime.of(15, 30),
                30.0
        ),
        NASDAQ(
                Locale.US,
                ZoneId.of("America/New_York"),
                2,
                LocalTime.of(9, 30),
                LocalTime.of(16, 0),
                null
        );

        private final Locale locale;
        private final ZoneId zoneId;
        private final int decimalPlaces;
        private final LocalTime marketOpenTime;
        private final LocalTime marketCloseTime;
        private final Double limitUpRatePercent;

        public boolean isLimitUp(double changeRate) {
            return limitUpRatePercent != null && changeRate >= (limitUpRatePercent * 0.99); // 29.7 for 30%
        }

        /**
         * 현재 시간이 장중인지 확인 (해당 시장 기준)
         */
        public boolean isMarketOpenNow(Instant now) {
            ZonedDateTime zonedNow = now.atZone(zoneId);
            LocalTime localTime = zonedNow.toLocalTime();
            return !localTime.isBefore(marketOpenTime) && localTime.isBefore(marketCloseTime);
        }
    }
}

