package cn.jongwong.server.controller.client;

import cn.jongwong.server.service.DistributionPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client/product")
public class ClientProductController {

    @Autowired
    private DistributionPointService distributionPointService;


//    // 获取最近的配送点
//    @GetMapping("/group/product")
//    public Mono<PageResponse<ProductVO>> queryLocation(@RequestParam(required = false) String name,
//                                                       @RequestParam(required = true) BigDecimal latitude,
//                                                       @RequestParam(required = true) int page,
//                                                       @RequestParam(required = true) int size) {
//        // String 转成 浮点数
//        return PageResponse.reactivePageSuccess(distributionPointService.searchSortByLocation(name, latitude, longitude, page, size));
//    }


}
