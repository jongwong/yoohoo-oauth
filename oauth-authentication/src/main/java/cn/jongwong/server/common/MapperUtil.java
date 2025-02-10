package cn.jongwong.server.common;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MapperUtil {

    // 用于缓存字段的列名，以提高性能
    private static final Map<String, String> columnNameCache = new ConcurrentHashMap<>();

  
    // Convert Java field name to database column name (snake_case)
    private static String convertToDatabaseColumnName(String fieldName) {
        StringBuilder columnName = new StringBuilder();
        for (char c : fieldName.toCharArray()) {
            if (Character.isUpperCase(c)) {
                columnName.append("_").append(Character.toLowerCase(c));
            } else {
                columnName.append(c);
            }
        }
        return columnName.toString();
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

    public static <T> void merge(T target, T copySource) {
        BeanUtils.copyProperties(copySource, target, getNullPropertyNames(copySource));
    }

    // 获取 source 中为 null 的属性名称
    private static String[] getNullPropertyNames(Object source) {
        final BeanWrapper src = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();
        return Arrays.stream(pds)
                .filter(pd -> src.getPropertyValue(pd.getName()) == null)
                .map(pd -> pd.getName())
                .toArray(String[]::new);
    }

}
