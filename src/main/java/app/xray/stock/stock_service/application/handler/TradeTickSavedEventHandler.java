package app.xray.stock.stock_service.application.handler;


import app.xray.stock.stock_service.application.service.StockCommandService;
import app.xray.stock.stock_service.common.event.TradeTickSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TradeTickSavedEventHandler {

    private final StockCommandService service;

    @Async
    @EventListener
    public void handleTradeTickSavedEvent(TradeTickSavedEvent event) {
        log.info("[TradeTickSavedEventHandler.handleTradeTickSavedEvent] " +
                        "stockId={}, " +
                        "timeRange.start={}, " +
                        "timeRange.end={}",
                event.stockId(), event.start(), event.end());
        service.updateTradingInfo(event.stockId());
    }

}
