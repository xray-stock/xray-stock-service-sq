package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.application.port.out.LoadStockDataPort;
import app.xray.stock.stock_service.application.port.out.LoadStockListDataPort;
import app.xray.stock.stock_service.application.port.out.SaveStockDataPort;
import app.xray.stock.stock_service.domain.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MongoStockRepository extends
        SaveStockDataPort,
        LoadStockDataPort,
        LoadStockListDataPort,
        MongoRepository<Stock, String> {

    Stock save(Stock stock);

    Optional<Stock> findOneById(String id);
}
