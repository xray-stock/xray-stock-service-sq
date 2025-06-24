package app.xray.stock.stock_service.adapter.in.redis;

import app.xray.stock.stock_service.adapter.in.socket.TradeTickSocketGateway;
import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTradeTickSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final TradeTickSocketGateway socketGateway;

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            String body = message.toString();
            TradeTickMessage tradeTickMessage = objectMapper.readValue(body, TradeTickMessage.class);
            log.debug("Redis listening: {}", tradeTickMessage);

            // WebSocket room 에 전송
            socketGateway.sendTickToRoom(tradeTickMessage);

        } catch (Exception e) {
            log.error("FAIL! Redis making message process", e);
        }
    }
}
