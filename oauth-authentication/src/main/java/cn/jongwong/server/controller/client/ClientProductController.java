package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.enums.product.ProductListedStatus;
import cn.jongwong.server.enums.product.PurchaseGroupStatus;
import cn.jongwong.server.service.product.PurchaseGroupProductService;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/client/group")
public class ClientProductController {


    @Autowired
    private PurchaseGroupProductService purchaseGroupProductService;

    @GetMapping("/product")
    public Mono<PageResponse<ClientPurchaseGroupProductVO>> queryLocation(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String deliveryStartTime,
            @RequestParam(required = false) String deliveryEndTime,
            @RequestParam int page, // 当前页
            @RequestParam int size) { // 每页大小


        return purchaseGroupProductService.query(productName, categoryId, PurchaseGroupStatus.SUCCESS.getCode(), ProductListedStatus.LISTED.getCode(), GlobalEnableTypeEnum.ENABLE.getValue(),
                        deliveryStartTime, deliveryEndTime, page, size)
                .map(PageResponse::success);

    }


}
