package cn.jongwong.server.common;

import io.r2dbc.spi.Row;
import org.springframework.data.relational.core.mapping.Column;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperUtil {

    // 用于缓存字段的列名，以提高性能
    private static final Map<String, String> columnNameCache = new ConcurrentHashMap<>();

    public static <T> T fromRow(Row row, Class<T> clazz) {
        try {
            // Create a new instance of the entity class
            T instance = clazz.getDeclaredConstructor().newInstance();

            // Iterate through all declared fields in the class
            for (Field field : clazz.getDeclaredFields()) {
                // If the field is annotated with @Column, map the value
                if (field.isAnnotationPresent(Column.class)) {
                    Column column = field.getAnnotation(Column.class);
                    String columnName = column.value(); // Get the column name from the annotation

                    // Retrieve the corresponding value from the Row object
                    Object value = row.get(columnName, field.getType());

                    // Handle special cases for types like String[], List<String>, etc.
                    if (value != null) {
                        // Handle String[] case (e.g., for VARCHAR that stores comma-separated values)
                        if (field.getType().equals(String[].class) && value instanceof String) {
                            value = ((String) value).split(",");  // Split the CSV string into an array
                        }

                        // Handle List<String> case (e.g., for JSON arrays stored as String in the database)
                        else if (field.getType().equals(List.class) && value instanceof String) {
                            // Example: split a CSV string into a List<String>
                            value = Arrays.asList(((String) value).split(","));
                        }

                        // Handle other types if needed, like int[] or List<Integer>
                        else if (field.getType().equals(int[].class) && value instanceof String) {
                            value = Arrays.stream(((String) value).split(","))
                                    .mapToInt(Integer::parseInt)
                                    .toArray();
                        }

                        // Handle more field types as necessary (e.g., Date, LocalDateTime, etc.)
                    }

                    // Set the field value only if it's not null
                    if (value != null) {
                        field.setAccessible(true); // Make private fields accessible
                        field.set(instance, value); // Set the value to the field
                    }
                }
            }

            return instance; // Return the populated entity instance
        } catch (Exception e) {
            throw new RuntimeException("Error mapping row to entity for class: " + clazz.getName(), e);
        }
    }

    public static <T, S> T mapFields(S source, Class<T> targetClass) {
        try {
            // 创建目标对象
            T target = targetClass.getDeclaredConstructor().newInstance();

            // 获取源对象和目标对象的字段
            Field[] sourceFields = source.getClass().getDeclaredFields();
            Field[] targetFields = targetClass.getDeclaredFields();

            // 将目标字段存入 Map，便于快速查找
            Map<String, Field> targetFieldMap = new HashMap<>();
            for (Field targetField : targetFields) {
                targetFieldMap.put(targetField.getName(), targetField);
            }

            // 遍历源对象的字段，进行赋值
            for (Field sourceField : sourceFields) {
                Field targetField = targetFieldMap.get(sourceField.getName());

                if (targetField != null &&
                        !java.lang.reflect.Modifier.isStatic(targetField.getModifiers()) &&  // 忽略 static 字段
                        !java.lang.reflect.Modifier.isFinal(targetField.getModifiers()) &&  // 忽略 final 字段
                        targetField.getType().equals(sourceField.getType())) { // 类型相同才能赋值

                    sourceField.setAccessible(true);
                    targetField.setAccessible(true);

                    // 将源字段的值复制到目标字段
                    Object value = sourceField.get(source);
                    targetField.set(target, value);
                }
            }

            return target;
        } catch (Exception e) {
            throw new RuntimeException("Error mapping fields between objects", e);
        }
    }

}
