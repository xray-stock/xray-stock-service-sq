package app.xray.stock.stock_service.adapter.in.redis;

import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.adapter.out.redis.RedisTradeTickPublisher;
import app.xray.stock.stock_service.common.event.TradeTickSavedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisPublishTradeTickEventHandler {

    private final RedisTradeTickPublisher redisPublisher;

    @Async
    @EventListener
    public void onTradeTickSaved(TradeTickSavedEvent event) {
        log.debug("[TradeTickSocketEventHandler] broadcasting event: {}", event);
        redisPublisher.publish(TradeTickMessage.of(event.stockId(), event.start(), event.end(), event.data()));
    }
}
