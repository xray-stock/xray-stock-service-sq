package app.xray.stock.stock_service.domain.vo;

import app.xray.stock.stock_service.common.type.CandleIntervalType;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * 지정된 시간 범위(start ~ end)를 주어진 캔들 간격과 타임존 기준으로 일정 구간(TimeRange)들로 분할한다.
 */
public record TimeRange(Instant start, Instant end) {

    public static TimeRange of(Instant start, Instant end) {
        return new TimeRange(start, end);
    }

    public static TimeRange ofYesterday(Instant baseTime, ZoneId zoneId) {
        LocalDate date = baseTime.atZone(zoneId).toLocalDate().minusDays(1);
        Instant start = date.atStartOfDay(zoneId).toInstant();
        Instant end = date.plusDays(1).atStartOfDay(zoneId).minusNanos(1).toInstant();
        return of(start, end);
    }

    /**
     * 타임존 기준으로 캔들 간격에 맞게 정렬된 구간 리스트를 생성한다.
     *
     * @param interval 캔들 구간 (예: 5초, 1분, 1시간, 1일 등)
     * @param zoneId   타임존 (예: Asia/Seoul)
     * @return 정렬된 TimeRange 목록
     */
    public List<TimeRange> makeIntervalsWithZone(CandleIntervalType interval, ZoneId zoneId) {
        List<TimeRange> ranges = new ArrayList<>();

        ZonedDateTime zonedStart = start.atZone(zoneId);
        ZonedDateTime zonedEnd = end.atZone(zoneId);

        // 캔들 간격 시작점 정렬
        ZonedDateTime cursor = alignToInterval(zonedStart, interval);

        // start 이전 구간이면 다음 캔들로 이동
        if (cursor.isBefore(zonedStart)) {
            cursor = cursor.plus(interval.getDuration());
        }

        while (cursor.isBefore(zonedEnd)) {
            ZonedDateTime next = cursor.plus(interval.getDuration());
            Instant rangeStart = cursor.toInstant();
            Instant rangeEnd = next.isAfter(zonedEnd) ? end : next.toInstant();
            ranges.add(TimeRange.of(rangeStart, rangeEnd));
            cursor = next;
        }

        return ranges;
    }

    /**
     * 주어진 ZonedDateTime 을 캔들 구간에 맞게 정렬된 기준 시점으로 맞춘다.
     * <pre>
     * 정렬 기준:
     * - 60초 미만: 분 단위로 truncate 후, 초 정렬 (ex: 5초 → 00, 05, 10 ...)
     * - 1시간 미만: 시 단위로 truncate 후, 분 정렬 (ex: 5분 → 00, 05, 10 ...)
     * - 1일 미만: 시 정렬
     * - 1일 이상: 자정 정렬
     * </pre>
     */
    private ZonedDateTime alignToInterval(ZonedDateTime time, CandleIntervalType interval) {
        Duration duration = interval.getDuration();
        long durationSeconds = duration.getSeconds();

        if (durationSeconds < 60) {
            // 초 단위 정렬
            int alignedSecond = (time.getSecond() / (int) durationSeconds) * (int) durationSeconds;
            return time.truncatedTo(ChronoUnit.MINUTES)
                    .withSecond(alignedSecond)
                    .withNano(0);
        } else if (durationSeconds < 3600) {
            // 분 단위 정렬
            int minutes = (time.getMinute() / (int) (durationSeconds / 60)) * (int) (durationSeconds / 60);
            return time.truncatedTo(ChronoUnit.HOURS)
                    .withMinute(minutes)
                    .withSecond(0)
                    .withNano(0);
        } else if (durationSeconds < 86400) {
            // 시간 단위 정렬
            return time.truncatedTo(ChronoUnit.HOURS);
        } else {
            // 일 단위 정렬
            return time.toLocalDate().atStartOfDay(time.getZone());
        }
    }
}
