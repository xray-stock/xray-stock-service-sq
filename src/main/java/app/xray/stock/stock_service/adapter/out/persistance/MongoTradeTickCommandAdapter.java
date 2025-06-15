package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.application.port.out.SaveTradeTickDataPort;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MongoTradeTickCommandAdapter implements SaveTradeTickDataPort {

    private final MongoTradeTickRepository mongoTradeTickRepository;

    @Override
    public List<TradeTick> saveAll(List<TradeTick> tradeTicks) {
        if (tradeTicks.isEmpty()) {
            return List.of();
        }
        return mongoTradeTickRepository.saveAll(tradeTicks);
    }
}
