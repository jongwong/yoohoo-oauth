package cn.jongwong.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class StaticResourceConfig implements WebFluxConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将请求路径 /static/** 映射到 classpath:/views/static/
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
