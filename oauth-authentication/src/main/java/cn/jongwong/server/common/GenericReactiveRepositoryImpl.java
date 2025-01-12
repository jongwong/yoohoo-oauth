package cn.jongwong.server.common;

import cn.jongwong.server.util.response.EntityUtils;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class GenericReactiveRepositoryImpl<T, ID> extends SimpleR2dbcRepository<T, ID> implements GenericReactiveRepository<T, ID> {


    private final R2dbcEntityOperations template;
    private final RelationalEntityInformation<T, ID> entityInformation;


    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
        super(entity, entityOperations, converter);
        this.template = entityOperations;
        this.entityInformation = entity;
    }

    public GenericReactiveRepositoryImpl(RelationalEntityInformation<T, ID> entity, DatabaseClient databaseClient, R2dbcConverter converter, ReactiveDataAccessStrategy accessStrategy) {
        super(entity, databaseClient, converter, accessStrategy);
        this.template = new R2dbcEntityTemplate(databaseClient, accessStrategy);
        this.entityInformation = entity;
    }

    // 覆盖 insert 方法
    @Override
    public <S extends T> Mono<S> insert(S entity) {

        EntityUtils.ensureIdExists(entity);


        // 如果 ID 为 null，则执行插入操作
        return template.insert((Class<S>) entity.getClass())
                .using(entity)
                .thenReturn(entity);
    }

    public <S extends T> Flux<T> saveRefAll(Iterable<S> entities) {
        // 将传入的实体列表转换成 Flux 来处理
        return Flux.fromIterable(entities)
                .flatMap(entity -> {
                    // 动态获取实体的主键字段和对应的值（使用反射）

                    // 获取主键字段名
                    String idValue = EntityUtils.getIdValue(entity);

                    if (idValue != null) {

                        // 使用 Update 构建更新操作
                        return super.save(entity);
                    } else {
                        // 如果没有主键，则执行插入操作
                        return insert(entity);
                    }
                });
    }


//    // 自定义方法：传入 ID 和 dslFn 方法来构建查询
//    public <S extends T> Mono<T> findOneByDSL(ID id, java.util.function.Function<org.jooq.SelectConditionStep, SelectConditionStep> dslFn) {
//
//        // 动态创建一个 DSLContext 实例
//        DSLContext dslContext = DSL.using(databaseClient.getConnectionFactory());
//
//
//        var sqlBase = dslContext.selectFrom(this.entityInformation.getTableName().toString());
//
//
//        // 构造 SQL 查询
//        var sql =
//                sqlBase.where("id = ?", id);
//
//        // 获取生成的 SQL 查询
//
//        // 使用传入的 dslFn 来构建查询
//        var sqlBaseFormat = dslFn.apply(sql);
//        // 打印 SQL 查询调试信息
//        System.out.println("Generated SQL: " + sqlBaseFormat.getSQL());
//
//        // 执行查询并返回查询结果
//        return databaseClient.sql(sqlBaseFormat.getSQL())
//                .bind(0, id) // 将 id 绑定到查询中
//                .map((row, metadata) -> {
//                    // 这里可以根据返回结果的行来映射实体类
//                    T result = MapperUtil.fromRow(row, this.entityInformation.getJavaType()); // 假设 mapRowToEntity 方法将 SQL 行映射为实体对象
//                    return result;
//                })
//                .one(); // 返回查询结果（Mono）
//    }


}
