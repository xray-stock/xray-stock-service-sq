package app.xray.stock.stock_service.domain.vo;

import app.xray.stock.stock_service.common.type.CandleInterval;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public record TimeRange(Instant start, Instant end) {

    public static TimeRange of(Instant start, Instant end) {
        return new TimeRange(start, end);
    }

    public List<TimeRange> makeIntervals(CandleInterval interval) {
        List<TimeRange> ranges = new ArrayList<>();

        Instant cursor = start;
        while (cursor.isBefore(end)) {
            Instant next = cursor.plus(interval.getDuration());
            if (next.isAfter(end)) {
                ranges.add(TimeRange.of(cursor, end)); // 마지막 자투리
                break;
            } else {
                ranges.add(TimeRange.of(cursor, next));
                cursor = next;
            }
        }

        return ranges;
    }
}
