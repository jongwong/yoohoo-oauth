package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.service.DistributionPointService;
import cn.jongwong.server.util.response.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
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
                                                                 @RequestParam(required = true) BigDecimal latitude,
                                                                 @RequestParam(required = true) BigDecimal longitude,
                                                                 @RequestParam(required = true) int page,
                                                                 @RequestParam(required = true) int size) {
        // String 转成 浮点数
        return PageResponse.reactivePageSuccess(distributionPointService.searchSortByLocation(name, latitude, longitude, page, size));
    }


}
