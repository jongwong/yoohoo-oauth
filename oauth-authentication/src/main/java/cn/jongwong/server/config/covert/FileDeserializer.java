package cn.jongwong.server.config.covert;

import cn.jongwong.server.dto.common.FileVO;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileDeserializer extends JsonDeserializer<FileVO> {
    @Override
    public FileVO deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        // 如果是字符串（旧格式："name,url"）
        if (node.isTextual()) {
            String value = node.asText();
            return deserializeFromString(value);
        }

        // 如果是 JSON 对象（新格式：{ "name": "...", "url": "..." }）
        if (node.isObject()) {
            String name = node.has("name") ? node.get("name").asText() : null;
            String url = node.has("url") ? node.get("url").asText() : null;
            return new FileVO(name, url);
        }

        return null; // 遇到不支持的格式返回 null
    }

    public static FileVO deserializeFromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        String[] parts = value.split(",", 2);
        return parts.length == 2 ? new FileVO(parts[0], parts[1]) : new FileVO(null, parts[0]);
    }
}
