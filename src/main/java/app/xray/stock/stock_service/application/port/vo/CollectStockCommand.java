package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.Stock;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

import java.util.List;

@Getter
public class CollectStockCommand extends SelfValidating<CollectStockCommand> {

    @NotEmpty
    private final List<Stock> stocks;

    private CollectStockCommand(List<Stock> stocks) {
        this.stocks = stocks;
        validateSelf();
    }

    public static CollectStockCommand from(List<Stock> stocks) {
        return new CollectStockCommand(stocks);
    }
}
