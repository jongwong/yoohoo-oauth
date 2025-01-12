package cn.jongwong.server.service.product;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.ProductCategoryVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.repository.ProductCategoryRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;
    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    public Mono<ProductCategoryVO> update(ProductCategoryVO productCategoryVO) {
        return productCategoryRepository.save(productCategoryVO);
    }

    public Mono<ProductCategoryVO> create(ProductCategoryVO productCategoryVO) {
        return productCategoryRepository.insert(productCategoryVO);
    }

    public Mono<ProductCategoryVO> findById(String id) {
        return productCategoryRepository.findById(id);
    }

    // 删除配送点
    public Mono<Void> deleteById(String id) {
        return productCategoryRepository.deleteById(id);
    }

    public Flux<ProductCategoryVO> findAll() {
        return productCategoryRepository.findAll();
    }

    // 启用
    public Mono<ProductCategoryVO> enable(String id) {
        return productCategoryRepository.findById(id)
                .flatMap(data -> {
                    // 更新 enable 字段
                    data.setEnable(GlobalEnableTypeEnum.ENABLE.getValue());
                    // 保存更新后的实体
                    return productCategoryRepository.save(data);
                });
    }

    //关闭
    public Mono<ProductCategoryVO> disable(String id) {
        return productCategoryRepository.findById(id)
                .flatMap(distributionPoint -> {
                    // 更新 enable 字段
                    distributionPoint.setEnable(GlobalEnableTypeEnum.DISABLE.getValue());
                    // 保存更新后的实体
                    return productCategoryRepository.save(distributionPoint);
                });
    }


    public Mono<Page<ProductCategoryVO>> search(String name, String code, Integer level, Integer page, Integer size) {

        return new QueryBuilder<>(r2dbcEntityTemplate, ProductCategoryVO.class)
                .addLikeCondition("name", name)
                .addEqualCondition("level", level)
                .addEqualCondition("code", code)
                .addSort("code,asc")
                .paginate(page, size)
                .exec().map(pageData -> {
                    List<ProductCategoryVO> userResList = pageData.getData().stream()
                            .map(e -> MapperUtil.mapFields(e, ProductCategoryVO.class))
                            .toList();

                    return new Page<>(
                            userResList,
                            pageData.getTotal(),
                            pageData.getPage(),
                            pageData.getSize()
                    );
                });


    }

    public Flux<ProductCategoryVO> findByIds(List<String> ids) {
        if (ids == null) {
            return Flux.empty();
        }
        return productCategoryRepository.findAllByIdIn(ids);
    }

}
