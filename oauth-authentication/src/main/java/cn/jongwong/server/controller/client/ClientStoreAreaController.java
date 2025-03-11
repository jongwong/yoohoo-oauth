package cn.jongwong.server.controller.client;

import cn.jongwong.server.dto.product.CommonBatchDTO;
import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.service.DistributionPointService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/client/store/area")
public class ClientStoreAreaController {

    @Autowired
    private DistributionPointService distributionPointService;


    // 获取最近的配送点
    @GetMapping("/distance")
    public Mono<PageResponse<DistributionPointVO>> queryLocation(@RequestParam(required = false) String name,
                                                                 @RequestParam(required = false) Integer enable,
                                                                 @RequestParam(required = true) BigDecimal latitude,
                                                                 @RequestParam(required = true) BigDecimal longitude,
                                                                 @RequestParam(required = true) int page,
                                                                 @RequestParam(required = true) int size) {
        // String 转成 浮点数
        return PageResponse.reactivePageSuccess(distributionPointService.searchSortByLocation(name, enable, latitude, longitude, page, size));
    }


    // 获取最近的配送点
    @GetMapping("/{id}")
    public Mono<Response<DistributionPointVO>> queryLocation(@PathVariable String id) {
        // String 转成 浮点数
        return distributionPointService.findById(id).map(Response::ok);
    }

    @PostMapping("/distribution-point/batch")
    public Mono<Response<List<DistributionPointVO>>> getProductListByIds(@RequestBody CommonBatchDTO data) {
        return distributionPointService.findByIds(data.getIds()).collectList().map(Response::success);
    }

    // 获取所有配送点
    @GetMapping("/distribution-point")
    public Mono<PageResponse<DistributionPointVO>> search(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Integer enable,
                                                          @RequestParam(required = true) Integer page,
                                                          @RequestParam(required = true) Integer size) {
        return PageResponse.reactivePageSuccess(distributionPointService.search(name, enable, page, size));
    }



}
