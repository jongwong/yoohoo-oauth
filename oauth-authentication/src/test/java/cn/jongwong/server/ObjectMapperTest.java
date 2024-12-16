package cn.jongwong.server.config;

import cn.jongwong.server.OauthAuthenticationApplication;
import cn.jongwong.server.util.response.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
}
