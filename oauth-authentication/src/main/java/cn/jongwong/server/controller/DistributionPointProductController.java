package cn.jongwong.server.controller;

import cn.jongwong.server.entity.DistributionPointProductVO;
import cn.jongwong.server.service.DistributionPointProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/distribution-point-products")
public class DistributionPointProductController {

    @Autowired
    private DistributionPointProductService distributionPointProductService;

    // 根据ID查找配送点商品
    @GetMapping("/{id}")
    public Mono<ResponseEntity<DistributionPointProductVO>> findById(@PathVariable String id) {
        return distributionPointProductService.findById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 创建配送点商品
    @PostMapping
    public Mono<ResponseEntity<DistributionPointProductVO>> create(@RequestBody DistributionPointProductVO distributionPointProductVO) {
        return distributionPointProductService.create(distributionPointProductVO)
                .map(saved -> ResponseEntity.status(201).body(saved));
    }

    // 更新配送点商品
    @PutMapping("/{id}")
    public Mono<ResponseEntity<DistributionPointProductVO>> update(@PathVariable String id, @RequestBody DistributionPointProductVO distributionPointProductVO) {
        distributionPointProductVO.setId(id);
        return distributionPointProductService.update(distributionPointProductVO)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    // 删除配送点商品
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Object>> delete(@PathVariable String id) {
        return distributionPointProductService.delete(id)
                .map(v -> ResponseEntity.noContent().build())
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
