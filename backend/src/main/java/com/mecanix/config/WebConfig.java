package com.mecanix.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins(
                "http://localhost:5173","http://127.0.0.1:5173","http://localhost:5500","http://127.0.0.1:5500",
                "http://localhost:5501","http://127.0.0.1:5501"
            )
            .allowedMethods("GET","POST","PUT","DELETE","OPTIONS","PATCH")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SessionInterceptor())
            .addPathPatterns("/api/**")
            .excludePathPatterns("/api/auth/login","/api/auth/logout");
    }
}

