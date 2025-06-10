package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.Stock;

public interface SaveStockDataPort {

    Stock save(Stock stock);
}
