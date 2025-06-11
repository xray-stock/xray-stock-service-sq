package app.xray.stock.stock_service.common.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "endpoint.stock-generator")
@Getter
@Setter
public class StockGeneratorApiProperties  {
    private String baseUrl;
}
