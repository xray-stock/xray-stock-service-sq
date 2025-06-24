package app.xray.stock.stock_service.application.handler;


import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.adapter.out.redis.RedisTradeTickPublisher;
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

    private final RedisTradeTickPublisher redisPublisher;

    @Async
    @EventListener
    public void handleTradeTickSavedEvent(TradeTickSavedEvent event) {
        log.debug("[TradeTickSavedEventHandler] publish to redis: {}", event.stockId());
        redisPublisher.publish(TradeTickMessage.of(event.stockId(), event.start(), event.end(), event.data()));
    }

}
