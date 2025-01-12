package cn.jongwong.server.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.relational.core.query.Criteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SqlBuilder {
    private static final Logger log = LoggerFactory.getLogger(SqlBuilder.class);

    private List<Criteria> criteriaList = new ArrayList<>();
    private String tableName;
    private String tableAlias;
    private List<String> selectFields = new ArrayList<>();
    private List<Join> joinClauseList = new ArrayList<>();
    private String orderByClause = "";
    private boolean isCountQuery = false;
    private boolean whereQuery = false;
    private Integer limit;
    private Integer offset;

    // 存储绑定的参数
    private Map<String, Object> parameters = new HashMap<>();

    public static SqlBuilder builder() {
        var re = new SqlBuilder();
        re.selectFields.add("*");
        return re;
    }

    public SqlBuilder select(String fields) {
        this.selectFields.clear();
        this.selectFields.add(fields);
        return this;
    }

    public SqlBuilder column(String column) {
        if (selectFields.isEmpty()) {
            this.selectFields.add(column);
        } else {
            this.selectFields.add(column);
        }
        return this;
    }

    public SqlBuilder appendColumn(String columns) {
        if (!columns.isEmpty()) {
            String[] columnArray = columns.split(",");
            for (String column : columnArray) {
                this.selectFields.add(column.trim());
            }
        }
        return this;
    }

    public SqlBuilder from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    public SqlBuilder as(String alias) {
        this.tableAlias = alias;
        return this;
    }

    // bind方法用于绑定参数
    public SqlBuilder bind(String paramName, Object value) {
        this.parameters.put(":" + paramName, value);
        return this;
    }

    // 修改where方法，让条件支持占位符
    public SqlBuilder where(Function<Criteria, Criteria> criteriaCallback) {
        Criteria criteria = Criteria.empty();
        criteria = criteriaCallback.apply(criteria);
        criteriaList.add(criteria);
        return this;
    }

    public SqlBuilder addEqualCondition(String column, Object value) {
        // 创建一个等式条件
        Criteria criteria = Criteria.where(column).is(value);

        criteriaList.add(criteria);

        return this;
    }

    public SqlBuilder withJoin(Function<Join, Join> joinCallback) {
        Join join = new Join();
        join = joinCallback.apply(join);
        this.joinClauseList.add(join);
        return this;
    }

    public SqlBuilder left() {
        return this.withJoin(join -> join.left());
    }

    public SqlBuilder inner() {
        return this.withJoin(join -> join.inner());
    }

    public SqlBuilder right() {
        return this.withJoin(join -> join.right());
    }

    public SqlBuilder sort(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    public SqlBuilder count() {
        this.isCountQuery = true;
        return this;
    }

    public SqlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    public SqlBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    public SqlBuilder findOne() {
        this.isCountQuery = false;
        return this.limit(1).offset(0);
    }

    // 构建最终的 SQL 字符串，并替换占位符
    public String toString() {
        StringBuilder sqlBuilder = new StringBuilder();

        // 构建 SELECT 子句
        if (isCountQuery) {
            sqlBuilder.append("SELECT COUNT(*)");
        } else {
            sqlBuilder.append("SELECT ");
            sqlBuilder.append(addAliasToFields(selectFields));
        }

        // 构建 FROM 子句
        sqlBuilder.append(" FROM ").append(getTableNameFromEntity());

        // 拼接 JOIN 子句
        for (Join join : joinClauseList) {
            sqlBuilder.append(" ").append(join.getJoinType())
                    .append(" ").append(join.getTable())
                    .append(" ON ").append(join.getOnCondition());
        }

        // 构建 WHERE 子句
        String whereClause = buildWhereClause();
        if (!whereClause.isEmpty()) {
            sqlBuilder.append(" ").append(whereClause);
        }

        // 构建 ORDER BY 子句
        String orderBy = buildOrderByClause();
        if (!orderBy.isEmpty()) {
            sqlBuilder.append(" ").append(orderBy);
        }

        // 构建 LIMIT 和 OFFSET
        if (limit != null) {
            sqlBuilder.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sqlBuilder.append(" OFFSET ").append(offset);
        }

        // 替换占位符
        String sql = sqlBuilder.toString();

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            sql = sql.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        log.debug("Generated SQL: " + sql);


        return sql;
    }

    private String buildWhereClause() {
        if (criteriaList.isEmpty()) {
            return "";
        }
        return "WHERE " + criteriaList.stream()
                .map(criteria -> addAliasToField(criteria.toString()))
                .collect(Collectors.joining(" AND "));
    }

    private String addAliasToField(String condition) {
        if (tableAlias != null && !condition.contains(".")) {
            condition = tableAlias + "." + condition;
        }

        condition = condition.replaceAll("\\(\\s*([^\\)]+)\\s*\\)", "$1");

        return condition;
    }

    private String addAliasToFields(List<String> fields) {
        return fields.stream()
                .map(this::addAliasToField)
                .collect(Collectors.joining(", "));
    }

    private String buildOrderByClause() {
        return orderByClause.isEmpty() ? "" : "ORDER BY " + orderByClause;
    }

    private String getTableNameFromEntity() {
        if (this.tableName != null) {
            return this.tableAlias != null ? this.tableName + " " + this.tableAlias : this.tableName;
        }
        return "unknown_table";
    }

    // Join 类
    public static class Join {
        private String joinType = "INNER JOIN";
        private String table;
        private String onCondition;

        public Join left() {
            this.joinType = "LEFT JOIN";
            return this;
        }

        public Join right() {
            this.joinType = "RIGHT JOIN";
            return this;
        }

        public Join inner() {
            this.joinType = "INNER JOIN";
            return this;
        }

        public Join table(String tableName) {
            this.table = tableName;
            return this;
        }

        public Join on(String onCondition) {
            this.onCondition = onCondition;
            return this;
        }

        public String getJoinType() {
            return joinType;
        }

        public String getTable() {
            return table;
        }

        public String getOnCondition() {
            return onCondition;
        }
    }
}
