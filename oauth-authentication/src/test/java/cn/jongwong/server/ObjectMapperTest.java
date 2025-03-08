package cn.jongwong.server;

import cn.jongwong.server.util.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(classes = OauthAuthenticationApplication.class)
public class ObjectMapperTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testObjectMapperSerialization() throws Exception {
        // 创建一个 ResponseResult 对象
        Response result = new Response(200, "OK", "Some data");

        // 将对象转换为 JSON 字符串
        String jsonResponse = objectMapper.writeValueAsString(result);
        // 期望的 JSON 字符串
        String expectedJson = "{\n" +
                "  \"code\" : 200,\n" +
                "  \"message\" : \"OK\",\n" +
                "  \"data\" : \"Some data\"\n" +
                "}";

        // 验证转换是否正确
        assertEquals(expectedJson, jsonResponse);
    }

    @Test
    public void testLocalDateTimeSerialization() throws Exception {
        // 使用给定的时间戳 1738857599999（代表 2025年2月7日 23:59:59.999 UTC）
        long timestamp = 1738857599999L;

        // 将时间戳转换为 LocalDateTime
        LocalDateTime expectedDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneOffset.UTC);

        // 创建 DateRO 对象并设置时间
        DateRO dateRO = new DateRO();
        dateRO.setTime(expectedDateTime);

        // 将对象转换为 JSON 字符串
        String jsonResponse = objectMapper.writeValueAsString(dateRO);

        // 构造期望的 JSON 字符串，时间戳应该是 1738857599999
        String expectedJson = "{\n" +
                "  \"time\" : " + timestamp + "\n" +
                "}";

        // 格式化比较时去除换行和空格
        assertEquals(normalizeJson(expectedJson), normalizeJson(jsonResponse));

        // 反序列化回 DateRO 对象
        DateRO deserializedDateRO = objectMapper.readValue(jsonResponse, DateRO.class);

        // 验证反序列化后的 LocalDateTime 是否匹配原始值
        assertEquals(expectedDateTime, deserializedDateRO.getTime());
    }

    // 内部类 DateRO
    public static class DateRO {
        private LocalDateTime time;

        public LocalDateTime getTime() {
            return time;
        }

        public void setTime(LocalDateTime time) {
            this.time = time;
        }
    }

    // 帮助方法，用于去除换行符和多余空格
    private String normalizeJson(String json) {
        // 去除换行符、回车符、制表符、空格
        return json.replaceAll("\\s+", "");
    }




}
