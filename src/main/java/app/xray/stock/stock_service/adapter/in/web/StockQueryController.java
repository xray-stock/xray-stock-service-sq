package app.xray.stock.stock_service.adapter.in.web;

import app.xray.stock.stock_service.application.port.in.QueryStockListUseCase;
import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.adapter.in.web.dto.StockListResponse;
import app.xray.stock.stock_service.application.port.in.QueryStockCandlesUseCase;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;
import app.xray.stock.stock_service.application.port.vo.StockListQuery;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
public class StockQueryController {

    private final QueryStockListUseCase queryStockListUseCase;
    private final QueryStockCandlesUseCase queryStockCandlesUseCase;

    @GetMapping
    public StockListResponse getStocks(@RequestParam("marketCode") String marketCode) {
        List<Stock> stocks = queryStockListUseCase.queryStockTopRoiRank(StockListQuery.from(marketCode));
        return StockListResponse.from(stocks);
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
