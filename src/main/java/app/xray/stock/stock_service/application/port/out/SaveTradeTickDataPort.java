package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.domain.TradeTick;

import java.util.List;

public interface SaveTradeTickDataPort {

    List<TradeTick> saveAll(List<TradeTick> tradeTicks);
}
