package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TradeTicksCandleConverter {

    private TimeRange timeRange;
    private CandleIntervalType interval;
    private List<TradeTick> tradeTicks;
    private List<Candle> candles;

    public static TradeTicksCandleConverter forConverting(
            TimeRange timeRange, CandleIntervalType interval, List<TradeTick> tradeTicks) {
        Assert.notNull(timeRange, "timeRange must not be null");
        Assert.notNull(interval, "interval must not be null");
        Assert.notEmpty(tradeTicks, "tradeTicks must not be empty");

        TradeTicksCandleConverter converter = new TradeTicksCandleConverter();
        converter.timeRange = timeRange;
        converter.interval = interval;
        converter.tradeTicks = tradeTicks;

        return converter;
    }


    public void aggregate(boolean includeEmptySlots) {
        List<TimeRange> ranges = timeRange.makeIntervals(interval);
        List<Candle> result = new ArrayList<>();

        for (TimeRange range : ranges) {
            List<TradeTick> ticksInRange = tradeTicks.stream()
                    .filter(tick -> !tick.getTickAt().isBefore(range.start()) && tick.getTickAt().isBefore(range.end()))
                    .sorted(Comparator.comparing(TradeTick::getTickAt))
                    .toList();

            if (ticksInRange.isEmpty()) {
                if (includeEmptySlots) {
                    result.add(Candle.onlyRange(range));
                }
                continue;
            }

            double open = ticksInRange.getFirst().getPrice();
            double close = ticksInRange.getLast().getPrice();
            double high = ticksInRange.stream().mapToDouble(TradeTick::getPrice).max().orElse(open);
            double low = ticksInRange.stream().mapToDouble(TradeTick::getPrice).min().orElse(open);
            long volume = ticksInRange.stream().mapToLong(TradeTick::getVolume).sum();

            result.add(Candle.create(range, open, high, low, close, volume));
        }

        this.candles = result;
    }


    public List<Candle> getCandles() {
        if (candles == null) {
            throw new IllegalStateException(); // 메세지
        }
        return candles;
    }
}
