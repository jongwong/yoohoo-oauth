package cn.jongwong.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    // 这里假设你已经配置好 Redis 的连接池
    @Autowired
    private StringRedisTemplate redisTemplate;

    public Mono<String> get(String key) {
        return Mono.fromCallable(() -> redisTemplate.opsForValue().get(key))
                .mapNotNull(value -> value); // 设置默认值
    }

    public Mono<Void> set(String key, String value, long expirationInSeconds) {
        return Mono.fromRunnable(() -> redisTemplate.opsForValue().set(key, value, expirationInSeconds, TimeUnit.SECONDS));
    }


}
