package app.xray.stock.stock_service.adapter.in.socket;

import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.domain.TradeTick;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

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
    public void sendTickToRoom(String stockId, Instant start, Instant end, List<TradeTick> tradeTicks) {
        var room = server.getRoomOperations(stockId);
        // 방에 클라이언트가 1명 이상 있는 경우에만 전송
        if (!room.getClients().isEmpty()) {
            log.debug("✅ Broadcasting tradeTicks to {} clients in room {}", room.getClients().size(), stockId);
            room.sendEvent("tickUpdate", TradeTickMessage.of(stockId, start, end, tradeTicks));
        } else {
            log.debug("⏸️ No clients in room {} — skip broadcasting", stockId);
        }
    }
}
