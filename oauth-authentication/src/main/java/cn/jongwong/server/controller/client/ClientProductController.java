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

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/client/group")
public class ClientProductController {


    @Autowired
    private PurchaseGroupProductService purchaseGroupProductService;

    @GetMapping("/product")
    public Mono<PageResponse<ClientPurchaseGroupProductVO>> queryLocation(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) Integer[] groupStatus,
            @RequestParam(required = false) String deliveryStartTime,
            @RequestParam(required = false) String deliveryEndTime,
            @RequestParam int page, // 当前页
            @RequestParam int size) { // 每页大小

        // 默认状态数组
        Integer[] defaultStatus = {
                PurchaseGroupStatus.SUCCESS.getCode(),
                PurchaseGroupStatus.IN_PROGRESS.getCode(),
                PurchaseGroupStatus.WAITING.getCode(),
        };

        // 如果传入的 groupStatus 不为空，则取交集；否则直接使用默认状态
        List<Integer> statusIntersection = groupStatus == null
                ? Arrays.asList(defaultStatus) // 使用默认状态
                : Arrays.stream(defaultStatus)
                .filter(Arrays.asList(groupStatus)::contains) // 求交集
                .toList();

        // 将交集转为数组
        Integer[] finalStatus = statusIntersection.toArray(new Integer[0]);
        return purchaseGroupProductService.search(productName, categoryId, finalStatus, ProductListedStatus.LISTED.getCode(), GlobalEnableTypeEnum.ENABLE.getValue(),
                        deliveryStartTime, deliveryEndTime, page, size)
                .map(PageResponse::success);

    }


}
