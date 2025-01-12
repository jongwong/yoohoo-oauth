package cn.jongwong.server.common;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public interface GenericReactiveRepository<T, ID> extends R2dbcRepository<T, ID> {

    // 声明 insert 方法，由具体的实现类来提供实现
    <S extends T> Mono<S> insert(S entity);


    // 批量保存或更新实体
    <S extends T> Flux<T> saveRefAll(Iterable<S> entities);

    // customFindOne 方法，传入 ID 和 xx 方法，返回查询结果
    <S extends T> Mono<T> findOneByDSL(ID id, java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction);

}
