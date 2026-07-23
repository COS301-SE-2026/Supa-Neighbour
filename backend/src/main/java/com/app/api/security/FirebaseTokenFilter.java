package com.app.api.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.Collections;


public class FirebaseTokenFilter extends OncePerRequestFilter {
    /**
     * Intercepts incoming HTTP requests and validates the Firebase ID token
     * present in the Authorization header.
     *
     * If a valid Bearer token is found, the authenticated user's UID is set
     * in the Spring Security context and the request proceeds.
     * If the token is missing or invalid, the filter returns a 401 Unauthorized
     * response and the request does not reach the controller.
     *
     * @param request     the incoming HTTP request
     * @param response    the HTTP response
     * @param filterChain the filter chain to pass the request through if authentication succeeds
     * @throws ServletException if a servlet error occurs
     * @throws IOException      if an I/O error occurs during filtering
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        System.out.println("FirebaseAuthenticationFilter: " + request.getRequestURI());
        String authHeader = request.getHeader("Authorization");
        if(authHeader != null && authHeader.startsWith("Bearer ")){
            String token = authHeader.substring(7);
            try{
                FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(decoded.getUid(), null, Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }catch(Exception e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }
}
