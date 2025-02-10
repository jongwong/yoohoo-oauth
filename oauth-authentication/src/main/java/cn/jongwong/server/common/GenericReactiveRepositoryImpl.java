package cn.jongwong.server.common;

import cn.jongwong.server.util.response.EntityUtils;
import cn.jongwong.server.util.response.Page;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.mapping.RelationalPersistentProperty;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.data.relational.repository.query.RelationalExampleMapper;
import org.springframework.data.util.Lazy;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class GenericReactiveRepositoryImpl<T, ID> extends SimpleR2dbcRepository<T, ID> implements GenericReactiveRepository<T, ID> {

    private final RelationalEntityInformation<T, ID> entity;
    private final R2dbcEntityOperations entityOperations;
    private final Lazy<RelationalPersistentProperty> idProperty;
    private final RelationalExampleMapper exampleMapper;
    private final DatabaseClient databaseClient;

    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
        super(entity, entityOperations, converter);
        this.entity = entity;
        this.entityOperations = entityOperations;
        this.idProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getRequiredIdProperty();
        });
        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
        this.databaseClient = entityOperations.getDatabaseClient();
    }

    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, DatabaseClient databaseClient, R2dbcConverter converter, ReactiveDataAccessStrategy accessStrategy) {
        super(entity, databaseClient, converter, accessStrategy);
        this.entity = entity;
        this.entityOperations = new R2dbcEntityTemplate(databaseClient, accessStrategy);
        this.idProperty = Lazy.of(() -> {
            return (RelationalPersistentProperty) ((RelationalPersistentEntity) converter.getMappingContext().getRequiredPersistentEntity(this.entity.getJavaType())).getRequiredIdProperty();
        });
        this.exampleMapper = new RelationalExampleMapper(converter.getMappingContext());
        this.databaseClient = databaseClient;
    }

    // 覆盖 insert 方法
    @Override
    public <S extends T> Mono<S> insert(S entity) {
        EntityUtils.ensureIdExists(entity);
        return entityOperations.insert((Class<S>) entity.getClass())
                .using(entity)
                .thenReturn(entity);
    }

    public <S extends T> Flux<T> saveRefAll(Iterable<S> entities) {
        return Flux.fromIterable(entities)
                .flatMap(entity -> {
                    String idValue = EntityUtils.getIdValue(entity);

                    // 如果主键值不为空，先查询数据库判断实体是否存在
                    if (idValue != null) {
                        return findById((ID) idValue)  // 查找数据库中是否存在这个主键
                                .flatMap(existingEntity -> {
                                    // 如果存在实体，则执行更新操作
                                    return super.save(entity);
                                })
                                .switchIfEmpty(insert(entity));  // 如果没有找到实体，则执行插入操作
                    } else {
                        return insert(entity);  // 如果主键值为空，直接插入
                    }
                });
    }


    private T instantiateEntity() {
        try {
            return this.entity.getJavaType().getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to instantiate entity", e);
        }
    }

    private void populateEntityFromRow(Row row, RowMetadata rowMetadata, T instance) {
        for (var field : instance.getClass().getDeclaredFields()) {
            String fieldName = field.getName();
            String rowFieldName = camelToSnakeCase(fieldName);
            try {



            if (rowMetadata.contains(rowFieldName)) {
                Object value = row.get(rowFieldName, field.getType());
                Method setter = findSetterMethod(instance.getClass(), field);
                if (setter != null) {

                        setter.invoke(instance, value);

                }
            }
            } catch (Exception e) {
                throw new RuntimeException("Failed to row value for field: " + rowFieldName, e);
            }
        }
    }

    private Method findSetterMethod(Class<?> classType, Field field) {
        String setterMethodName = "set" + field.getName().substring(0, 1).toUpperCase() + field.getName().substring(1);
        try {
            return classType.getMethod(setterMethodName, field.getType());
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    private String camelToSnakeCase(String camelCase) {
        StringBuilder snakeCase = new StringBuilder();
        for (char c : camelCase.toCharArray()) {
            if (Character.isUpperCase(c)) {
                if (!snakeCase.isEmpty()) {
                    snakeCase.append("_");
                }
                snakeCase.append(Character.toLowerCase(c));
            } else {
                snakeCase.append(c);
            }
        }
        return snakeCase.toString();
    }


    // 自定义方法：传入 ID 和 dslFn 方法来构建查询
    public <S extends T> Mono<T> findOneByDSL(ID id, java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction) {

        var tableName = this.entity.getTableName().toString();
        var sqlBuilder = SqlBuilder.select().from(tableName);
        sqlBuilder.eq("id", id);

        // 将 sqlBuilderFunction 应用到 SqlBuilder 实例
        sqlBuilderFunction.apply(sqlBuilder);


        sqlBuilder
                .findOne();

        return databaseClient.sql(sqlBuilder.toString())
                .map((row, metadata) -> {
                    T instance = instantiateEntity();
                    populateEntityFromRow(row, metadata, instance);
                    return instance;
                })
                .one();
    }


    public <S extends T> Mono<Page<T>> findPageByDSL(Integer page, Integer size, java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction) {

        var tableName = this.entity.getTableName().toString();
        var sqlBuilder = SqlBuilder.select().from(tableName);

        // 将 sqlBuilderFunction 应用到 SqlBuilder 实例
        sqlBuilderFunction.apply(sqlBuilder);


        var listSql = sqlBuilder.clone().limit(size).offset((page - 1) * size).toString();
        var countSql = sqlBuilder.clone().count().toString();

        Mono<List<T>> dataMono = databaseClient.sql(listSql)
                .map((row, metadata) -> {
                    T instance = instantiateEntity();
                    populateEntityFromRow(row, metadata, instance);
                    return instance;
                }).all()
                .collectList();
        Mono<Long> countMono = databaseClient.sql(countSql)
                .map((row, metadata) -> row.get(0, Long.class))
                .one();

        // 组合数据和总数，返回分页对象
        return Mono.zip(dataMono, countMono)
                .map(tuple -> new Page<>(tuple.getT1(), tuple.getT2(), page, size));
    }

    public <S extends T> Flux<T> findAllByDSL(java.util.function.Function<SqlBuilder, SqlBuilder> sqlBuilderFunction) {

        var tableName = this.entity.getTableName().toString();
        var sqlBuilder = SqlBuilder.select().from(tableName);

        // 将 sqlBuilderFunction 应用到 SqlBuilder 实例
        sqlBuilderFunction.apply(sqlBuilder);


        var listSql = sqlBuilder.clone().toString();

        return databaseClient.sql(listSql)
                .map((row, metadata) -> {
                    T instance = instantiateEntity();
                    populateEntityFromRow(row, metadata, instance);
                    return instance;
                }).all();

    }

}
