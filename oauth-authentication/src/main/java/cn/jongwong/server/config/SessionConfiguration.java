package cn.jongwong.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@EnableRedisWebSession
@Configuration
public class SessionConfiguration {


//    @Bean
//    @Primary
//    public RedisIndexedSessionRepository customSessionRepository(RedisOperations
//                                                                         <Object,
//                                                                                 Object> redisTemplate) {
//        RedisIndexedSessionRepository sessionRepository = new RedisIndexedSessionRepository(redisTemplate);
//        sessionRepository.setDefaultMaxInactiveInterval(1800); // Set session expiration time (in seconds)
//        return sessionRepository;
//    }
//
//    @Bean
//    public SessionRepositoryFilter<?> springSessionRepositoryFilter(RedisIndexedSessionRepository sessionRepository) {
//        return new SessionRepositoryFilter<>(sessionRepository);
//    }

}