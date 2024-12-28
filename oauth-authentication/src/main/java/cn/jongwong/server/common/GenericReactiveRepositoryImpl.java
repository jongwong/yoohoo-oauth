package cn.jongwong.server.common;

import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

public class GenericReactiveRepositoryImpl<T, ID> extends SimpleR2dbcRepository<T, ID> implements GenericReactiveRepository<T, ID> {


    private final R2dbcEntityOperations entityOperations;


    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
        super(entity, entityOperations, converter);
        this.entityOperations = entityOperations;
    }

    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, DatabaseClient databaseClient, R2dbcConverter converter, ReactiveDataAccessStrategy accessStrategy) {
        super(entity, databaseClient, converter, accessStrategy);
        this.entityOperations = new R2dbcEntityTemplate(databaseClient, accessStrategy);
    }

    // 覆盖 insert 方法
    @Override
    public <S extends T> Mono<S> insert(S entity) {
        // 如果 ID 为 null，则执行插入操作
        return entityOperations.insert((Class<S>) entity.getClass())
                .using(entity)
                .thenReturn(entity);
    }
}
