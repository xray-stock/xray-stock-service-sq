package app.xray.stock.stock_service.adapter.in.web;

import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.adapter.in.web.dto.StockListResponse;
import app.xray.stock.stock_service.application.port.in.QueryStockCandlesUseCase;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockQueryController {

    private final QueryStockCandlesUseCase queryStockCandlesUseCase;

    @GetMapping
    public StockListResponse getStocks(
            @RequestParam("") String sort
    ) {
        return new StockListResponse(List.of());
    }

    @GetMapping("/{stockId}/candles")
    public StockCandlesResponse getCandles(
            @PathVariable("stockId") String stockId,
            @RequestParam(required = false) String interval,
            @RequestParam(required = false) Instant start,
            @RequestParam(required = false) Instant end) {
        return queryStockCandlesUseCase.queryCandles(StockCandleSearchConditionQuery
                .withCondition(stockId, interval, start, end));
    }
}
