package app.xray.stock.stock_service.application.port.out;

public interface StockGeneratorClient {

    boolean checkStockTick(String symbol);

//    Map<String, Object> getLatestTradeTick(String symbol);
}
