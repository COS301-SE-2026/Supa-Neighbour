package com.app.api.config;
import com.app.api.services.FirebaseAuthService; // adjust to your actual FirebaseAuthService package
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
 
import java.security.Principal;
import java.util.Map;
/**
 * Runs during the initial HTTP handshake (before the connection is upgraded
 * to WebSocket). This is where we validate the Firebase ID token, since
 * STOMP CONNECT frames don't carry a standard Authorization header the way
 * REST requests do -- Flutter passes the token as a query param instead:
 *
 *   ws://<host>/ws?token=<firebase_id_token>
 *
 * On success, we stash a Principal in the handshake attributes so it flows
 * through to the STOMP session (picked up in StompAuthChannelInterceptor).
 *
 * If validation fails here, we return false and the handshake is rejected
 * outright (client gets a failed connection, not an authenticated-but-empty
 * session).
 */
@Component
public class StompHandshakeInterceptor implements HandshakeInterceptor {
    
    private final FirebaseAuthService firebaseAuthService;

    @Autowired
    public StompHandshakeInterceptor(FirebaseAuthService firebaseAuthService){
        this.firebaseAuthService = firebaseAuthService;
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Map<String, Object> attributes){
        String token = extractToken(request);
        if(token == null || token.isBlank()){
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }

        try{
            int userId = firebaseAuthService.getUserIdFromToken(token);
            attributes.put("userId", String.valueOf(userId));
            return true;
        }catch(Exception e){
            response.setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
            return false;
        }
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,WebSocketHandler wsHandler, Exception exception){
        // no op
    }

    
    private String extractToken(ServerHttpRequest request){
        if(request instanceof ServletServerHttpRequest servletRequest){
            return servletRequest.getServletRequest().getParameter("token");
        }

        return null;
    }
}
