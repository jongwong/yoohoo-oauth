package cn.jongwong.server.enums;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "商品状态", type = "string")
public enum GlobalEnableTypeEnum {

    @Schema(description = "禁用")
    DISABLE(0),

    @Schema(description = "启用")
    ENABLE(1);

    private final int value;
}
