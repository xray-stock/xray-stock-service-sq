package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.Arguments;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class TimeRangeTest {

    private static final ZoneId KST = ZoneId.of("Asia/Seoul");

    static Stream<Arguments> provideIntervalTestCases() {
        return Stream.of(
                Arguments.of(
                        "5초 단위",
                        CandleIntervalType.FIVE_SEC,
                        Instant.parse("2025-06-20T14:59:56Z"),
                        Instant.parse("2025-06-20T15:00:07Z"),
                        2
                ),
                Arguments.of(
                        "1분 단위",
                        CandleIntervalType.ONE_MIN,
                        Instant.parse("2025-06-20T14:59:45Z"),
                        Instant.parse("2025-06-20T15:01:10Z"),
                        2
                ),
                Arguments.of(
                        "5분 단위",
                        CandleIntervalType.FIVE_MIN,
                        Instant.parse("2025-06-20T14:57:12Z"),
                        Instant.parse("2025-06-20T15:03:30Z"),
                        1
                ),
                Arguments.of(
                        "1일 단위",
                        CandleIntervalType.ONE_DAY,
                        Instant.parse("2025-06-20T14:00:00Z"),
                        Instant.parse("2025-06-22T14:00:00Z"),
                        2
                ),
                Arguments.of(
                        "1주 단위",
                        CandleIntervalType.ONE_WEEK,
                        Instant.parse("2025-06-01T15:00:00Z"),
                        Instant.parse("2025-06-22T14:59:59Z"),
                        3
                )
        );
    }

    @ParameterizedTest(name = "[{index}] {0}")
    @MethodSource("provideIntervalTestCases")
    void testAlignedIntervals(
            String name,
            CandleIntervalType interval,
            Instant start,
            Instant end,
            int expectedSize
    ) {
        // when
        TimeRange range = TimeRange.of(start, end);
        List<TimeRange> intervals = range.makeIntervalsWithZone(interval, KST);

        // then
        assertThat(intervals).hasSize(expectedSize);

        // 구간 길이 확인
        Duration intervalDuration = interval.getDuration();
        for (int i = 0; i < intervals.size(); i++) {
            TimeRange r = intervals.get(i);
            Duration actual = Duration.between(r.start(), r.end());
            if (i < intervals.size() - 1) {
                assertThat(actual).isEqualTo(intervalDuration);
            } else {
                assertThat(actual).isLessThanOrEqualTo(intervalDuration);
            }
        }
    }
}
