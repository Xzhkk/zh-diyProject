package com.xzh.aspect;

import cn.hutool.json.JSONUtil;
import com.xzh.bean.CmServiceLog;
import com.xzh.config.ServiceLogTableNameContextHolder;
import com.xzh.log.ServiceLogWriter;
import com.xzh.service.CmServiceLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Aspect
@Component
public class ServiceLogAspect {
    @Autowired
    private ServiceLogWriter logWriter;
    @Autowired private CmServiceLogService logService;

    // 拦截所有 RestController 的方法
    @Around("within(@org.springframework.web.bind.annotation.RestController *) && execution(* com.xzh..controller..*.*(..))")
    public Object recordLog(ProceedingJoinPoint joinPoint) throws Throwable {
        Object res = joinPoint.proceed(); // 先执行业务
        try {
            // 1. 获取月份并检查/创建表
            String tableName = "cm_service_log_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMM"));
            logWriter.ensureTableExists(tableName);

            // 2. 构造日志对象
            CmServiceLog log = new CmServiceLog();
            ServletRequestAttributes attr = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            log.setUrl(attr.getRequest().getRequestURI());
            log.setResult(JSONUtil.toJsonStr(res));
            log.setCreateTime(LocalDateTime.now());

            // 3. 设置动态表名并保存至 zh-log 库
            ServiceLogTableNameContextHolder.setTableName(tableName);
            logService.save(log);
        } catch (Exception e) {
            e.printStackTrace(); // 日志记录失败不应阻断主业务流
        } finally {
            ServiceLogTableNameContextHolder.clear();
        }
        return res;
    }
}
