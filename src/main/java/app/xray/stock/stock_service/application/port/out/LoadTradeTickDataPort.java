package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.TradeTick;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface LoadTradeTickDataPort {

    Optional<TradeTick> loadLastTradeTickDataBy(String stockId);

    List<TradeTick> loadTradeTicksDataByRange(String stockId, Instant start, Instant end);
}
