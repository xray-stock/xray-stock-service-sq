package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.TradeTick;

import java.util.Optional;

public interface LoadTradeTickDataPort {

    Optional<TradeTick> loadLastTradeTickDataBy(String stockId);
}
