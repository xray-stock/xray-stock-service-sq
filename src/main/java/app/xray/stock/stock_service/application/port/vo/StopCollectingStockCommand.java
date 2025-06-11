package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StopCollectingStockCommand extends SelfValidating<StopCollectingStockCommand> {

    @NotBlank
    private final String stockId;

    public StopCollectingStockCommand(String stockId) {
        this.stockId = stockId;
        validateSelf();
    }

    public static StopCollectingStockCommand from(String stockId) {
        return new StopCollectingStockCommand(stockId);
    }
}
