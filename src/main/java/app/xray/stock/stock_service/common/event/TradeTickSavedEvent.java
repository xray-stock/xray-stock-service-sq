package app.xray.stock.stock_service.common.event;


import java.time.Instant;

public record TradeTickSavedEvent(String stockId, Instant start, Instant end) { }
