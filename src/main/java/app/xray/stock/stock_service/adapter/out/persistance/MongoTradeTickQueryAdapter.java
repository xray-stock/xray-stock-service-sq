package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.application.port.out.LoadTradeTickDataPort;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoTradeTickQueryAdapter implements LoadTradeTickDataPort {

    private final MongoTradeTickRepository mongoTradeTickRepository;

    @Override
    public Optional<TradeTick> loadLastTradeTickDataBy(String stockId) {
        return mongoTradeTickRepository.findTopByStockIdOrderByTickAtDesc(stockId);
    }
}
