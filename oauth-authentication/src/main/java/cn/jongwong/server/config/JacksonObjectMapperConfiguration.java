package cn.jongwong.server.config;

import cn.jongwong.server.config.covert.FileDeserializer;
import cn.jongwong.server.config.covert.FileSerializer;
import cn.jongwong.server.dto.common.FileVO;
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
            public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeNumber(value.atZone(ZoneOffset.UTC).toInstant().toEpochMilli()); // 转为毫秒级时间戳
            }
        });

        javaTimeModule.addDeserializer(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
            @Override
            public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
                long timestamp = p.getLongValue();
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);  // 确保反序列化为 LocalDateTime
            }
        });


        // 启用时间序列化为时间戳
        objectMapper.registerModule(javaTimeModule);


        // 1️⃣ 创建 Jackson 模块
        SimpleModule customModule = new SimpleModule();
        // 2️⃣ 注册 FileVO 的序列化和反序列化器
        customModule.addSerializer(FileVO.class, new FileSerializer());
        customModule.addDeserializer(FileVO.class, new FileDeserializer());

        objectMapper.registerModule(customModule);

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


    // 字符串数组的自定义序列化器
    public static class StringArraySerializer extends JsonSerializer<String[]> {
        @Override
        public void serialize(String[] value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (value != null) {
                String joined = String.join(",", value);  // 使用逗号连接字符串数组
                gen.writeString(joined);  // 写入 JSON 字符串
            }
        }
    }

    // 字符串数组的自定义反序列化器
    public static class StringArrayDeserializer extends JsonDeserializer<String[]> {
        @Override
        public String[] deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getText();
            if (value != null && !value.isEmpty()) {
                return value.split(",");  // 根据逗号拆分字符串为数组
            }
            return new String[0];  // 空数组
        }
    }
}
