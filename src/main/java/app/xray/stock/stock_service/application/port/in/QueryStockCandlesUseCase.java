package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.adapter.in.web.dto.StockCandlesResponse;
import app.xray.stock.stock_service.application.port.vo.StockCandleSearchConditionQuery;

public interface QueryStockCandlesUseCase {

    StockCandlesResponse queryCandles(StockCandleSearchConditionQuery query);

}
