package app.xray.stock.stock_service.common.type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.Arrays;

@RequiredArgsConstructor
public enum CandleIntervalType {

    FIVE_SEC("5s", Duration.ofSeconds(5)),
    ONE_MIN("1m", Duration.ofMinutes(1)),
    FIVE_MIN("5m", Duration.ofMinutes(5)),
    ONE_DAY("1d", Duration.ofDays(1)),
    ONE_WEEK("1w", Duration.ofDays(7));

    private final String value;
    @Getter
    private final Duration duration;

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static CandleIntervalType convertOrDefaultNull(String value) {
        return Arrays.stream(values()).filter(v -> v.value.equalsIgnoreCase(value))
                .findFirst().orElse(null);
    }
}
