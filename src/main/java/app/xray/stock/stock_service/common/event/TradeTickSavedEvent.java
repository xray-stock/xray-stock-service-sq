package app.xray.stock.stock_service.common.event;


import app.xray.stock.stock_service.domain.TradeTick;

import java.time.Instant;
import java.util.List;

public record TradeTickSavedEvent(String stockId, Instant start, Instant end, List<TradeTick> data) { }
