package app.xray.stock.stock_service.adapter.in.socket.dto;

import java.util.Map;

public record AckMessage(
        boolean success,
        String message
) {
    public static AckMessage ok() {
        return new AckMessage(true, null);
    }

    public static AckMessage fail(String message) {
        return new AckMessage(false, message);
    }

    public Map<String, Object> toMap() {
        return Map.of(
                "success", success,
                "message", message
        );
    }
}
