package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.application.port.in.LoadCollectEnableStocksUseCase;
import app.xray.stock.stock_service.application.port.out.LoadStockListDataPort;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockQueryService implements LoadCollectEnableStocksUseCase {

    private final LoadStockListDataPort loadStockListDataPort;

    @Override
    public List<Stock> loadAll() {
        return loadStockListDataPort.findAllByEnableIsTrue();
    }
}
