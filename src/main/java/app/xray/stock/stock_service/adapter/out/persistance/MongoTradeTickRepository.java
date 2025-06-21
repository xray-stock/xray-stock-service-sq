package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.domain.TradeTick;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface MongoTradeTickRepository extends MongoRepository<TradeTick, String> {

    Optional<TradeTick> findTopByStockIdOrderByTickAtDesc(String stockId);

    List<TradeTick> findAllByStockIdAndTickAtBetweenOrderByTickAtDesc(String stockId, Instant start, Instant end);

}
