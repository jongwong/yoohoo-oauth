package cn.jongwong.server.util.response;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.lang.reflect.Field;
import java.util.UUID;

public class EntityUtils {

    /**
     * 获取带有 @Id 注解的字段名
     *
     * @param entity 实体对象
     * @return 主键字段名称
     */
    public static String getIdFieldName(Object entity) {
        var entityClass = entity.getClass();
        String idName = null;

        // 遍历类层次结构，直到 Object 类
        while (entityClass != null && entityClass != Object.class) {
            for (Field field : entityClass.getDeclaredFields()) {
                // 使用 field.getAnnotations() 获取所有注解，并检查是否包含 @Id 注解
                for (var annotation : field.getAnnotations()) {
                    // 打印字段注解类型，便于调试
                    if (annotation.annotationType().equals(Id.class)) {
                        idName = field.getName();  // 保存字段名称
                        break;  // 退出当前字段循环，避免继续遍历该类的其它字段
                    }
                }
                if (idName != null) {
                    break;  // 如果已经找到 @Id 字段，退出当前类的字段循环
                }
            }

            if (idName != null) {
                break;  // 如果找到 @Id 字段，退出类层次结构的循环
            }
            entityClass = entityClass.getSuperclass();  // 获取父类，继续查找
        }

        // 如果没有找到 @Id 注解，抛出异常
        if (idName == null) {
            throw new RuntimeException("No @Id field found in the class: " + entity.getClass().getName());
        }

        return idName;
    }

    /**
     * 获取主键字段的值
     *
     * @param entity 实体对象
     * @return 主键字段的值
     */
    public static String getIdValue(Object entity) {
        try {
            // 获取实体类的主键字段名
            String idFieldName = getIdFieldName(entity);
            // 使用反射获取主键字段的值
            Field idField = entity.getClass().getDeclaredField(idFieldName);
            idField.setAccessible(true);  // 设置字段可访问
            return (String) idField.get(entity);   // 返回字段的值
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error accessing id field", e);
        }
    }

    /**
     * 检查实体主键是否为 null，如果是，则生成 UUID 并设置到主键字段
     *
     * @param entity 实体对象
     */
    public static void ensureIdExists(Object entity) {
        try {
            // 获取实体类的主键字段名
            String idFieldName = getIdFieldName(entity);

            // 获取主键字段
            Field idField = entity.getClass().getDeclaredField(idFieldName);
            idField.setAccessible(true);

            // 检查主键字段值是否为 null 或空
            if (idField.get(entity) == null || idField.get(entity).toString().isEmpty()) {
                String generatedId = UUID.randomUUID().toString(); // 生成 UUID
                idField.set(entity, generatedId); // 设置生成的 UUID 到主键字段
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Error ensuring ID field", e);
        }
    }


    // 获取表名
    public static <T> String getTableNameFromEntityClass(Class<T> entityType) {
        System.out.printf("-------entityType-------%s%n", entityType);

        Table tableAnnotation = entityType.getAnnotation(Table.class);
        if (tableAnnotation != null) {
            String name = tableAnnotation.name();
            if (name.isEmpty()) {
                name = tableAnnotation.value();
            }
            if (name.isEmpty()) {
                throw new IllegalStateException("Table1 name must be defined via @Table annotation on entity class.");
            }
            return name;
        } else {
            throw new IllegalStateException("Table name must be defined via @Table annotation on entity class.");
        }
    }

    // 获取表名
    public static String getTableNameFromEntity(Object entityType) {

        return getTableNameFromEntityClass(entityType.getClass());
    }
}
