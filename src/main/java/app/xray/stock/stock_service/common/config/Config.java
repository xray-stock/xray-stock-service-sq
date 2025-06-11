package app.xray.stock.stock_service.common.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@EnableConfigurationProperties({
    StockGeneratorApiProperties.class,
})
@Configuration
public class Config {
}
