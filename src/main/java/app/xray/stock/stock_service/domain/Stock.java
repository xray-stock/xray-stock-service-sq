package app.xray.stock.stock_service.domain;

import app.xray.stock.stock_service.common.validation.SelfValidating;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp = "^\\S*$", message = "symbol must not be contained space blank.")
    private String symbol;
    @NotBlank
    private String name;

    private Boolean enable;
    private Instant startedAt;
    private Instant endedAt;

    private Instant createAt;

    public static Stock create(MarketType marketType, String symbol, String name) {
        Stock stock = new Stock();
        stock.marketType = marketType;
        stock.symbol = symbol.trim();
        stock.name = name.trim();
        stock.createAt = Instant.now();
        stock.id = makeId(stock.marketType, stock.symbol);
        stock.validateSelf();
        return stock;
    }

    private static String makeId(MarketType marketType, String symbol) {
        return String.format("%s::%s", marketType, symbol);
    }

    public void start() {
        if (!enable) {
            throw new IllegalStateException(); // FIXME custom exception
        }
        startedAt = Instant.now();
    }

    public void stop() {
        if (startedAt == null) {
            throw new IllegalStateException(); // FIXME custom exception
        }
        endedAt = Instant.now();
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

