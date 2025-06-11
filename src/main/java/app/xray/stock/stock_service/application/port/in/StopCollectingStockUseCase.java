package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.application.port.vo.StopCollectingStockCommand;

public interface StopCollectingStockUseCase {

    void stopCollecting(StopCollectingStockCommand command);
}
