package cn.jongwong.server.config.covert;

import cn.jongwong.server.dto.common.FileVO;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class FileSerializer extends JsonSerializer<FileVO> {
    @Override
    public void serialize(FileVO fileVO, JsonGenerator gen, SerializerProvider serializers) throws IOException {

        if (fileVO == null) {
            gen.writeNull();
            return;
        }

        // 写入 JSON 对象
        gen.writeStartObject();
        if (fileVO.getName() != null) {
            gen.writeStringField("name", fileVO.getName());
        }
        gen.writeStringField("url", fileVO.getUrl());
        gen.writeEndObject();
    }


    public static String extractFileName(String url) {
        return (url == null || url.isEmpty()) ? null : url.substring(url.lastIndexOf('/') + 1);
    }
}
