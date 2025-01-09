package cn.jongwong.server.common;

import cn.jongwong.server.util.response.Page;
import io.r2dbc.spi.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder<T> {
    private static final Logger log = LoggerFactory.getLogger(QueryBuilder.class);

    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final DatabaseClient databaseClient;
    private final Class<T> entityType;
    private final List<Criteria> criteriaList = new ArrayList<>();
    private String tableName;
    private String selectFields = "*"; // 默认查询所有字段
    private List<String> joinClauseList = new ArrayList<>(); // 联表查询
    private String orderByClause = ""; // ORDER BY 子句
    private int page = 1;
    private int size = 10;
    private FieldMappingCallback<T> fieldMappingCallback;

    public QueryBuilder(R2dbcEntityTemplate r2dbcEntityTemplate, Class<T> entityType) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.databaseClient = r2dbcEntityTemplate.getDatabaseClient();
        this.entityType = entityType;
    }

    // 设置表名
    public QueryBuilder<T> fromTable(String tableName) {
        this.tableName = tableName;
        return this;
    }

    // 设置 SELECT 字段
    public QueryBuilder<T> selectFields(String fields) {
        this.selectFields = fields;
        return this;
    }

    public QueryBuilder<T> withJoin(String joinClause) {
        joinClauseList.add(joinClause);
        return this;
    }

    // 添加 WHERE 子句条件
    public QueryBuilder<T> addEqualCondition(String column, Object value) {
        if (value != null) {
            criteriaList.add(Criteria.where(column).is(value));
        }
        return this;
    }

    // 设置回调接口
    public QueryBuilder<T> withFieldMapping(FieldMappingCallback<T> fieldMappingCallback) {
        this.fieldMappingCallback = fieldMappingCallback;
        return this;
    }

    // 添加 LIKE 条件
    public QueryBuilder<T> addLikeCondition(String column, String value) {
        if (value != null && !value.isEmpty()) {
            criteriaList.add(Criteria.where(column).like("%" + value + "%"));
        }
        return this;
    }

    // 设置排序
    public QueryBuilder<T> addSort(String orderByClause) {
        // 按逗号分割字符串
        var parts = orderByClause.split(",");

        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid orderByClause format. Expected format: 'field,asc/desc'");
        }

        // 保留字段名不变，将排序关键字转为大写
        var field = parts[0].trim();
        var order = parts[1].trim().toUpperCase();

        // 拼接成 "字段名 排序关键字"
        var result = String.join(" ", field, order);


        this.orderByClause = result;
        return this;
    }

    // 设置分页
    public QueryBuilder<T> paginate(int page, int size) {
        this.page = page;
        this.size = size;
        return this;
    }

    // 执行查询
    public Mono<Page<T>> exec() {
        return executeAdvancedQuery();
    }

    // 复杂查询使用 DatabaseClient 执行自定义 SQL
    private Mono<Page<T>> executeAdvancedQuery() {
        String curTableName = getTableNameFromEntity(entityType);
        StringBuilder sqlBuilder = new StringBuilder();

// 构建 FROM 和 JOIN 子句
        sqlBuilder.append(" FROM ").append(curTableName);

        for (String join : joinClauseList) {
            sqlBuilder.append(" JOIN ").append(join);  // 拼接每个 JOIN 子句
        }

// 构建 WHERE 子句
        String whereClause = buildWhereClause();
        if (!whereClause.isEmpty()) {
            sqlBuilder.append(" ").append(whereClause);
        }

// 构建 ORDER BY 子句
        String orderByClause = buildOrderByClause();
        if (!orderByClause.isEmpty()) {
            sqlBuilder.append(" ").append(orderByClause);
        }

        StringBuilder sqlSelectBuilder = new StringBuilder();
        // 构建 SELECT 子句
        sqlSelectBuilder.append("SELECT ");
        sqlSelectBuilder.append(selectFields.isEmpty() ? curTableName + ".*" : selectFields);


// 构建分页查询语句
        String sql = sqlSelectBuilder.append(' ').append(sqlBuilder).append(" LIMIT ").append(size).append(" OFFSET ").append((page - 1) * size).toString();
        log.debug("QueryBuilder Select:" + sql);

// 执行查询
        Mono<List<T>> dataMono = databaseClient.sql(sql)
                .map((row, metadata) -> {
                    var data = r2dbcEntityTemplate.getConverter().read(entityType, row);
                    if (this.fieldMappingCallback != null) {
                        return this.fieldMappingCallback.mapFields(row, data);
                    } else {
                        return data;

                    }
                })
                .all()
                .collectList();

// 构建 COUNT 查询语句 (复用 SQL 构建部分，不包含 LIMIT 和 OFFSET)
        String countSql = "SELECT COUNT(*) " + sqlBuilder;
        log.debug("QueryBuilder Count Select:" + countSql);
        Mono<Long> countMono = databaseClient.sql(countSql)
                .map((row, metadata) -> row.get(0, Long.class))
                .one();

        // 组合数据和总数，返回分页对象
        return Mono.zip(dataMono, countMono)
                .map(tuple -> new Page<>(tuple.getT1(), tuple.getT2(), page, size));
    }

    // 构建 WHERE 子句
    private String buildWhereClause() {
        if (criteriaList.isEmpty()) {
            return "";
        }

        return "WHERE " + criteriaList.stream()
                .map(Criteria::toString)
                .collect(Collectors.joining(" AND "));
    }

    // 构建 ORDER BY 子句
    private String buildOrderByClause() {
        return orderByClause.isEmpty() ? "" : "ORDER BY " + orderByClause;
    }

    // 获取表名
    private String getTableNameFromEntity(Class<T> entityType) {
        if (this.tableName != null) {
            return this.tableName;
        }

        Table tableAnnotation = entityType.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            String name = tableAnnotation.name();
            if (name.isEmpty()) {
                name = tableAnnotation.value();
            }
            if (name.isEmpty()) {
                throw new IllegalStateException("Table name must be defined via @Table annotation on entity class.");
            }
            return name;
        } else {
            throw new IllegalStateException("Table name must be defined via @Table annotation on entity class.");
        }
    }

    public interface FieldMappingCallback<T> {
        T mapFields(Row row, T entity);
    }
}
