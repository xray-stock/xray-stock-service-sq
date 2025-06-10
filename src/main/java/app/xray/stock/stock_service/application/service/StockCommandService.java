package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.application.port.in.SaveStockUseCase;
import app.xray.stock.stock_service.application.port.out.SaveStockDataPort;
import app.xray.stock.stock_service.application.port.vo.SaveStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockCommandService implements SaveStockUseCase {

    private final SaveStockDataPort saveStockDataPort;

    @Override
    public Stock save(SaveStockCommand command) {
        return saveStockDataPort.save(Stock.create(command.getMarketType(), command.getSymbol(), command.getName()));
    }
}
