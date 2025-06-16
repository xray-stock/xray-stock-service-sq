package app.xray.stock.stock_service.application.port.out;

import app.xray.stock.stock_service.adapter.out.external.dto.TradeTickDataResponse;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public interface StockGeneratorClient {

    boolean checkStockTick(String symbol);

    List<TradeTickDataResponse> getRangeTradeTicks(String symbol, Instant start, Instant end);
}
