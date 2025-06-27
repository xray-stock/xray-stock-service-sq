package app.xray.stock.stock_service.adapter.in.socket;

import com.corundumstudio.socketio.AuthorizationListener;
import com.corundumstudio.socketio.HandshakeData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;

@Component
public class SocketAuthorizationListener implements AuthorizationListener {

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key key;

    @PostConstruct
    public void init() {
        // secret 값에서 Key 객체 생성 (HMAC-SHA)
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

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
        try {
            // 1. 토큰 파싱 및 서명 검증 (이게 통과시 유효)
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            // 2. 여기서 추가적인 claim 검사도 가능 (예: 만료, issuer 등)
            return true;
        } catch (Exception e) {
            // 파싱 실패 (유효하지 않은 토큰)
            return false;
        }
    }
}
