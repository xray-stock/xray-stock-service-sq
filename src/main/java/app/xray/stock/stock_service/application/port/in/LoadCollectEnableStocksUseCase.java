package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.domain.Stock;

import java.util.List;

public interface LoadCollectEnableStocksUseCase {

    List<Stock> loadAll();
}
