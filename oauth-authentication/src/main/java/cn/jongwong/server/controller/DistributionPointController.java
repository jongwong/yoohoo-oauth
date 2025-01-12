package cn.jongwong.server.controller;

import cn.jongwong.server.dto.product.CommonBatchDTO;
import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.service.DistributionPointService;
import cn.jongwong.server.util.response.PageResponse;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/admin/distribution-point")
public class DistributionPointController {

    @Autowired
    private DistributionPointService distributionPointService;

    // 根据ID查找配送点
    @GetMapping("/{id}")
    public Mono<Response<DistributionPointVO>> findById(@PathVariable String id) {
        return distributionPointService.findById(id)
                .map(Response::success)
                .defaultIfEmpty(Response.notFound());
    }

    // 获取所有配送点
    @GetMapping
    public Mono<PageResponse<DistributionPointVO>> search(@RequestParam(required = false) String name,
                                                          @RequestParam(required = false) Integer enable,
                                                          @RequestParam(required = true) Integer page,
                                                          @RequestParam(required = true) Integer size) {
        return PageResponse.reactivePageSuccess(distributionPointService.search(name, enable, page, size));
    }

    // 创建配送点
    @PostMapping
    public Mono<Response<DistributionPointVO>> create(@RequestBody DistributionPointVO distributionPointVO) {
        return distributionPointService.create(distributionPointVO)
                .map(Response::success);
    }

    // 更新配送点
    @PutMapping("/{id}")
    public Mono<Response<DistributionPointVO>> update(@PathVariable String id, @RequestBody DistributionPointVO distributionPointVO) {
        distributionPointVO.setId(id);
        return distributionPointService.update(distributionPointVO)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    // 更新配送点
    @PutMapping("/{id}/enable")
    public Mono<Response<DistributionPointVO>> enable(@PathVariable String id) {
        return distributionPointService.enable(id)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }

    // 更新配送点
    @PutMapping("/{id}/disable")
    public Mono<Response<DistributionPointVO>> disable(@PathVariable String id) {
        return distributionPointService.disable(id)
                .map(Response::ok)
                .defaultIfEmpty(Response.notFound());
    }


    // 删除配送点
    @DeleteMapping("/{id}")
    // 控制器层
    public Mono<Response<Void>> delete(@PathVariable String id) {
        return distributionPointService.delete(id)
                .then(Mono.just(Response.success()));  // 删除成功后返回一个空的响应
    }


    @PostMapping("/batch")
    public Mono<Response<List<DistributionPointVO>>> getProductListByIds(@RequestBody CommonBatchDTO data) {
        return distributionPointService.findByIds(data.getIds()).collectList().map(Response::success);
    }

}
