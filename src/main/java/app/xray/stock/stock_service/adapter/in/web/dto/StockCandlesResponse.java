package app.xray.stock.stock_service.adapter.in.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StockCandlesResponse {

    private String symbol;
    private String interval;
    private List<CandleData> candles;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    static class CandleData {
        private Instant at;
        private float open;
        private float high;
        private float low;
        private float close;
        private long volume;
    }
}
