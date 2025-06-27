package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.Stock;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LoadStockListDataPort {
    List<Stock> findAllByEnableIsTrue();

    Optional<Stock> findByIdAndEnableIsTrue(String id);

    @Query("{ '_id': { $regex: ?0 }, 'enable': true }") // TODO 어뎁터 리팩토링
    List<Stock> findByMarketIdPrefix(String marketIdPrefixRegex);
}
