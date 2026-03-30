package com.xzh.config;

import com.xzh.filter.UserLoginFilter;
import com.xzh.log.TraceIdInterceptor;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcRegistrations;
import org.springframework.cglib.transform.impl.AddInitTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonWebConfig implements WebMvcConfigurer {

    @Bean
    public UserLoginFilter userLoginFilter() {
        return new UserLoginFilter();
    }

    @Bean
    public TraceIdInterceptor traceIdInterceptor() {
        return new TraceIdInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userLoginFilter())
                // 拦截所有业务路径
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/**"
                );
        registry.addInterceptor(traceIdInterceptor())
                .addPathPatterns("/**");
    }
}
