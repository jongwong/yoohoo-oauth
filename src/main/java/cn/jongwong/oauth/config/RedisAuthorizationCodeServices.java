package cn.jongwong.oauth.config;


import com.baomidou.mybatisplus.core.toolkit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.token.store.redis.JdkSerializationStrategy;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RedisAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {
    private String prefix = "authorization:authorization_code:";

    private final RedisTemplate<String, Object> redisTemplate;

    private final JdkSerializationStrategy serializationStrategy = new JdkSerializationStrategy();

    public RedisAuthorizationCodeServices(RedisTemplate redisTemplate) {
        Assert.notNull(redisTemplate, "redis connection factory required");
        this.redisTemplate = redisTemplate;
    }

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        String key = prefix + code;
        redisTemplate.opsForValue().set(key, serializationStrategy.serialize(authentication), 2, TimeUnit.MINUTES);
    }

    @Override
    protected OAuth2Authentication remove(String code) {
        OAuth2Authentication auth2Authentication =
                serializationStrategy.deserialize(
                        (byte[]) redisTemplate.opsForValue().get(prefix + code), OAuth2Authentication.class);
        redisTemplate.delete(prefix + code);
        return auth2Authentication;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
