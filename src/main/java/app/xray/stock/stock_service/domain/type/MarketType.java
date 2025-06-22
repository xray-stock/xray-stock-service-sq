package app.xray.stock.stock_service.domain.type;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.*;
import java.util.Locale;
import java.util.Set;

/**
 * MarketType 는 시장 구분을 정의하는 enum 입니다.
 * <pre>
 * - decimalPlaces: 해당 시장에서 사용하는 기본 소숫점 자리수
 * </pre>
 */
@Getter
@RequiredArgsConstructor
public enum MarketType {
    KOSPI(
            Locale.KOREA,
            ZoneId.of("Asia/Seoul"),
            0,
            LocalTime.of(9, 0),
            LocalTime.of(15, 30),
            30.0
    ),
    NASDAQ(
            Locale.US,
            ZoneId.of("America/New_York"),
            2,
            LocalTime.of(9, 30),
            LocalTime.of(16, 0),
            null
    );

    private final Locale locale;
    private final ZoneId zoneId;
    private final int decimalPlaces;
    private final LocalTime marketOpenTime;
    private final LocalTime marketCloseTime;
    private final Double limitUpRatePercent;

    public boolean isLimitUp(double changeRate) {
        return limitUpRatePercent != null && changeRate >= (limitUpRatePercent * 0.99); // 29.7 for 30%
    }

    /**
     * 현재 시간이 장중인지 확인 (해당 시장 기준)
     */
    public boolean isMarketOpenNow(Instant now) {
        ZonedDateTime zonedNow = now.atZone(zoneId);
        LocalTime localTime = zonedNow.toLocalTime();
        return !isHoliday(zonedNow.toLocalDate()) && !localTime.isBefore(marketOpenTime) && localTime.isBefore(marketCloseTime);
    }

    public boolean isHoliday(LocalDate date) {
        return isWeekend(date) || isNationalHoliday(date);
    }

    private boolean isWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    private boolean isNationalHoliday(LocalDate date) {
        return switch (this) {
            // TODO 리스트업 - 추석 설날 음력 변수.. ㅠ -> 행정안전부 공휴일 API 적용해야 정확
            case KOSPI -> Set.of(
                    LocalDate.of(2025, 1, 1),   // 신정
                    LocalDate.of(2025, 3, 1),   // 삼일절
                    LocalDate.of(2025, 6, 6),   // 현충일 등등
                    LocalDate.of(2025, 12, 25),   // 크리스마스
                    LocalDate.of(2025, 12, 31)    // 연말
            ).contains(date);
            case NASDAQ -> Set.of(
                    LocalDate.of(2025, 7, 4),   // 미국 독립기념일
                    LocalDate.of(2025, 12, 25)  // 크리스마스
            ).contains(date);
        };
    }

    public boolean isEarlyClose(LocalDate date) {
        if (this == NASDAQ) {
            return Set.of(
                    LocalDate.of(2025, 7, 3),
                    LocalDate.of(2025, 11, 28),
                    LocalDate.of(2025, 12, 24)
            ).contains(date);
        }
        return false;
    }
}
