package com.competition.backend.config;

import com.competition.backend.interceptor.JwtInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注释或删除以下代码，拦截器将不再生效
        // registry.addInterceptor(jwtInterceptor)
        //        .addPathPatterns("/api/**")
        //        .excludePathPatterns("/api/auth/**");
    }
/*    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor)
                .addPathPatterns("/**")  // 拦截所有请求
                .excludePathPatterns(    // 不拦截的请求
                        "/**",
                        "/auth/**",      // 登录相关
                        "/error",        // 错误页面
                        "/swagger-ui/**", // Swagger UI
                        "/v3/api-docs/**", // OpenAPI
                        "/announcements/**", // 公告相关
                        "/competitions/**",   // 竞赛相关
                        "/departments/**",
                        "/registration/**",
                        "/teams/**",
                        "/team/**",
                        "/registrations/**",
                        "/registrations/teacher/**",
                        "/registrations/teacher",
                        "/user/**"
                );
    }*/

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
 