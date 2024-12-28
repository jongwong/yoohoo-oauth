package cn.jongwong.server.common;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface GenericReactiveRepository<T, ID> extends R2dbcRepository<T, ID> {

    // 声明 insert 方法，由具体的实现类来提供实现
    <S extends T> Mono<S> insert(S entity);


}
