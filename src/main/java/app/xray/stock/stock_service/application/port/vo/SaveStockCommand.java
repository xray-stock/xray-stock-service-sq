package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.adapter.in.web.dto.SaveStockRequest;
import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.type.MarketType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class SaveStockCommand extends SelfValidating<SaveStockCommand> {

    @NotNull
    private MarketType marketType;
    @NotBlank
    private String symbol;
    @NotBlank
    private String name;

    public static SaveStockCommand from(SaveStockRequest request) {
        SaveStockCommand command = new SaveStockCommand();
        command.marketType = MarketType.valueOf(request.getMarketType());
        command.symbol = request.getSymbol();
        command.name = request.getName();
        return command;
    }
}
