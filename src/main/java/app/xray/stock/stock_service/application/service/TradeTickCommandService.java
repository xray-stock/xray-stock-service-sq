package app.xray.stock.stock_service.application.service;

import app.xray.stock.stock_service.adapter.out.external.dto.TradeTickDataResponse;
import app.xray.stock.stock_service.adapter.out.persistance.MongoTradeTickRepository;
import app.xray.stock.stock_service.application.port.in.CollectTradeTickDataUseCase;
import app.xray.stock.stock_service.application.port.out.LoadTradeTickDataPort;
import app.xray.stock.stock_service.application.port.out.SaveTradeTickDataPort;
import app.xray.stock.stock_service.application.port.out.StockGeneratorClient;
import app.xray.stock.stock_service.application.port.vo.CollectStockCommand;
import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TradeTickCommandService implements CollectTradeTickDataUseCase {

    private final LoadTradeTickDataPort loadTradeTickDataPort;
    private final SaveTradeTickDataPort saveTradeTickDataPort; // FIXME 이거 겹칠때 어떻게 해결할 것인지...
    private final MongoTradeTickRepository mongoTradeTickRepository;

    private final StockGeneratorClient stockGeneratorClient;

    @Override
    public void collectAndSave(CollectStockCommand command) {
        for (Stock stock : command.getStocks()) {
            Optional<TradeTick> optionalLastTradeTick = loadTradeTickDataPort.findTopByStockIdOrderByTickAtDesc(stock.getId());
            Instant end = Instant.now();
            Instant start = optionalLastTradeTick.isEmpty() ? end.minusSeconds(5) : optionalLastTradeTick.get().getTickAt();
            List<TradeTickDataResponse> rangeTradeTicksResponse = stockGeneratorClient.getRangeTradeTicks(stock.getSymbol(), start, end);

            if (rangeTradeTicksResponse.isEmpty()) {
                throw new RuntimeException("수집 데이터 없음");
            }
            // 저장 처리 진행
            List<TradeTick> tradeTicks = TradeTick.fromResponses(stock.getSymbol(), rangeTradeTicksResponse);
            mongoTradeTickRepository.saveAll(tradeTicks); // TODO 수정 필요 saveTradeTickDataPort
        }
    }
}
