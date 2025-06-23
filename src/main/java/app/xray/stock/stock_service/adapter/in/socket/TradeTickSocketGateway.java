package app.xray.stock.stock_service.adapter.in.socket;

import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.domain.TradeTick;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class TradeTickSocketGateway {

    private final SocketIOServer server;

    @PostConstruct
    public void init() {
        server.addConnectListener(client -> {
            log.info("[TradeTickSocketGateway.server.ConnectListener] client connected: sessionId = {}",
                    client.getSessionId());
        });

        server.addDisconnectListener(client -> {
            log.info("[TradeTickSocketGateway.server.DisconnectListener] client disconnected: sessionId = {}",
                    client.getSessionId());
        });

        server.addEventListener("joinRoom", String.class, (client, roomName, ackSender) -> {
            client.joinRoom(roomName);
            log.info("[TradeTickSocketGateway.server.EventListener] client joined room: sessionId = {}, roomName = {}",
                    client.getSessionId(), roomName);
        });
    }

    // 특정 stockId 방에 TradeTick 정보 broadcast
    public void sendTickToRoom(String symbol, TradeTick tick) {
        var room = server.getRoomOperations(symbol);

        // 방에 클라이언트가 1명 이상 있는 경우에만 전송
        if (!room.getClients().isEmpty()) {
            log.debug("✅ Broadcasting tick to {} clients in room {}", room.getClients().size(), symbol);
            room.sendEvent("tickUpdate", TradeTickMessage.from(tick));
        } else {
            log.debug("⏸️ No clients in room {} — skip broadcasting", symbol);
        }
    }
}
