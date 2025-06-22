package app.xray.stock.stock_service.adapter.in.job;

import app.xray.stock.stock_service.application.port.in.CollectTradeTickDataUseCase;
import app.xray.stock.stock_service.application.port.in.LoadCollectEnableStocksUseCase;
import app.xray.stock.stock_service.application.port.vo.CollectStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Log4j2
@Component
@RequiredArgsConstructor
public class CollectStockTradeTickScheduler {

    private final LoadCollectEnableStocksUseCase loadCollectEnableStocksUseCase;
    private final CollectTradeTickDataUseCase collectTradeTickDataUseCase;

    @Scheduled(fixedRate = 5000)
    public void collect() {
        log.info("[CollectStockTradeTickScheduler.collect] START");
        Instant now = Instant.now();
        List<Stock> stocks = loadCollectEnableStocksUseCase.loadAll(now);
        collectTradeTickDataUseCase.collectAndSave(CollectStockCommand.of(stocks, now));
        log.info("[CollectStockTradeTickScheduler.collect] END");
    }
}

