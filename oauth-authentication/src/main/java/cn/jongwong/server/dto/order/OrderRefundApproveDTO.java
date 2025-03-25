package cn.jongwong.server.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OrderRefundApproveDTO {


    @Schema(description = "订单ID")
    private String orderId;

    @Schema(description = "退款原因")
    private String reason;

}
