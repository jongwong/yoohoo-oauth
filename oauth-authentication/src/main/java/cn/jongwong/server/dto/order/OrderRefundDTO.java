package cn.jongwong.server.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRefundDTO {


    @Schema(description = "openId")
    private String openId;
    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "退款原因")
    private String reason;

}
