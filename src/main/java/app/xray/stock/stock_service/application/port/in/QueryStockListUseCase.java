package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.application.port.vo.StockListQuery;
import app.xray.stock.stock_service.domain.Stock;

import java.util.List;

public interface QueryStockListUseCase {

    List<Stock> queryStockTopRoiRank(StockListQuery from);
}
