package cn.jongwong.server.config.security.common;

import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import reactor.core.publisher.Mono;

import java.time.Duration;

public class ReactiveRedisTokenStore {

    private final ReactiveRedisOperations<String, Object> redisOperations;

    public ReactiveRedisTokenStore(ReactiveRedisOperations<String, Object> redisOperations) {
        this.redisOperations = redisOperations;
    }

    public Mono<Void> save(OAuth2AccessToken token, Authentication authentication) {
        String key = "token:" + token.getTokenValue();
        return redisOperations.opsForValue().set(key, token)
                .then(redisOperations.expire(key, Duration.ofSeconds(token.getExpiresAt().getEpochSecond()))).then();
    }

    public Mono<OAuth2AccessToken> findByTokenValue(String tokenValue) {
        String key = "token:" + tokenValue;
        return redisOperations.opsForValue().get(key).map(obj -> (OAuth2AccessToken) obj);
    }

    public Mono<Void> remove(String tokenValue) {
        String key = "token:" + tokenValue;
        return redisOperations.delete(key).then();
    }
}