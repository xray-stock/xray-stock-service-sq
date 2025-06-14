package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.Stock;

import java.util.List;

public interface LoadStockListDataPort {
    List<Stock> findAllByEnableIsTrue();
}
