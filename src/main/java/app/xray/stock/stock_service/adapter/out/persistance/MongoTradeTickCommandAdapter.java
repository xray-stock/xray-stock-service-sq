package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.application.port.out.SaveTradeTickDataPort;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MongoTradeTickCommandAdapter implements SaveTradeTickDataPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<TradeTick> saveAll(List<TradeTick> tradeTicks) {
        if (tradeTicks == null || tradeTicks.isEmpty()) return List.of();
        return new ArrayList<>(mongoTemplate.insert(tradeTicks, TradeTick.class));
    }
}
