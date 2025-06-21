package app.xray.stock.stock_service.application.service.exception;

import java.time.Instant;

public class NoTradeTickCollectedException extends RuntimeException {
    public NoTradeTickCollectedException(String stockId, Instant from, Instant to) {
        super("No tick data collected for [" + stockId + "] within range [" + from + " ~ " + to + "].");
    }
}
