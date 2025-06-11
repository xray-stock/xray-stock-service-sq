package app.xray.stock.stock_service.application.port.in;

import app.xray.stock.stock_service.application.port.vo.StartCollectingStockCommand;

public interface StartCollectingStockUseCase {

    void startCollecting(StartCollectingStockCommand command);
}
