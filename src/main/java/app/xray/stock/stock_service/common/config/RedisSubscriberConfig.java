package app.xray.stock.stock_service.common.config;

import app.xray.stock.stock_service.adapter.in.redis.RedisTradeTickSubscriber;
import app.xray.stock.stock_service.adapter.out.redis.RedisTradeTickPublisher;
import app.xray.stock.stock_service.common.constant.RedisChannel;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import jakarta.annotation.PostConstruct;

@Configuration
@RequiredArgsConstructor
public class RedisSubscriberConfig {

    private final RedisConnectionFactory connectionFactory;
    private final RedisTradeTickSubscriber subscriber;

    @PostConstruct
    public void subscribe() {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.addMessageListener(subscriber, new PatternTopic(RedisChannel.TRADE_TICK));
        container.afterPropertiesSet();
        container.start();
    }
}
