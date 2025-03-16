package cn.jongwong.server.config.covert;

import cn.jongwong.server.dto.common.FileVO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.stereotype.Component;

@Component
@ReadingConverter
public class StringToFileVOConverter implements Converter<String, FileVO> {
    @Override
    public FileVO convert(String source) {
        return deserializeFromString(source);
    }

    // ⬇️ 直接调用 `FileSerializer` 的方法，避免重复代码
    private static FileVO deserializeFromString(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }

        String[] parts = value.split(",", 2);
        if (parts.length == 2) {
            return new FileVO(parts[0], parts[1]);
        } else {
            return new FileVO(FileSerializer.extractFileName(value), value);
        }
    }
}
