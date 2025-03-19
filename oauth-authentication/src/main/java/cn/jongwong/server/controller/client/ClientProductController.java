package cn.jongwong.server.controller.client;

import cn.jongwong.server.dto.product.CommonBatchDTO;
import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.entity.ProductSkuVO;
import cn.jongwong.server.entity.ProductVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.enums.product.ProductListedStatus;
import cn.jongwong.server.enums.product.ProductStatus;
import cn.jongwong.server.enums.product.PurchaseGroupStatus;
import cn.jongwong.server.service.GroupAdminService;
import cn.jongwong.server.service.ProductService;
import cn.jongwong.server.service.ProductSkuService;
import cn.jongwong.server.service.PurchaseGroupService;
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
@RequestMapping()
public class ClientProductController {


    @Autowired
    private PurchaseGroupProductService purchaseGroupProductService;

    @Autowired
    private PurchaseGroupService purchaseGroupService;


    @Autowired
    private ProductService productService;
    ;

    @Autowired
    private ProductSkuService productSkuService;
    @Autowired
    private GroupAdminService groupAdminService;

    @GetMapping("/client/group/product")
    public Mono<PageResponse<ClientPurchaseGroupProductVO>> queryGroupProduct(
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
        return purchaseGroupProductService.search(productName, categoryId, distributionPointId, finalStatus, ProductListedStatus.LISTED.getCode(), GlobalEnableTypeEnum.ENABLE.getValue(),
                        timeDeliveryStart, timeDeliveryEnd, timeGroupStart, timeGroupEnd, page, size)
                .map(PageResponse::success);

    }

    @GetMapping("/client/group/product/{id}")
    public Mono<Response<ClientPurchaseGroupProductVO>> findOneById(@PathVariable String id) { // 每页大小


        return purchaseGroupProductService.fineOneWithImage(id)
                .map(Response::ok);

    }

    @GetMapping("/client/group/product/by-group-product/{groupId}/{productId}")
    public Mono<Response<ClientPurchaseGroupProductVO>> findOneById(@PathVariable String groupId, @PathVariable String productId) { // 每页大小


        return purchaseGroupProductService.fineOneBypProductGroupId(groupId, productId)
                .map(Response::ok);

    }

    @GetMapping("/client/product")
    public Mono<PageResponse<ProductVO>> searchProduct(@RequestParam(required = false) String name, @RequestParam(required = true) int page, @RequestParam(required = true) int size) { // 每页大小


        String status = ProductStatus.LISTED.getCode() + "";

        return productService.search(name, status, page, size);
    }


    @PostMapping("/client/product/batch")
    public Mono<Response<List<ProductVO>>> getProductListByIds(@RequestBody CommonBatchDTO data) {
        return productService.findByIds(data.getIds()).collectList().map(Response::success);
    }

    @GetMapping("/client/product/sku/{id}")
    public Mono<Response<List<ProductSkuVO>>> findAllSkuBProductId(@PathVariable String id) { // 每页大小


        return productSkuService.findAllByProductId(id).collectList().map(Response::ok);
    }

    @GetMapping("/client/product/sku/by_group/{id}/{groupId}")
    public Mono<Response<List<ProductSkuVO>>> findAllGroupSkuBProductId(@PathVariable String groupId, @PathVariable String id) { // 每页大小


        return purchaseGroupService.findById(groupId).flatMap(
                group -> {

                    var productList = group.getProducts();

                    return productSkuService.findAllByProductId(id).collectList().map(
                            list -> {
                                list.forEach(
                                        sku -> {
                                            var find = productList.stream().filter(
                                                    product -> product.getProductId().equals(sku.getProductId())
                                            ).findFirst();
                                            if (find.isPresent()) {
                                                var findSku = find.get();

                                                var newPrice = sku.getPrice() - findSku.getAmountOffset();
                                                sku.setPrice(newPrice);

                                            }

                                        }
                                );
                                return list;
                            }
                    );
                }
        ).map(Response::ok);
    }


    @GetMapping("/client/product/{id}")
    public Mono<Response<ProductVO>> findByProductId(@PathVariable String id) { // 每页大小


        return productService.findById(id).map(Response::ok);
    }




}
