package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.domain.TradeTick;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoTradeTickRepository extends MongoRepository<TradeTick, String> {

    Optional<TradeTick> findTopByStockIdOrderByTickAtDesc(String stockId);

}
