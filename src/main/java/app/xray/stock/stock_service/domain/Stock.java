package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Locale;

@Document(collection = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends SelfValidating<Stock> {

    @Id
    @NotNull
    private String id;
    @NotNull
    private MarketType marketType;
    @NotBlank
    private String symbol;
    @NotBlank
    private String name;
    private Instant createAt;

    public static Stock create(MarketType marketType, String symbol, String name) {
        Stock stock = new Stock();
        stock.marketType = marketType;
        stock.symbol = symbol;
        stock.name = name;
        stock.createAt = Instant.now();
        stock.id = makeId(marketType, symbol);
        stock.validateSelf();
        return stock;
    }

    private static String makeId(MarketType marketType, String symbol) {
        return String.format("%s-%s", marketType, symbol);
    }

    /**
     * MarketType 는 시장 구분을 정의하는 enum 입니다.
     * <pre>
     * - decimalPlaces: 해당 시장에서 사용하는 기본 소숫점 자리수
     * </pre>
     */
    @Getter
    @RequiredArgsConstructor
    public enum MarketType {
        KOSPI(Locale.KOREA, 0),
        NASDAQ(Locale.US, 2);

        private final Locale locale;
        private final int decimalPlaces;
    }
}

