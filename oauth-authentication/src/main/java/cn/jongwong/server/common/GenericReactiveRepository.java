package cn.jongwong.server.common;

import cn.jongwong.server.util.response.Page;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
public interface GenericReactiveRepository<T, ID> extends R2dbcRepository<T, ID> {

    // 声明 insert 方法，由具体的实现类来提供实现
    <S extends T> Mono<S> insert(S entity);


    // 批量保存或更新实体
    <S extends T> Flux<T> saveRefAll(Iterable<S> entities);

    <S extends T> Mono<T> findOneByDSL(ID id, java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction);

    <S extends T> Mono<Page<T>> findPageByDSL(Integer page, Integer size, java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction);

    <S extends T> Flux<T> findAllByDSL(java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction);

}
