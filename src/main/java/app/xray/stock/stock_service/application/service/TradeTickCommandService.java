package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.adapter.out.external.dto.TradeTickDataResponse;
import app.xray.stock.stock_service.application.port.in.CollectTradeTickDataUseCase;
import app.xray.stock.stock_service.application.port.out.LoadTradeTickDataPort;
import app.xray.stock.stock_service.application.port.out.SaveTradeTickDataPort;
import app.xray.stock.stock_service.application.port.out.StockGeneratorClient;
import app.xray.stock.stock_service.application.port.vo.CollectStockCommand;
import app.xray.stock.stock_service.application.service.exception.NoTradeTickCollectedException;
import app.xray.stock.stock_service.common.event.TradeTickSavedEvent;
import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@RequiredArgsConstructor
public class TradeTickCommandService implements CollectTradeTickDataUseCase {

    private final LoadTradeTickDataPort loadTradeTickDataPort;
    private final SaveTradeTickDataPort saveTradeTickDataPort;

    private final StockGeneratorClient stockGeneratorClient;

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public void collectAndSave(CollectStockCommand command) {
        for (Stock stock : command.getStocks()) {
            Optional<TradeTick> optionalLastTradeTick = loadTradeTickDataPort.loadLastTradeTickDataBy(stock.getId());
            Instant end = Instant.now();
            Instant start = optionalLastTradeTick.isEmpty() ? end.minusSeconds(5) : optionalLastTradeTick.get().getTickAt();
            List<TradeTickDataResponse> rangeTradeTicksResponse = stockGeneratorClient.getRangeTradeTicks(stock.getSymbol(), start, end);
            if (rangeTradeTicksResponse.isEmpty()) {
                // 이렇게 수집 계속 안되는 경우 카운팅
                throw new NoTradeTickCollectedException(stock.getId(), start, end);
            }
            log.info("[TradeTickCommandService.collectAndSave] {} Collected {} tick(s). Range: {} ~ {}",
                    stock.getId(),
                    rangeTradeTicksResponse.size(),
                    start, end);

            // 저장 처리 진행
            saveTradeTickDataPort.saveAll(TradeTick.fromResponses(
                    stock.getId(), rangeTradeTicksResponse));

            // 이벤트 발행
            eventPublisher.publishEvent(new TradeTickSavedEvent(stock.getId(), start, end));
        }
    }
}
