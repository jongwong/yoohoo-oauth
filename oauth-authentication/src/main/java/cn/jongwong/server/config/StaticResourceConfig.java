package cn.jongwong.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;

@Configuration
public class StaticResourceConfig implements WebFluxConfigurer {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 将请求路径 /static/** 映射到 classpath:/static/ 目录下
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // 如果你使用 Springdoc OpenAPI，需要确保 Swagger UI 资源能够正确加载
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");

        // 也可以将其他静态资源（如 public、views 等）映射到对应路径
        registry.addResourceHandler("/public/**")
                .addResourceLocations("classpath:/public/");
    }
}
