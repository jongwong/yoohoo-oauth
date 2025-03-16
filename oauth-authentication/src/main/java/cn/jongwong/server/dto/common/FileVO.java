package cn.jongwong.server.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FileVO {


    @Schema(description = "图片名称")
    private String name;

    @Schema(description = "图片访问的URL地址")
    private String url;


}
