package cn.jongwong.server.common;

import cn.jongwong.server.config.security.jwt.JwtCodeAuthenticationToken;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

@Aspect
@Component
public class AutoUpdatedAspect {


    @Before("execution(* cn.jongwong.server..*(.., @cn.jongwong.server.common.AutoUpdatedField (*)))")
    public void setAutoCreatedFields(JoinPoint joinPoint) {


        // 获取目标方法的所有参数
        Object[] args = joinPoint.getArgs();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // 如果认证信息是 JwtCodeAuthenticationToken 类型，获取用户信息
        if (authentication instanceof JwtCodeAuthenticationToken) {
            var user = ((JwtCodeAuthenticationToken) authentication).getCurrentUser();


            // 遍历方法参数
            for (Object data : args) {
                if (data != null) {
                    // 遍历类中的字段
                    Field[] fields = data.getClass().getDeclaredFields();
                    // 遍历方法来寻找带有 @AutoCreatedField 注解的字段

                    try {

                        // 填充对应的字段
                        for (Field field : fields) {
                            field.setAccessible(true);


                            if ("updatedAt".equals(field.getName())) {
                                field.set(data, LocalDateTime.now());  // 填充时间戳
                            }
                            if ("updatedBy".equals(field.getName())) {
                                field.set(data, user.getId());  // 填充用户名
                            }
                            if ("updatedByName".equals(field.getName())) {
                                field.set(data, user.getName());  // 填充用户名
                            }
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
