package app.xray.stock.stock_service.application.port.vo;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import app.xray.stock.stock_service.domain.Stock;
import app.xray.stock.stock_service.domain.type.MarketType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class StockListQuery extends SelfValidating<StockListQuery> {

    @NotNull
    MarketType marketType;

    public static StockListQuery from(String marketCode) {
        StockListQuery query = new StockListQuery();
        query.marketType = MarketType.valueOf(marketCode);
        query.validateSelf();
        return query;
    }
}
