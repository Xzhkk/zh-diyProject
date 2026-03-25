package com.xzh.aspect;

import com.xzh.annotation.TargetDataSource;
import com.xzh.datasource.DynamicDataSourceContextHolder;
import com.xzh.constants.DataSourceConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;

import java.lang.reflect.Method;

@Aspect
@Component
@Order(-1) // 确保在事务切面之前执行
public class TargetDataSourceAspect {
    @Around("@within(com.xzh.annotation.TargetDataSource) || @annotation(com.xzh.annotation.TargetDataSource)")
    public Object switchDataSource(ProceedingJoinPoint joinPoint) throws Throwable {
        TargetDataSource targetDataSource = getAnnotation(joinPoint);
        String key = (targetDataSource != null) ? targetDataSource.value() : DataSourceConstants.MASTER;
        DynamicDataSourceContextHolder.push(key); // 存入上下文
        try {
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll(); // 清理上下文
        }
    }

    private TargetDataSource getAnnotation(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> targetClass = joinPoint.getTarget().getClass();
        // 先找目标类实际方法上的注解，兼容接口方法和父类方法
        TargetDataSource ds = AnnotationUtils.findAnnotation(targetClass, TargetDataSource.class);
        return ds;
    }
}
