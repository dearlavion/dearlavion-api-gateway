package com.dearlavion.gateway.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.security.Key;

@Configuration
public class GatewayConfig {

    private static final String SECRET_KEY = "5B6F7D3E2A9C4B8E0A1F6D9B3E7A2C9D4F8E5B6C3A7B1D6F4C9A3E8D2B5F7A1";

    @Bean
    @Primary
    public KeyResolver userOrIpKeyResolverBean() {
        return exchange -> {

            System.out.println("Found userOrIpKeyResolverBean");

            String token = exchange.getRequest()
                    .getHeaders()
                    .getFirst("Authorization");

            if (token != null && token.startsWith("Bearer ")) {
                String user = extractUser(token);
                if (user != null) {
                    System.out.println("[userOrIpKeyResolverBean] key via username");
                    return Mono.just(user);
                }
            }

            System.out.println("[userOrIpKeyResolverBean] key via IP");
            // 🔥 fallback to IP
            String ip = exchange.getRequest()
                    .getRemoteAddress()
                    .getAddress()
                    .getHostAddress();

            return Mono.just(ip);
        };
    }

    private String extractUser(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // ✅ Use subject (standard)
            return claims.getSubject();

        } catch (Exception e) {
            return null; // invalid token
        }
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
