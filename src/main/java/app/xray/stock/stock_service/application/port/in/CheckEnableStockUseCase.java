package app.xray.stock.stock_service.application.port.in;

import java.time.Instant;

public interface CheckEnableStockUseCase {

    boolean check(String stockId, Instant now);
}
