package webstack.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色访问控制注解。
 * <p>
 * 可标注在 Controller 类或方法上（方法级优先于类级）。
 * value 为空表示"任意已登录用户"均可访问；否则仅当用户角色命中其中之一时放行。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    String[] value() default {};
}
