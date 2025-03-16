package cn.jongwong.server.config.covert;

import cn.jongwong.server.dto.common.FileVO;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.stereotype.Component;

@WritingConverter
@Component
public class FileVOToStringConverter implements Converter<FileVO, String> {
    @Override
    public String convert(FileVO source) {
        return serializeToDataBaseString(source);
    }

    private String serializeToDataBaseString(FileVO fileVO) {
        if (fileVO == null || fileVO.getUrl() == null) {
            return null;
        }

        String extractedName = extractFileName(fileVO.getUrl());
        if (fileVO.getName() == null || fileVO.getName().equals(extractedName)) {
            return fileVO.getUrl();
        } else {
            return fileVO.getName() + "," + fileVO.getUrl();
        }
    }

    private static String extractFileName(String url) {
        return (url == null || url.isEmpty()) ? null : url.substring(url.lastIndexOf('/') + 1);
    }
}