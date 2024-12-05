package cn.jongwong.server.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.core.context.SecurityContext;

@Configuration
public class ReactiveRedisConfig {

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 配置 ReactiveRedisTemplate，指定 SecurityContext 类型
     *
     * @param factory Redis连接工厂
     * @return ReactiveRedisTemplate 实例
     */
    @Bean
    public ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate(ReactiveRedisConnectionFactory factory) {

        // 使用 Jackson2JsonRedisSerializer 序列化 SecurityContext 对象
        Jackson2JsonRedisSerializer<SecurityContext> jackson2JsonRedisSerializer =
                new Jackson2JsonRedisSerializer<>(SecurityContext.class);
        jackson2JsonRedisSerializer.setObjectMapper(objectMapper); // 设置 ObjectMapper

        // 使用 StringRedisSerializer 序列化 Redis 的 key（String）
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

        // 构建 RedisSerializationContext，指定 key 和 value 的序列化方式
        RedisSerializationContext<String, SecurityContext> serializationContext = RedisSerializationContext
                .<String, SecurityContext>newSerializationContext(stringRedisSerializer)
                .key(stringRedisSerializer)
                .value(jackson2JsonRedisSerializer)
                .hashKey(stringRedisSerializer)
                .hashValue(jackson2JsonRedisSerializer)
                .build();

        // 创建并返回 ReactiveRedisTemplate 实例
        return new ReactiveRedisTemplate<>(factory, serializationContext);
    }

    /**
     * 对 ReactiveRedisTemplate 中字符串类型数据的操作
     *
     * @param reactiveRedisTemplate ReactiveRedisTemplate 实例
     * @return ReactiveValueOperations 实例
     */
    @Bean
    public ReactiveValueOperations<String, SecurityContext> reactiveValueOperations(ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate) {
        return reactiveRedisTemplate.opsForValue();
    }

    /**
     * 对 ReactiveRedisTemplate 中哈希类型数据的操作
     *
     * @param reactiveRedisTemplate ReactiveRedisTemplate 实例
     * @return ReactiveHashOperations 实例
     */
    @Bean
    public ReactiveHashOperations<String, String, SecurityContext> reactiveHashOperations(ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate) {
        return reactiveRedisTemplate.opsForHash();
    }

    /**
     * 对 ReactiveRedisTemplate 中链表类型数据的操作
     *
     * @param reactiveRedisTemplate ReactiveRedisTemplate 实例
     * @return ReactiveListOperations 实例
     */
    @Bean
    public ReactiveListOperations<String, SecurityContext> reactiveListOperations(ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate) {
        return reactiveRedisTemplate.opsForList();
    }

    /**
     * 对 ReactiveRedisTemplate 中无序集合类型数据的操作
     *
     * @param reactiveRedisTemplate ReactiveRedisTemplate 实例
     * @return ReactiveSetOperations 实例
     */
    @Bean
    public ReactiveSetOperations<String, SecurityContext> reactiveSetOperations(ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate) {
        return reactiveRedisTemplate.opsForSet();
    }

    /**
     * 对 ReactiveRedisTemplate 中有序集合类型数据的操作
     *
     * @param reactiveRedisTemplate ReactiveRedisTemplate 实例
     * @return ReactiveZSetOperations 实例
     */
    @Bean
    public ReactiveZSetOperations<String, SecurityContext> reactiveZSetOperations(ReactiveRedisTemplate<String, SecurityContext> reactiveRedisTemplate) {
        return reactiveRedisTemplate.opsForZSet();
    }
}
