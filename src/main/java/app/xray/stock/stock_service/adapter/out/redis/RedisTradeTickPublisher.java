package app.xray.stock.stock_service.adapter.out.redis;

import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.common.constant.RedisChannel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisTradeTickPublisher {

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public void publish(TradeTickMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            redisTemplate.convertAndSend(RedisChannel.TRADE_TICK, json);
            log.debug("Redis publish to channel '{}': {}", RedisChannel.TRADE_TICK, json);
        } catch (JsonProcessingException e) {
            log.error("Redis publish FAIL - JSON Serialization", e);
        }
    }
}
