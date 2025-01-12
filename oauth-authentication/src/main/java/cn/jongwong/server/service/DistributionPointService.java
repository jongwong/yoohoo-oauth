package cn.jongwong.server.service;

import cn.jongwong.server.common.AutoCreatedField;
import cn.jongwong.server.common.AutoUpdatedField;
import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.DistributionPointVO;
import cn.jongwong.server.enums.GlobalEnableTypeEnum;
import cn.jongwong.server.repository.DistributionPointRepository;
import cn.jongwong.server.util.response.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DistributionPointService {

    @Autowired
    private DistributionPointRepository distributionPointRepository;


    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    // 删除配送点
    public Mono<Void> delete(String id) {
        return distributionPointRepository.deleteById(id);
    }

    // 根据ID查询配送点
    public Mono<DistributionPointVO> findById(String id) {
        return distributionPointRepository.findById(id);
    }



    // 更新配送点
    public Mono<DistributionPointVO> update(@AutoUpdatedField DistributionPointVO distributionPointVO) {
        return distributionPointRepository.save(distributionPointVO);
    }

    // 启用
    public Mono<DistributionPointVO> enable(String id) {
        return distributionPointRepository.findById(id)
                .flatMap(distributionPoint -> {
                    // 更新 enable 字段
                    distributionPoint.setEnable(GlobalEnableTypeEnum.ENABLE.getValue());
                    // 保存更新后的实体
                    return distributionPointRepository.save(distributionPoint);
                });
    }

    //关闭
    public Mono<DistributionPointVO> disable(String id) {
        return distributionPointRepository.findById(id)
                .flatMap(distributionPoint -> {
                    // 更新 enable 字段
                    distributionPoint.setEnable(GlobalEnableTypeEnum.DISABLE.getValue());
                    // 保存更新后的实体
                    return distributionPointRepository.save(distributionPoint);
                });
    }


    // 新增配送点
    public Mono<DistributionPointVO> create(@AutoCreatedField DistributionPointVO distributionPointVO) {
        distributionPointVO.setId(UUID.randomUUID().toString());
        return distributionPointRepository.insert(distributionPointVO);
    }


    public Mono<Page<DistributionPointVO>> search(String name, Integer enable, Integer page, Integer size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, DistributionPointVO.class)
                .addLikeCondition("name", name)
                .addEqualCondition("enable", enable)
                .paginate(page, size)
                .exec().map(pageData -> {
                    List<DistributionPointVO> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, DistributionPointVO.class))
                            .toList();

                    return new Page<>(
                            userResList,
                            pageData.getTotal(),
                            pageData.getPage(),
                            pageData.getSize()
                    );
                });


    }

    public Mono<Page<DistributionPointVO>> searchSortByLocation(String name, BigDecimal latitude, BigDecimal longitude, int page, int size) {

        // 使用 SQL 或者自定义的查询 DSL 来获取离输入点最近的记录
        return distributionPointRepository.findPageByDSL(page, size, qsl -> {
            qsl.like("name", name);
            qsl.sort("distance,asc");  // 假设你想按距离排序，可以通过计算距离来排序
            qsl.column("6371 * ACOS(COS(RADIANS(:latitude)) * COS(RADIANS(latitude)) * COS(RADIANS(longitude) - RADIANS(:longitude)) + SIN(RADIANS(:latitude)) * SIN(RADIANS(latitude))) AS distance")
                    .bind("latitude", latitude)
                    .bind("longitude", longitude);
            // 计算距离：Haversine公式
            return qsl;
        });

    }

    public Flux<DistributionPointVO> findByIds(List<String> ids) {
        if (ids == null) {
            return Flux.empty();
        }
        return distributionPointRepository.findAllByIdIn(ids);
    }
}
