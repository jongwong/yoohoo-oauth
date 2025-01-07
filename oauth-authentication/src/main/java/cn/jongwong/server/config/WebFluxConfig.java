package cn.jongwong.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class WebFluxConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/");
    }


    @Bean
    public R2dbcCustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new EnumToIntConverter());
        converters.add(new IntToEnumConverter());
        return new R2dbcCustomConversions(R2dbcCustomConversions.StoreConversions.NONE, converters);
    }

    @WritingConverter
    public static class EnumToIntConverter implements Converter<Enum, Integer> {
        @Override
        public Integer convert(Enum status) {
            return status.ordinal();
        }
    }

    @ReadingConverter
    public static class IntToEnumConverter implements Converter<Byte, Enum> {

        @Override
        public Enum convert(Byte source) {
            // 获取目标的枚举类型
            // 这里我们需要通过上下文来获取目标的枚举类型，可以使用反射或传递 enumType
            throw new UnsupportedOperationException("Enum conversion not implemented for this type");
        }
    }


    @Bean
    public WebClient webClient() {
        return WebClient.create();  // Creates a simple WebClient instance
    }

    @Bean
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }

}
