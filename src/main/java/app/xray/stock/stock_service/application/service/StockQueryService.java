package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.application.port.in.LoadCollectEnableStocksUseCase;
import app.xray.stock.stock_service.application.port.in.QueryStockListUseCase;
import app.xray.stock.stock_service.application.port.out.LoadStockListDataPort;
import app.xray.stock.stock_service.application.port.vo.StockListQuery;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockQueryService implements QueryStockListUseCase, LoadCollectEnableStocksUseCase {

    private final LoadStockListDataPort loadStockListDataPort;

    @Override
    public List<Stock> queryStockTopRoiRank(StockListQuery query) {
        return loadStockListDataPort.findByMarketIdPrefix("^" + query.getMarketType().name() + "::")
                .stream()
                .filter(stock -> stock.getCurrentTradeTick() != null)
                .sorted(Comparator.comparing(
                        (Stock s) -> s.getCurrentTradeTick().getChangeRate()
                ).reversed())
                .toList();
    }

    @Override
    public List<Stock> loadAll(Instant now) {
        return loadStockListDataPort.findAllByEnableIsTrue().stream()
                .filter(each ->  each.getMarketType().isMarketOpenNow(now))
                .toList();
    }
}
