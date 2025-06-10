package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.application.port.vo.SaveStockCommand;
import app.xray.stock.stock_service.domain.Stock;

public interface SaveStockUseCase {

    Stock save(SaveStockCommand command);
}
