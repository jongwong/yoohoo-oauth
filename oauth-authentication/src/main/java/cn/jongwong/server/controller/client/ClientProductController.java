package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.enums.product.ProductListedStatus;
import cn.jongwong.server.enums.product.PurchaseGroupStatus;
import cn.jongwong.server.service.product.PurchaseGroupProductService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/client/group")
public class ClientProductController {


    @Autowired
    private PurchaseGroupProductService purchaseGroupProductService;

    @GetMapping("/product")
    public Mono<PageResponse<ClientPurchaseGroupProductVO>> queryProduct(
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String distributionPointId,
            @RequestParam(required = false) Integer[] groupStatus,
            @RequestParam(required = false) LocalDateTime timeDeliveryStart,
            @RequestParam(required = false) LocalDateTime timeDeliveryEnd,
            @RequestParam(required = false) LocalDateTime timeGroupStart,
            @RequestParam(required = false) LocalDateTime timeGroupEnd,
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
        return purchaseGroupProductService.searchWithImage(productName, categoryId, distributionPointId, finalStatus, ProductListedStatus.LISTED.getCode(), GlobalEnableTypeEnum.ENABLE.getValue(),
                        timeDeliveryStart, timeDeliveryEnd, timeGroupStart, timeGroupEnd, page, size)
                .map(PageResponse::success);

    }

    @GetMapping("/product/{id}")
    public Mono<Response<ClientPurchaseGroupProductVO>> findOneById(@PathVariable String id) { // 每页大小


        return purchaseGroupProductService.fineOneWithImage(id)
                .map(Response::ok);

    }


}
