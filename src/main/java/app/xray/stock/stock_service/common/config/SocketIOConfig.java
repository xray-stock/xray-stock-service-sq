package app.xray.stock.stock_service.common.config;

import app.xray.stock.stock_service.adapter.in.socket.SocketAuthorizationListener;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.Transport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {

    @Value("${socket.host:localhost}")
    private String host;

    @Value("${socket.port:9092}")
    private Integer port;

    @Bean
    public SocketIOServer socketIOServer(SocketAuthorizationListener authorizationListener) {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(host);
        config.setPort(port);
        config.setTransports(Transport.WEBSOCKET); // 강제 웹소켓 only
        config.setAuthorizationListener(authorizationListener);
        return new SocketIOServer(config);
    }

    @Bean
    public ApplicationRunner runner(SocketIOServer server) {
        return args -> {
            server.start();
        };
    }
}
