package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.service.DistributionPointService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

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



}
