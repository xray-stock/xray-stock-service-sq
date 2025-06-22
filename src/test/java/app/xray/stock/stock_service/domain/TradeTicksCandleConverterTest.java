package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.domain.Stock.MarketType;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TradeTicksCandleConverterTest {

    @Test
    void itShouldAggregateTradeTicksIntoOneMinuteCandlesCorrectly() {
        // given
        Instant start = Instant.parse("2025-06-20T00:00:00Z"); // KST 09:00
        Instant end = Instant.parse("2025-06-20T00:03:00Z");   // KST 09:03

        TimeRange range = TimeRange.of(start, end);

        List<TradeTick> ticks = List.of(
                TradeTick.create("STK1", 100.0, 0.0, 10, Instant.parse("2025-06-20T00:00:10Z")),
                TradeTick.create("STK1", 105.0, 0.0, 5, Instant.parse("2025-06-20T00:00:50Z")),
                TradeTick.create("STK1", 103.0, 0.0, 20, Instant.parse("2025-06-20T00:01:05Z")),
                TradeTick.create("STK1", 102.0, 0.0, 10, Instant.parse("2025-06-20T00:01:45Z")),
                TradeTick.create("STK1", 110.0, 0.0, 15, Instant.parse("2025-06-20T00:02:30Z"))
        );

        TradeTicksCandleConverter converter = TradeTicksCandleConverter.forConverting(
                MarketType.KOSPI,
                range,
                CandleIntervalType.ONE_MIN,
                ticks
        );

        // when
        converter.aggregate(true);
        List<Candle> candles = converter.getCandles();

        // then
        assertThat(candles).hasSize(3);

        // 첫 번째 캔들 (00:00 ~ 00:01)
        Candle c1 = candles.get(0);
        assertThat(c1.getOpen()).isEqualTo(100.0);
        assertThat(c1.getClose()).isEqualTo(105.0);
        assertThat(c1.getHigh()).isEqualTo(105.0);
        assertThat(c1.getLow()).isEqualTo(100.0);
        assertThat(c1.getVolume()).isEqualTo(15L);

        // 두 번째 캔들 (00:01 ~ 00:02)
        Candle c2 = candles.get(1);
        assertThat(c2.getOpen()).isEqualTo(103.0);
        assertThat(c2.getClose()).isEqualTo(102.0);
        assertThat(c2.getHigh()).isEqualTo(103.0);
        assertThat(c2.getLow()).isEqualTo(102.0);
        assertThat(c2.getVolume()).isEqualTo(30L);

        // 세 번째 캔들 (00:02 ~ 00:03)
        Candle c3 = candles.get(2);
        assertThat(c3.getOpen()).isEqualTo(110.0);
        assertThat(c3.getClose()).isEqualTo(110.0);
        assertThat(c3.getHigh()).isEqualTo(110.0);
        assertThat(c3.getLow()).isEqualTo(110.0);
        assertThat(c3.getVolume()).isEqualTo(15L);
    }

    static Stream<Arguments> provideTestCases() {
        return Stream.of(
                Arguments.of(
                        "정상 캔들 생성 - 1분 단위",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),
                        Instant.parse("2025-06-20T00:03:00Z"),
                        List.of(
                                TradeTick.create("STK1", 100.0, 0.0, 10, Instant.parse("2025-06-20T00:00:10Z")),
                                TradeTick.create("STK1", 105.0, 0.0, 5, Instant.parse("2025-06-20T00:00:50Z")),
                                TradeTick.create("STK1", 103.0, 0.0, 20, Instant.parse("2025-06-20T00:01:05Z")),
                                TradeTick.create("STK1", 102.0, 0.0, 10, Instant.parse("2025-06-20T00:01:45Z")),
                                TradeTick.create("STK1", 110.0, 0.0, 15, Instant.parse("2025-06-20T00:02:30Z"))
                        ),
                        true,
                        3
                ),
                Arguments.of(
                        "빈 구간 포함 - 1분 단위",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),
                        Instant.parse("2025-06-20T00:03:00Z"),
                        List.of(
                                TradeTick.create("STK1", 100.0, 0.0, 10, Instant.parse("2025-06-20T00:00:10Z")),
                                TradeTick.create("STK1", 110.0, 0.0, 10, Instant.parse("2025-06-20T00:02:10Z"))
                        ),
                        true,
                        3
                ),
                Arguments.of(
                        "빈 구간 제외 - 1분 단위",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),
                        Instant.parse("2025-06-20T00:03:00Z"),
                        List.of(
                                TradeTick.create("STK1", 100.0, 0.0, 10, Instant.parse("2025-06-20T00:00:10Z")),
                                TradeTick.create("STK1", 110.0, 0.0, 10, Instant.parse("2025-06-20T00:02:10Z"))
                        ),
                        false,
                        2
                ),
                Arguments.of(
                        "정렬되지 않은 입력 - 1분 단위",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),
                        Instant.parse("2025-06-20T00:02:00Z"),
                        List.of(
                                TradeTick.create("STK1", 102.0, 0.0, 10, Instant.parse("2025-06-20T00:01:45Z")),
                                TradeTick.create("STK1", 103.0, 0.0, 20, Instant.parse("2025-06-20T00:01:05Z")),
                                TradeTick.create("STK1", 105.0, 0.0, 5, Instant.parse("2025-06-20T00:00:50Z")),
                                TradeTick.create("STK1", 100.0, 0.0, 10, Instant.parse("2025-06-20T00:00:10Z"))
                        ),
                        true,
                        2
                ),
                Arguments.of(
                        "start 이전 tick 무시",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),
                        Instant.parse("2025-06-20T00:01:00Z"),
                        List.of(
                                TradeTick.create("STK1", 90.0, 0.0, 10, Instant.parse("2025-06-19T23:59:59Z")),
                                TradeTick.create("STK1", 100.0, 0.0, 20, Instant.parse("2025-06-20T00:00:10Z"))
                        ),
                        true,
                        1
                ),
                Arguments.of(
                        "5분 단위 캔들 - 전체 구간 포함",
                        CandleIntervalType.FIVE_MIN,
                        Instant.parse("2025-06-20T00:00:00Z"),  // 09:00
                        Instant.parse("2025-06-20T00:15:00Z"),  // 09:15
                        List.of(
                                TradeTick.create("STK1", 101.0, 0.0, 10, Instant.parse("2025-06-20T00:01:00Z")), // 첫 구간
                                TradeTick.create("STK1", 103.0, 0.0, 20, Instant.parse("2025-06-20T00:07:00Z")), // 두번째 구간
                                TradeTick.create("STK1", 105.0, 0.0, 15, Instant.parse("2025-06-20T00:14:00Z"))  // 세번째 구간
                        ),
                        true,
                        3
                ),
                Arguments.of(
                        "1일 단위 캔들 - 빈 날짜 포함",
                        CandleIntervalType.ONE_DAY,
                        Instant.parse("2025-06-20T00:00:00Z"),  // 6/20 09:00 KST
                        Instant.parse("2025-06-23T00:00:00Z"),  // 6/23 09:00 KST
                        List.of(
                                TradeTick.create("STK1", 100.0, 0.0, 5, Instant.parse("2025-06-20T01:00:00Z")),
                                TradeTick.create("STK1", 110.0, 0.0, 10, Instant.parse("2025-06-21T05:00:00Z"))
                                // 6/22에는 없음
                        ),
                        true,
                        3
                )
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideTestCases")
    void itShouldGenerateCandlesCorrectlyWithinGivenRange(
            String name,
            CandleIntervalType interval,
            Instant start,
            Instant end,
            List<TradeTick> ticks,
            boolean includeEmptySlots,
            int expectedCandleCount
    ) {
        // given
        TimeRange range = TimeRange.of(start, end);
        TradeTicksCandleConverter converter = TradeTicksCandleConverter.forConverting(
                MarketType.KOSPI,
                range,
                interval,
                ticks
        );

        // when
        converter.aggregate(includeEmptySlots);
        List<Candle> candles = converter.getCandles();

        // then
        assertThat(candles).hasSize(expectedCandleCount);

        // 모든 candle 의 시간 범위는 range 안에 있고,
        candles.forEach(c -> {
            assertThat(c.getTimeRange().start()).isBetween(start, end.minusNanos(1));
            assertThat(c.getTimeRange().end()).isBetween(start.plusNanos(1), end);
        });

        // 모든 candle 은 오름차순 정렬된 상태
        assertThat(candles).isSortedAccordingTo(Comparator.comparing(c -> c.getTimeRange().start()));
    }
}
