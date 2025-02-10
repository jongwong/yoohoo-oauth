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
    private List<String> rawFieldsList = new ArrayList<>();
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
        cloned.rawFieldsList = this.rawFieldsList;

        return cloned;
    }

    // 选择查询字段
    public static SqlBuilder select() {
        var sql = builder();
        sql.type = "SELECT";

        return sql;
    }



    // 拼接字段
    public SqlBuilder column(String columns) {
        if (!columns.isEmpty()) {
            String[] columnArray = columns.split(",");
            for (String column : columnArray) {
                this.selectFields.add(column.trim());
            }
        }
        return this;
    }

    public SqlBuilder field(String column, boolean rawField) {

        if (rawField) {
            this.rawFieldsList.add(column.trim().replaceAll("\\r\\n|\\n|\\r", ""));
        } else {
            this.column(column.trim());
        }

        return this;
    }


    // 拼接字段
    public SqlBuilder columns(String[] columns) {
        for (String column : columns) {
            this.column(column.trim());
        }
        return this;
    }



    // 拼接字段
    public SqlBuilder field(String columns, String alias) {
        if (!columns.isEmpty()) {
            this.selectFields.add((columns + " AS " + alias).trim());
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
        criteriaCallback.apply(criteria);

        return this;
    }

    private SqlBuilder addInnerCondition(String column, Object value, String operator, boolean isLeftLike, Boolean notNull) {
        // 检查是否为空
        if (!notNull && (value == null || (value instanceof String && ((String) value).trim().isEmpty()))) {
            return this;
        }

        // 处理数组或集合类型
        if (value instanceof List || value.getClass().isArray()) {
            List<Object> values;
            if (value instanceof List) {
                values = (List<Object>) value;
            } else {
                values = List.of((Object[]) value);
            }

            // 如果值列表为空，直接跳过
            if (values.isEmpty()) {
                return this;
            }

            // 生成占位符
            String placeholder = column + "_list";
            this.bind(placeholder, values);

            // IN 操作符的条件
            Criteria criteria = Criteria.where(column).in(values);
            criteriaList.add(criteria);

            return this;
        }

        // 处理普通值（非数组或集合）
        String trimmedValue = value != null ? value.toString().trim() : "";
        if (!trimmedValue.startsWith(":")) {
            var name = column.trim();
            trimmedValue = ":" + name;
            this.bind(name, value);
        }

        // 处理 LIKE 或其他运算符
        if (isLeftLike) {
            trimmedValue = "%" + trimmedValue;
        } else if ("like".equals(operator)) {
            trimmedValue = trimmedValue + "%";
        }

        var formatOperator = operator.trim().toLowerCase();
        Criteria criteria;
        switch (formatOperator) {
            case "like":
                criteria = Criteria.where(column).like(trimmedValue);
                break;
            case "<=":
                criteria = Criteria.where(column).lessThanOrEquals(trimmedValue);
                break;
            case ">=":
                criteria = Criteria.where(column).greaterThanOrEquals(trimmedValue);
                break;
            case "<":
                criteria = Criteria.where(column).lessThan(trimmedValue);
                break;
            case ">":
                criteria = Criteria.where(column).greaterThan(trimmedValue);
                break;
            default:
                criteria = Criteria.where(column).is(trimmedValue);
        }

        criteriaList.add(criteria);
        return this;
    }

    public SqlBuilder customCondition(String column, String operator, Object value) {
        return addInnerCondition(column, value, operator, false, false); // Regular equality check
    }

    public SqlBuilder in(String column, Object value) {
        return addInnerCondition(column, value, "in", false, false);
    }
    // eq condition (equals)
    public SqlBuilder eq(String column, Object value) {
        return addInnerCondition(column, value, "eq", false, false); // Regular equality check
    }

    // like condition (matches right side)
    public SqlBuilder like(String column, Object value) {
        return addInnerCondition(column, value, "like", false, false); // Right match (value%)
    }

    // leftLike condition (matches left side)
    public SqlBuilder leftLike(String column, Object value) {
        return addInnerCondition(column, value, "like", true, false); // Left match (%value)
    }

    // eq condition (equals)
    public SqlBuilder eq(String column, Object value, Boolean notNull) {
        return addInnerCondition(column, value, "eq", false, notNull); // Regular equality check
    }

    // like condition (matches right side)
    public SqlBuilder like(String column, Object value, Boolean notNull) {
        return addInnerCondition(column, value, "like", false, notNull); // Right match (value%)
    }

    // leftLike condition (matches left side)
    public SqlBuilder leftLike(String column, Object value, Boolean notNull) {
        return addInnerCondition(column, value, "like", true, notNull); // Left match (%value)
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
            var str = addAliasToFields(selectFields);
            // 如果str trim 后 第一个且最后一个是括号去掉括号
            if (str.trim().startsWith("(") && str.trim().endsWith(")")) {
                str = str.trim().substring(1, str.trim().length() - 1);
            }
            sqlBuilder.append(str);
        }
        this.rawFieldsList.forEach(rawField -> sqlBuilder.append(", ").append(rawField));

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

        if (!isCountQuery) {
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
        }


        String sql = sqlBuilder.toString();
        ;

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
        var str = criteriaList.stream()
                .map(criteria -> addAliasToField(criteria.toString()))
                .collect(Collectors.joining(" AND "));
        if (str.trim().isEmpty()) {
            return "";
        }
        return "WHERE " + str;
    }

    private String addAliasToField(String condition) {
        if (tableAlias != null && !condition.contains(".")) {
            condition = tableAlias + "." + condition;
        }

        // 不去掉括号，原来的正则表达式会保留括号
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
