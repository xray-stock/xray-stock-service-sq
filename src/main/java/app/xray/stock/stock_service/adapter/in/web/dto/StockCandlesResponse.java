package app.xray.stock.stock_service.adapter.in.web.dto;

import app.xray.stock.stock_service.common.type.CandleIntervalType;
import app.xray.stock.stock_service.domain.vo.Candle;
import app.xray.stock.stock_service.domain.vo.TimeRange;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCandlesResponse {

    private String stockId;
    private String interval;
    private List<CandleData> candles;

    public static StockCandlesResponse of(String stockId, CandleIntervalType interval, List<Candle> candles) {
        StockCandlesResponse response = new StockCandlesResponse();
        response.stockId = stockId;
        response.interval = interval.getValue();
        response.candles = candles.stream().map(CandleData::from).collect(Collectors.toList());
        return response;
    }


    record CandleData(Instant at, Double open, Double high, Double low, Double close, Long volume) {
        public static CandleData from(Candle candle) {
            return new CandleData(
                    pickAt(candle.getTimeRange()),
                    candle.getOpen(),
                    candle.getHigh(),
                    candle.getLow(),
                    candle.getClose(),
                    candle.getVolume());
        }

        private static Instant pickAt(TimeRange range) {
            return range.start(); // 범위 중 시작 시간으로 결정함
        }
    }
}
