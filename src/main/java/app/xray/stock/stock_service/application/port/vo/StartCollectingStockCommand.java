package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class StartCollectingStockCommand extends SelfValidating<StartCollectingStockCommand> {

    @NotBlank
    private final String stockId;

    public StartCollectingStockCommand(String stockId) {
        this.stockId = stockId;
        validateSelf();
    }

    public static StartCollectingStockCommand from(String stockId) {
        return new StartCollectingStockCommand(stockId);
    }
}
