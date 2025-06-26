package app.xray.stock.stock_service.adapter.in.socket;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SocketAuthorizationListener implements AuthorizationListener {

    @Value("${socketio.secret-key}")
    private String secretKey;

    @Override
    public boolean isAuthorized(HandshakeData handshakeData) {
        // query 에서 token 파라미터 추출
        String token = handshakeData.getSingleUrlParam("token");
        if (token == null || token.isBlank()) {
            return false;
        }
        return validateToken(token);
    }

    private boolean validateToken(String token) {
        return secretKey.equals(token);
    }
}
