package app.xray.stock.stock_service.application.port.out;

import java.util.List;
import java.util.Map;

public interface LoadStockListDataPort {
    List<Map<String, Object>> loadStockList();
}
