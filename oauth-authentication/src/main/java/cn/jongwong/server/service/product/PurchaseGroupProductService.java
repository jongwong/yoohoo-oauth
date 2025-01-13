package cn.jongwong.server.service.product;

import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.repository.ClientPurchaseGroupProductRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class PurchaseGroupProductService {


    @Autowired
    private ClientPurchaseGroupProductRepository clientPurchaseGroupProductRepository;

    public Mono<Page<ClientPurchaseGroupProductVO>> query(

            String productName, String categoryId, Integer groupStatus, Integer listedStatus, Integer enable,
            String deliveryStartTime, String deliveryEndTime,
            int page, // 当前页
            int size) { // 每页大小
        // 计算偏移量
        int offset = (page - 1) * size;

        Flux<ClientPurchaseGroupProductVO> dataFlux = clientPurchaseGroupProductRepository.findByDynamicConditions(
                productName, categoryId, groupStatus, listedStatus, enable,
                deliveryStartTime, deliveryEndTime, size, offset);

        // 获取总数
        Mono<Long> countMono = clientPurchaseGroupProductRepository.countByDynamicConditions(
                productName, categoryId, groupStatus, listedStatus, enable,
                deliveryStartTime, deliveryEndTime);

        // Collect Flux into a List and zip with the count
        return dataFlux.collectList() // Collect Flux into a List
                .zipWith(countMono)  // Combine the list with the count
                .map(tuple -> new Page<>(tuple.getT1(), tuple.getT2(), page, size));
    }
}
