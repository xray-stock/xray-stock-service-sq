package app.xray.stock.stock_service.adapter.in.socket;

import app.xray.stock.stock_service.common.event.TradeTickSavedEvent;
import app.xray.stock.stock_service.domain.TradeTick;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TradeTickSocketEventHandler {

    private final TradeTickSocketGateway socketGateway;

    @Async
    @EventListener
    public void onTradeTickSaved(TradeTickSavedEvent event) {
        log.debug("[TradeTickSocketEventHandler] broadcasting event: {}", event);
        socketGateway.sendTickToRoom(event.stockId(), event.start(), event.end(), event.data());
    }
}
