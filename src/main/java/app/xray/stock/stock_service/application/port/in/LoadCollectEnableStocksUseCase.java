package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.domain.Stock;

import java.time.Instant;
import java.util.List;

public interface LoadCollectEnableStocksUseCase {

    List<Stock> loadAll(Instant now);
}
