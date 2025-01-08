package cn.jongwong.server.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

// 用于标记需要自动填充的字段，应用于 service 层方法参数
@Target(ElementType.PARAMETER) // 只能作用于方法参数
@Retention(RetentionPolicy.RUNTIME) // 注解在运行时可用
public @interface AutoUpdatedField {
    // 默认支持字段类型：createdAt, createdBy, createdByName
}
