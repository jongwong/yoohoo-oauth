package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.CouponsVO;
import cn.jongwong.server.enums.coupons.CouponsStatus;
import cn.jongwong.server.repository.CouponsRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class CouponsService {


    @Autowired
    R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private CouponsRepository couponsRepository;
    @Autowired
    private UserService userService;

    // 根据ID查找优惠券
    public Mono<CouponsVO> findById(String id) {
        return couponsRepository.findById(id);
    }

    // 创建新的优惠券
    public Mono<CouponsVO> createCoupon(CouponsVO data) {


        return userService.getCurrentUserReactiveId()
                .map(userId -> data.toBuilder()
                        .createdAt(LocalDateTime.now())
                        .status(CouponsStatus.DRAFT.getCode())
                        .createdBy(userId)
                        .id(UUID.randomUUID().toString())
                        .build())
                .flatMap(couponsRepository::insert);
    }

    private CouponsVO mergeData(CouponsVO old, CouponsVO data) {

        old.setName(data.getName());
        old.setDiscountAmount(data.getDiscountAmount());
        old.setDiscountPercentage(data.getDiscountPercentage());
        old.setMinSpend(data.getMinSpend());
        old.setMaxDiscount(data.getMaxDiscount());
        old.setValidFrom(data.getValidFrom());
        old.setValidTo(data.getValidTo());
        old.setCreatedBy(data.getCreatedBy());
        old.setStatus(data.getStatus());
        old.setRejectionReason(data.getRejectionReason());
        return old;
    }

    // 更新优惠券
    public Mono<CouponsVO> update(String id, CouponsVO couponsVO) {
        return couponsRepository.findById(id)
                .map(existingCouponsVO -> mergeData(existingCouponsVO, couponsVO)) // 同步合并数据
                .flatMap(couponsRepository::save); // 异步保存
    }

    // 获取最大ID的优惠券
    public Mono<CouponsVO> getMaxIdCoupon() {
        return couponsRepository.findTopByOrderByIdDesc();
    }

    public Mono<Page<CouponsVO>> search(String name, String status, int page, int size) {


        return new QueryBuilder<>(r2dbcEntityTemplate, CouponsVO.class)
                .addLikeCondition("name", name)
                .addEqualCondition("status", status).paginate(page, size)
                .exec().map(pageData -> {
                    List<CouponsVO> userResList = pageData.getData().stream()
                            .map(user -> MapperUtil.mapFields(user, CouponsVO.class))
                            .toList();

                    return new Page<>(
                            userResList,
                            pageData.getTotal(),
                            pageData.getPage(),
                            pageData.getSize()
                    );
                });


    }


    /**
     * 提交优惠券审核
     *
     * @param id      优惠券ID
     * @return 更新后的优惠券信息
     */
    public Mono<CouponsVO> submit(String id, CouponsVO data) {
        return couponsRepository.findById(id)
                .map(old -> {
                    if (old.getStatus() == CouponsStatus.DRAFT.getCode() || old.getStatus() == CouponsStatus.REJECTED.getCode()) {
                        CouponsVO newData = mergeData(old, data);
                        newData.setStatus(CouponsStatus.REVIEWING.getCode()); // 设置状态为 "审核中"
                        newData.setUpdatedAt(LocalDateTime.now());
                        return newData; // 返回更新后的数据
                    } else {
                        throw new IllegalStateException("优惠券状态不允许提交审核");
                    }
                })
                .flatMap(couponsRepository::save) // 异步保存
                .switchIfEmpty(Mono.error(new IllegalArgumentException("优惠券未找到")));
    }

    /**
     * 审核拒绝操作，将优惠券状态设置为 "审核拒绝"。
     *
     * @param couponId        优惠券ID
     * @param rejectionReason 拒绝原因
     * @return 更新后的优惠券信息
     */
    public Mono<CouponsVO> reject(String couponId, String rejectionReason) {
        return couponsRepository.findById(couponId)
                .map(couponsVO -> {
                    if (couponsVO.getStatus() == CouponsStatus.REVIEWING.getCode()) { // 仅审核中的优惠券可以被拒绝
                        couponsVO.setStatus(CouponsStatus.REJECTED.getCode()); // 设置状态为 "审核拒绝"
                        couponsVO.setRejectionReason(rejectionReason);
                        couponsVO.setUpdatedAt(LocalDateTime.now());
                        return couponsVO;
                    } else {
                        throw new IllegalStateException("仅审核中的优惠券可以被拒绝");
                    }
                })
                .flatMap(couponsRepository::save)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("优惠券未找到")));
    }

    /**
     * 审核通过操作，将优惠券状态设置为 "审核通过"。
     *
     * @param couponId 优惠券ID
     * @return 更新后的优惠券信息
     */
    public Mono<CouponsVO> approve(String couponId) {
        return couponsRepository.findById(couponId)
                .map(couponsVO -> {
                    if (couponsVO.getStatus() == CouponsStatus.REVIEWING.getCode()) { // 仅审核中的优惠券可以被审核通过
                        couponsVO.setStatus(CouponsStatus.APPROVED.getCode()); // 设置状态为 "审核通过"
                        couponsVO.setUpdatedAt(LocalDateTime.now());
                        return couponsVO;
                    } else {
                        throw new IllegalStateException("仅审核中的优惠券可以被审核通过");
                    }
                })
                .flatMap(couponsRepository::save)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("优惠券未找到")));
    }

    public Mono<String> delete(String couponId) {
        return couponsRepository.findById(couponId).switchIfEmpty(Mono.error(new IllegalArgumentException("优惠券未找到")))
                .map(couponsVO -> {
                    if (couponsVO.getStatus() == CouponsStatus.DRAFT.getCode() || couponsVO.getStatus() == CouponsStatus.REJECTED.getCode()) { // 仅审核中的优惠券可以被审核通过
                        return couponId;
                    } else {
                        throw new IllegalStateException("仅草稿中或者审核拒绝的优惠券可以被删除");
                    }
                })
                .flatMap(couponsRepository::deleteById).then(Mono.fromCallable(() -> couponId));
    }

}
