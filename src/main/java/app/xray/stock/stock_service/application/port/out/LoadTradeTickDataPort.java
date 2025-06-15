package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.TradeTick;

import java.util.Optional;

public interface LoadTradeTickDataPort {

    // stockId 기준으로 가장 최근 tick 하나 조회
    Optional<TradeTick> findTopByStockIdOrderByTickAtDesc(String stockId);
}
