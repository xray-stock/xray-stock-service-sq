package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.application.port.vo.CollectStockCommand;

public interface CollectTradeTickDataUseCase {
    void collectAndSave(CollectStockCommand command);
}
