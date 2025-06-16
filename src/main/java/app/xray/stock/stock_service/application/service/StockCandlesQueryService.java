package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.application.port.in.QueryStockCandlesUseCase;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;

public class StockCandlesQueryService implements QueryStockCandlesUseCase {

    @Override
    public StockCandlesResponse queryCandles(StockCandleSearchConditionQuery query) {
        return null; //  TODO
    }
}
