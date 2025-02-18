package com.example.sceneproject.demos.utils;


import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;

@Configuration
public class RedisConfig {
    @Bean
    public RedissonClient redisson(){
        // useSingleServer 单机模式，还存在其他模式
        Config config = new Config();
        config.useSingleServer().setAddress("redis://101.34.152.79:6378").setPassword("123456").setDatabase(1);
        config.useSingleServer().setAddress("redis://101.34.152.79:6378").setPassword("123456").setDatabase(1);
        return Redisson.create(config);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
//		1.创建 redisTemplate
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//		2.设置连接工厂
        redisTemplate.setConnectionFactory(redisConnectionFactory);
//		3.设置序列化工具
        GenericJackson2JsonRedisSerializer jsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
//		key 和 hashkey 采用 String 序列化
        redisTemplate.setKeySerializer(RedisSerializer.string());
        redisTemplate.setKeySerializer(RedisSerializer.string());
//		value 和 hashvalue 采用 json 序列化
        redisTemplate.setValueSerializer(jsonRedisSerializer);
        redisTemplate.setHashValueSerializer(jsonRedisSerializer);
        return  redisTemplate;
    };
}
