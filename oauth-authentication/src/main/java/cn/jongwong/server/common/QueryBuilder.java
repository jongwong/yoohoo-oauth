package cn.jongwong.server.common;

import cn.jongwong.server.util.response.Page;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

public class QueryBuilder<T> {
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final Class<T> entityType;
    private final List<Criteria> criteriaList = new ArrayList<>();


    public QueryBuilder(R2dbcEntityTemplate r2dbcEntityTemplate, Class<T> entityType) {
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
        this.entityType = entityType;
    }

    // Add LIKE condition
    public QueryBuilder<T> addLikeCondition(String column, String value) {
        if (value != null && !value.isEmpty()) {
            criteriaList.add(Criteria.where(column).like("%" + value + "%"));
        }
        return this;
    }

    // Add EQUAL condition
    public QueryBuilder<T> addEqualCondition(String column, Object value) {
        if (value != null) {
            criteriaList.add(Criteria.where(column).is(value));
        }
        return this;
    }

    // Execute query with pagination
    public Mono<Page<T>> executeQuery(int page, int size) {
        // Build the query object
        Query query = Query.query(Criteria.from(criteriaList))
                .limit(size)
                .offset((long) (page - 1) * size);

        // Fetch the data
        Mono<List<T>> dataMono = r2dbcEntityTemplate.select(query, entityType).collectList();

        // Fetch the total count
        Query countQuery = Query.query(Criteria.from(criteriaList));
        Mono<Long> countMono = r2dbcEntityTemplate.count(countQuery, entityType);

        // Combine data and count into a Page object
        return Mono.zip(dataMono, countMono)
                .map(tuple -> new Page<>(tuple.getT1(), tuple.getT2(), page, size));
    }
}
