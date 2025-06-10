package app.xray.stock.stock_service.adapter.out.persistance;

import app.xray.stock.stock_service.application.port.out.SaveStockDataPort;
import app.xray.stock.stock_service.domain.Stock;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MongoStockRepository extends SaveStockDataPort, MongoRepository<Stock, String> {

    Stock save(Stock stock);
}
