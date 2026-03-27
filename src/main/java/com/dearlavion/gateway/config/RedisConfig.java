package com.dearlavion.gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
public class RedisConfig {

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port) {
        System.out.println("[Gateway Redis] host: " + host);
        System.out.println("[Gateway Redis] port: " + port);
        return new LettuceConnectionFactory(new RedisStandaloneConfiguration(host, port));
    }
}