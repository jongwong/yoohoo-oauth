package cn.jongwong.server.service;

import cn.jongwong.server.entity.ProductSkuVO;
import cn.jongwong.server.repository.ProductSkuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class ProductSkuService {

    @Autowired
    private ProductSkuRepository productSkuRepository;

    public Flux<ProductSkuVO> findAllByProductId(String productId) {
        return productSkuRepository.findAllByProductId(productId);
    }
}
