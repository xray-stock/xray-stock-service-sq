package app.xray.stock.stock_service.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
public enum CandleInterval {
    ONE_MIN("1m"), FIVE_MIN("5m"), ONE_DAY("1d"), ONE_WEEK("1w");

    private final String value;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CandleInterval from(String value) {
        return Arrays.stream(values()).filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("Invalid CandleInternal: " + value));
    }
}
