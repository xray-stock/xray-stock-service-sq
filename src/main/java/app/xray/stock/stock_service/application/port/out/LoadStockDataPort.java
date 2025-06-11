package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.Stock;

import java.util.Optional;

public interface LoadStockDataPort {
    Optional<Stock> findOneById(String id);
}
