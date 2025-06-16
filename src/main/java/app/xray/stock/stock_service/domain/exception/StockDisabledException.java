package app.xray.stock.stock_service.domain.exception;

public class StockDisabledException extends IllegalStateException {
    public StockDisabledException(String stockId) {
        super("Stock is not enabled: " + stockId);
    }
}
