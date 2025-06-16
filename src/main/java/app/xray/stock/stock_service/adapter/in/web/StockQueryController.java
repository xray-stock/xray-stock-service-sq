package app.xray.stock.stock_service.adapter.in.web;

import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.application.port.in.QueryStockCandlesUseCase;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;
import app.xray.stock.stock_service.common.type.CandleInterval;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockQueryController {

    private final QueryStockCandlesUseCase queryStockCandlesUseCase;

    @GetMapping("/{stockId}/candles")
    public StockCandlesResponse getCandles(
            @PathVariable String stockId,
            @RequestParam CandleInterval interval,
            @RequestParam Instant start,
            @RequestParam Instant end) {
        return queryStockCandlesUseCase.queryCandles(StockCandleSearchConditionQuery.withCondition(
                stockId, interval, start, end));
    }


}
