package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.application.port.in.SaveStockUseCase;
import app.xray.stock.stock_service.application.port.in.StartCollectingStockUseCase;
import app.xray.stock.stock_service.application.port.in.StopCollectingStockUseCase;
import app.xray.stock.stock_service.application.port.out.LoadStockDataPort;
import app.xray.stock.stock_service.application.port.out.SaveStockDataPort;
import app.xray.stock.stock_service.application.port.out.StockGeneratorClient;
import app.xray.stock.stock_service.application.port.vo.SaveStockCommand;
import app.xray.stock.stock_service.application.port.vo.StartCollectingStockCommand;
import app.xray.stock.stock_service.application.port.vo.StopCollectingStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockCommandService implements SaveStockUseCase, StartCollectingStockUseCase, StopCollectingStockUseCase {

    private final SaveStockDataPort saveStockDataPort;
    private final LoadStockDataPort loadStockDataPort;

    private final StockGeneratorClient stockGeneratorClient;

    @Override
    public Stock save(SaveStockCommand command) {
        return saveStockDataPort.save(Stock.create(command.getMarketType(), command.getSymbol(), command.getName()));
    }

    @Override
    public void startCollecting(StartCollectingStockCommand command) {
        Stock stock = loadStockDataPort.findOneById(command.getStockId()).orElseThrow(); // FIXME custom exception
        stock.enable(stockGeneratorClient.checkStockTick(stock.getSymbol()));
        stock.start();
        saveStockDataPort.save(stock);
    }

    @Override
    public void stopCollecting(StopCollectingStockCommand command) {
        Stock stock = loadStockDataPort.findOneById(command.getStockId()).orElseThrow(); // FIXME custom exception
        stock.stop();
        saveStockDataPort.save(stock);
    }
}
