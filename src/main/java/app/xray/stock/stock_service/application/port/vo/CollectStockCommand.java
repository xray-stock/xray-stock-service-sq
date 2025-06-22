package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.Stock;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
public class CollectStockCommand extends SelfValidating<CollectStockCommand> {

    @NotNull
    private final List<Stock> stocks;
    @NotNull
    private final Instant at;

    private CollectStockCommand(List<Stock> stocks, Instant now) {
        this.stocks = stocks;
        this.at = now;
        validateSelf();
    }

    public static CollectStockCommand of(List<Stock> stocks, Instant now) {
        return new CollectStockCommand(stocks, now);
    }
}
