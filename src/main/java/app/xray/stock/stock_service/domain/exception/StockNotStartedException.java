package app.xray.stock.stock_service.domain.exception;

/**
 * Stock 이 시작되지 않았는데 stop()이 호출된 경우 발생.
 */
public class StockNotStartedException extends IllegalStateException {
    public StockNotStartedException(String stockId) {
        super("Cannot stop stock because it was not started. (stockId = " + stockId + ")");
    }
}
