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
    private Integer limit;
    private Integer offset;
    private Map<String, Object> parameters = new HashMap<>();
    private String type;

    public static SqlBuilder builder() {
        SqlBuilder re = new SqlBuilder();
        re.selectFields.add("*");
        return re;
    }

    // 克隆方法，防止串改
    protected SqlBuilder clone() {
        SqlBuilder cloned = new SqlBuilder();
        cloned.criteriaList = new ArrayList<>(this.criteriaList);
        cloned.selectFields = new ArrayList<>(this.selectFields);
        cloned.joinClauseList = new ArrayList<>(this.joinClauseList);
        cloned.parameters = new HashMap<>(this.parameters);
        cloned.tableName = this.tableName;
        cloned.tableAlias = this.tableAlias;
        cloned.orderByClause = this.orderByClause;
        cloned.isCountQuery = this.isCountQuery;
        cloned.limit = this.limit;
        cloned.offset = this.offset;
        cloned.type = this.type;

        return cloned;
    }

    // 选择查询字段
    public static SqlBuilder select() {
        var sql = builder();
        sql.type = "SELECT";
        
        return sql;
    }

    // 添加字段
    public SqlBuilder column(String column) {
        this.selectFields.add(column);
        return this;
    }

    // 拼接字段
    public SqlBuilder appendColumn(String columns) {
        if (!columns.isEmpty()) {
            String[] columnArray = columns.split(",");
            for (String column : columnArray) {
                this.selectFields.add(column.trim());
            }
        }
        return this;
    }

    // 设置表名
    public SqlBuilder from(String tableName) {
        this.tableName = tableName;
        return this;
    }

    // 设置别名
    public SqlBuilder as(String alias) {
        this.tableAlias = alias;
        return this;
    }

    // 绑定参数
    public SqlBuilder bind(String paramName, Object value) {
        this.parameters.put(":" + paramName, value);
        return this;
    }

    // 设置 WHERE 条件
    public SqlBuilder where(Function<Criteria, Criteria> criteriaCallback) {
        Criteria criteria = Criteria.empty();
        criteria = criteriaCallback.apply(criteria);
        this.criteriaList.add(criteria);
        return this;
    }

    // Common method to handle eq, like, and leftLike conditions
    private SqlBuilder addInnerCondition(String column, Object value, String operator, boolean isLeftLike) {
        String trimmedValue = value != null ? value.toString().trim() : ""; // Trim the value

        if (value == null || trimmedValue.isEmpty()) {
            return this;

        }
        // If the value does not start with a colon, add the column name prefixed with ":"
        if (!trimmedValue.startsWith(":")) {
            var name = column.trim();
            trimmedValue = ":" + name;
            this.bind(name, value); // Bind the value to the parameter
        }

        // Adjust the condition based on whether it's a left match (for LIKE)
        if (isLeftLike) {
            trimmedValue = "%" + trimmedValue; // Left match means value starts with % (e.g., "%value")
        } else if ("like".equals(operator)) {
            trimmedValue = trimmedValue + "%"; // Regular LIKE means value ends with % (e.g., "value%")
        }

        // Create the condition (eq, like, or leftLike) based on the operator
        Criteria criteria;
        if ("like".equals(operator)) {
            criteria = Criteria.where(column).like(trimmedValue);
        } else {
            criteria = Criteria.where(column).is(trimmedValue);
        }

        this.criteriaList.add(criteria); // Add the condition to the criteria list
        return this; // Return the builder itself for chaining
    }

    // eq condition (equals)
    public SqlBuilder eq(String column, Object value) {
        return addInnerCondition(column, value, "eq", false); // Regular equality check
    }

    // like condition (matches right side)
    public SqlBuilder like(String column, Object value) {
        return addInnerCondition(column, value, "like", false); // Right match (value%)
    }

    // leftLike condition (matches left side)
    public SqlBuilder leftLike(String column, Object value) {
        return addInnerCondition(column, value, "like", true); // Left match (%value)
    }

    // 设置 JOIN 子句
    public SqlBuilder withJoin(Function<Join, Join> joinCallback) {
        Join join = new Join();
        join = joinCallback.apply(join);
        this.joinClauseList.add(join);
        return this;
    }

    // 设置 LEFT JOIN
    public SqlBuilder left() {
        return this.withJoin(join -> join.left());
    }

    // 设置 INNER JOIN
    public SqlBuilder inner() {
        return this.withJoin(join -> join.inner());
    }

    // 设置 RIGHT JOIN
    public SqlBuilder right() {
        return this.withJoin(join -> join.right());
    }

    // 设置排序
    public SqlBuilder sort(String orderByClause) {
        this.orderByClause = orderByClause;
        return this;
    }

    // 设置查询计数
    public SqlBuilder count() {
        this.isCountQuery = true;
        return this;
    }

    // 设置 LIMIT
    public SqlBuilder limit(int limit) {
        this.limit = limit;
        return this;
    }

    // 设置 OFFSET
    public SqlBuilder offset(int offset) {
        this.offset = offset;
        return this;
    }

    // 生成单个查询（LIMIT 1）
    public SqlBuilder findOne() {
        return this.limit(1).offset(0);
    }

    // 生成 SQL 字符串
    public String toString() {
        StringBuilder sqlBuilder = new StringBuilder();

        if (isCountQuery) {
            sqlBuilder.append("SELECT COUNT(*)");
        } else {
            sqlBuilder.append(this.type).append(" ");
            sqlBuilder.append(addAliasToFields(selectFields));
        }

        sqlBuilder.append(" FROM ").append(getTableNameFromEntity());

        for (Join join : joinClauseList) {
            sqlBuilder.append(" ").append(join.getJoinType())
                    .append(" ").append(join.getTable())
                    .append(" ON ").append(join.getOnCondition());
        }

        String whereClause = buildWhereClause();
        if (!whereClause.isEmpty()) {
            sqlBuilder.append(" ").append(whereClause);
        }

        String orderBy = buildOrderByClause();
        if (!orderBy.isEmpty()) {
            sqlBuilder.append(" ").append(orderBy);
        }

        if (limit != null) {
            sqlBuilder.append(" LIMIT ").append(limit);
        }
        if (offset != null) {
            sqlBuilder.append(" OFFSET ").append(offset);
        }

        String sql = sqlBuilder.toString();

        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            sql = sql.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        log.debug("Generated SQL: " + sql);

        return sql;
    }

    // 其他辅助方法（如构建 WHERE 子句，排序等）
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
