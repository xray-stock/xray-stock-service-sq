package app.xray.stock.stock_service.adapter.in.socket;

import app.xray.stock.stock_service.adapter.in.socket.dto.AckMessage;
import app.xray.stock.stock_service.adapter.in.socket.dto.TradeTickMessage;
import app.xray.stock.stock_service.application.port.in.CheckEnableStockUseCase;
import app.xray.stock.stock_service.domain.TradeTick;
import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Log4j2
@Component
@RequiredArgsConstructor
public class TradeTickSocketGateway {

    private final SocketIOServer server;
    private final CheckEnableStockUseCase checkEnableStockUseCase;

    @PostConstruct
    public void init() {
        // [사용자 연결 성공 처리]
        server.addConnectListener(client -> {
            log.info("[TradeTickSocketGateway.server.ConnectListener] client connected: sessionId = {}",
                    client.getSessionId());
        });

        // [사용자 연결 종료 처리]
        server.addDisconnectListener(client -> {
            log.info("[TradeTickSocketGateway.server.DisconnectListener] client disconnected: sessionId = {}",
                    client.getSessionId());
        });

        // [방입장 처리]
        server.addEventListener("joinRoom", String.class, (client, stockIdForJoiningRoom, ackSender) -> {

            // client: 누가 요청했는지에 대한 정보 (세션, 방 가입/탈퇴, 메시지 전송)
            // ackSender: 요청에 대해 응답을 줄 수 있는 핸들러 (ACK 응답 채널) -> Client 콜백 내 ACK 응답 활용

            if (!checkEnableStockUseCase.check(stockIdForJoiningRoom, Instant.now())) {
                log.warn("Client attempted to join invalid stockId: {}", stockIdForJoiningRoom);
                ackSender.sendAckData(
                        AckMessage.fail(String.format("error: invalid or disabled stockId [%s]",
                                stockIdForJoiningRoom)));
                return;
            }

            client.joinRoom(stockIdForJoiningRoom);
            ackSender.sendAckData(AckMessage.ok());
            log.info("[TradeTickSocketGateway.server.EventListener] client joined room: sessionId = {}, stockIdForJoiningRoom = {}",
                    client.getSessionId(), stockIdForJoiningRoom);
        });

        // [방나가기 처리]
        server.addEventListener("leaveRoom", String.class, (client, stockIdForLeavingRoom, ackSender) -> {
            if (client.getAllRooms().contains(stockIdForLeavingRoom)) {
                client.leaveRoom(stockIdForLeavingRoom);
                log.info("[TradeTickSocketGateway.server.EventListener] client left room: sessionId = {}, stockId = {}",
                        client.getSessionId(), stockIdForLeavingRoom);
                ackSender.sendAckData(AckMessage.ok());
            } else {
                log.warn("Client attempted to leave a room they are not part of: {}", stockIdForLeavingRoom);
                ackSender.sendAckData(AckMessage.fail("Not a member of the specified room: " + stockIdForLeavingRoom));
            }
        });
    }

    // 특정 stockId 방에 TradeTick 정보 broadcast
    public void sendTickToRoom(String stockId, Instant start, Instant end, List<TradeTick> tradeTicks) {
        var room = server.getRoomOperations(stockId);

        if (room.getClients().isEmpty()) {
            log.debug("⏸️ No clients in room {} — skip broadcasting", stockId);
            return;
        }

        // 방에 클라이언트가 1명 이상 있는 경우에만 전송
        log.debug("✅ Broadcasting tradeTicks to {} clients in room {}", room.getClients().size(), stockId);
        room.sendEvent("tickUpdate", TradeTickMessage.of(stockId, start, end, tradeTicks));
    }

    // 특정 stockId 방에 TradeTick 정보 broadcast
    public void sendTickToRoom(TradeTickMessage tradeTickMessage) {
        String stockId = tradeTickMessage.stockId();
        var room = server.getRoomOperations(stockId);

        if (room.getClients().isEmpty()) {
            log.debug("⏸️ No clients in room {} — skip broadcasting", tradeTickMessage.stockId());
            return;
        }

        // 방에 클라이언트가 1명 이상 있는 경우에만 전송
        log.debug("✅ Broadcasting tradeTicks to {} clients in room {}", room.getClients().size(), stockId);
        room.sendEvent("tickUpdate", tradeTickMessage);
    }


}
