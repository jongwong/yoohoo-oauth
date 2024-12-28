package cn.jongwong.server.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Configuration
@ConditionalOnClass(Jackson2ObjectMapperBuilder.class)
public class JacksonObjectMapperConfiguration {

    private static final DateTimeFormatter ISO_INSTANT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneOffset.UTC);

    public static ObjectMapper configureObjectMapper(ObjectMapper objectMapper) {
        // 设置命名策略为 snake_case
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // 启用字符串化日期时间反序列化
        objectMapper.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);

        // 注册支持 Java 8 日期时间 API 的模块
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 忽略未知字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);


        // 设置 LocalDateTime 和 Instant 的序列化为时间戳（毫秒级）
        javaTimeModule.addSerializer(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
            @Override
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException, IOException {
                gen.writeNumber(value.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()); // 转为毫秒级时间戳
            }
        });

        javaTimeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                long timestamp = p.getLongValue();
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);
            }
        });

        // 启用时间序列化为时间戳
        objectMapper.registerModule(javaTimeModule);


        SimpleModule module = new SimpleModule();

        // 显式注册 UUID 的序列化和反序列化处理器
        module.addSerializer(UUID.class, new com.fasterxml.jackson.databind.ser.std.UUIDSerializer());
        module.addDeserializer(UUID.class, new com.fasterxml.jackson.databind.deser.std.UUIDDeserializer());

        // 注册枚举的自定义序列化器
        module.addSerializer(Enum.class, new JsonSerializer<Enum>() {
            @Override
            public void serialize(Enum value, JsonGenerator gen, com.fasterxml.jackson.databind.SerializerProvider serializers) throws IOException {
                if (value != null) {
                    gen.writeNumber(value.ordinal());  // 写入枚举的 ordianl 值作为 int 存储
                }
            }
        });

        // 注册枚举的自定义反序列化器
        module.addDeserializer(Enum.class, new JsonDeserializer<Enum>() {
            @Override
            public Enum deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                int value = p.getIntValue();
                Class<?> enumClass = p.getCurrentValue().getClass();

                // 返回对应枚举值，通过枚举的 ordinal 值进行反序列化
                Object[] enumConstants = enumClass.getEnumConstants();
                return (Enum) enumConstants[value];
            }
        });

        objectMapper.registerModule(module);

        return objectMapper;
    }

    @Bean("json")
    public ObjectMapper jsonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = new ObjectMapper();
        return configureObjectMapper(objectMapper);
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper defaultObjectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        return configureObjectMapper(objectMapper);
    }
}
