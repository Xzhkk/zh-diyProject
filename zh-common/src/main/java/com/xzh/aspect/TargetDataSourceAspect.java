package com.xzh.aspect;

import com.xzh.annotation.TargetDataSource;
import com.xzh.config.DynamicDataSourceContextHolder;
import com.xzh.constants.DataSourceConstants;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(-1) // 确保在事务切面之前执行
public class TargetDataSourceAspect {
    @Around("@within(ds) || @annotation(ds)")
    public Object switchDataSource(ProceedingJoinPoint joinPoint, TargetDataSource ds) throws Throwable {
        String key = (ds != null) ? ds.value() : DataSourceConstants.MASTER;
        DynamicDataSourceContextHolder.push(key); // 存入上下文
        try {
            return joinPoint.proceed();
        } finally {
            DynamicDataSourceContextHolder.poll(); // 清理上下文
        }
    }
}
