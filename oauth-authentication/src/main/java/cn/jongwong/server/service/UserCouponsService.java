package cn.jongwong.server.service;

import cn.jongwong.server.common.QueryBuilder;
import cn.jongwong.server.entity.CouponsVO;
import cn.jongwong.server.entity.UserCouponsRO;
import cn.jongwong.server.entity.UserCouponsVO;
import cn.jongwong.server.enums.coupons.CouponsStatus;
import cn.jongwong.server.repository.UserCouponsRepository;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserCouponsService {

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    private UserCouponsRepository userCouponsRepository;

    @Autowired
    private CouponsService couponsService;

    @Autowired
    private UserService userService;


    /**
     * 根据用户优惠券ID查找用户优惠券
     *
     * @param id 用户优惠券ID
     * @return 用户优惠券详情
     */
    public Mono<UserCouponsVO> findById(String id) {
        return userCouponsRepository.findById(id)
                .switchIfEmpty(Mono.error(new IllegalArgumentException("用户优惠券未找到")));
    }

    /**
     * 创建用户优惠券
     *
     * @param userCouponsVO 用户优惠券信息
     * @return 创建后的用户优惠券详情
     */
    public Mono<UserCouponsVO> createUserCoupon(UserCouponsVO userCouponsVO) {
        return couponsService.findById(userCouponsVO.getCouponsId())
                .flatMap(coupon -> {
                    if (coupon.getStatus() != CouponsStatus.APPROVED.getCode()) {
                        return Mono.error(new IllegalStateException("仅审核通过的优惠券可以分发"));
                    }
                    UserCouponsVO userCoupon = UserCouponsVO.builder()
                            .id(UUID.randomUUID().toString())
                            .userId(userCouponsVO.getUserId())
                            .couponsId(userCouponsVO.getCouponsId())
                            .isUsed(false)
                            .createdAt(LocalDateTime.now())
                            .validFrom(coupon.getValidFrom())
                            .validTo(coupon.getValidTo())
                            .build();
                    return userCouponsRepository.insert(userCoupon);
                });
    }

    /**
     * 标记用户优惠券为已使用
     *
     * @param id 用户优惠券ID
     * @return 更新后的用户优惠券详情
     */
    public Mono<UserCouponsVO> markAsUsed(String id) {
        return userCouponsRepository.findById(id)
                .flatMap(userCoupon -> {
                    if (userCoupon.getIsUsed()) {
                        return Mono.error(new IllegalStateException("优惠券已使用"));
                    }
                    userCoupon.setIsUsed(true);
                    userCoupon.setUsedAt(LocalDateTime.now());
                    return userCouponsRepository.save(userCoupon);
                });
    }

    public Mono<UserCouponsVO> clearAsUsed(String id) {
        return userCouponsRepository.findById(id)
                .flatMap(userCoupon -> {

                    userCoupon.setIsUsed(false);
                    userCoupon.setUsedAt(null);
                    return userCouponsRepository.save(userCoupon);
                });
    }

    /**
     * 删除用户优惠券（仅未使用的优惠券可删除）
     *
     * @param id 用户优惠券ID
     * @return 删除成功的优惠券ID
     */
    public Mono<String> delete(String id) {
        return userCouponsRepository.findById(id)
                .flatMap(userCoupon -> {
                    if (userCoupon.getIsUsed()) {
                        return Mono.error(new IllegalStateException("已使用的优惠券无法删除"));
                    }
                    return userCouponsRepository.deleteById(id).then(Mono.just(id));
                });
    }

    /**
     * 搜索用户优惠券
     *
     * @param userId 用户ID
     * @param page   当前页码
     * @param size   每页大小
     * @return 用户优惠券分页结果
     */
    public Mono<Page<UserCouponsVO>> searchUserByUserId(String userId, int page, int size) {
        // 创建 QueryBuilder 实例
        QueryBuilder<UserCouponsVO> queryBuilder = new QueryBuilder<>(r2dbcEntityTemplate, UserCouponsVO.class);

        // 添加查询条件：根据 couponsId 查找
        queryBuilder.addEqualCondition("user_id", userId);

        // 设置联表查询：假设 UserCouponsVO 表有 user_id 字段，User 表有 id 字段
        queryBuilder.withJoin("tb_user u ON u.id = tb_user_coupons.user_id");  // 根据实际表名和字段修改

        queryBuilder.selectFields("tb_user_coupons.*, u.name AS user_name");

        // 设置自定义字段映射：为 user_name 字段设置额外的处理逻辑
        queryBuilder.withFieldMapping((row, userCouponsVO) -> {
            String userName = row.get("user_name", String.class);
            userCouponsVO.setUserName(userName); // 自定义字段处理
            return userCouponsVO;
        });

        // 执行查询并返回分页结果
        return queryBuilder.paginate(page, size).exec();
    }

    public Mono<Page<UserCouponsVO>> searchUserByCouponsId(String couponsId, int page, int size) {
        // 创建 QueryBuilder 实例
        QueryBuilder<UserCouponsVO> queryBuilder = new QueryBuilder<>(r2dbcEntityTemplate, UserCouponsVO.class);

        // 添加查询条件：根据 couponsId 查找
        queryBuilder.addEqualCondition("coupons_id", couponsId);

        // 设置联表查询：假设 UserCouponsVO 表有 user_id 字段，User 表有 id 字段
        queryBuilder.withJoin("tb_user u ON u.id = tb_user_coupons.user_id");  // 根据实际表名和字段修改

        queryBuilder.selectFields("tb_user_coupons.*, u.name AS user_name");

        // 设置自定义字段映射：为 user_name 字段设置额外的处理逻辑
        queryBuilder.withFieldMapping((row, userCouponsVO) -> {
            String userName = row.get("user_name", String.class);
            userCouponsVO.setUserName(userName); // 自定义字段处理
            return userCouponsVO;
        });

        // 执行查询并返回分页结果
        return queryBuilder.paginate(page, size).exec();
    }


    public Flux<UserCouponsRO> getUserCouponsByUserId(String userId, Integer isUsed) {

        return userCouponsRepository.findUserCouponsByUserId(userId, isUsed);
    }

    public Flux<UserCouponsVO> issue(List<String> userIds, String couponsId) {

        var userCouponsROFlux = Flux.fromIterable(userIds)
                .flatMap(userId -> {
                    UserCouponsVO userCouponsRO = new UserCouponsVO();
                    userCouponsRO.setId(UUID.randomUUID().toString());
                    userCouponsRO.setUserId(userId);
                    userCouponsRO.setCouponsId(couponsId);
                    userCouponsRO.setIsUsed(false);
                    userCouponsRO.setCreatedAt(LocalDateTime.now());
                    return Mono.just(userCouponsRO);
                }).flatMap(uc -> userService.getCurrentUserReactive().map(user -> {
                    uc.setCreatedBy(user.getId());
                    uc.setCreatedByName(user.getName());
                    return uc;
                }));

        return couponsService.findById(couponsId).<CouponsVO>handle((c, sink) -> {

            if (c.getStatus() != CouponsStatus.APPROVED.getCode()) {
                sink.error(new IllegalStateException("仅审核通过的优惠券可以分发"));
                return;
            }
            if (c.getDisable().equals(1)) {
                sink.error(new IllegalStateException("仅审核通过的优惠券可以分发"));
                return;
            }

            // 时间对比是否失效
            if (c.getValidFrom() != null && c.getValidTo() != null) {
                if (c.getValidFrom().isAfter(LocalDateTime.now()) || c.getValidTo().isBefore(LocalDateTime.now())) {
                    sink.error(new IllegalStateException("优惠券已失效"));
                    return;
                }
            }

            sink.next(c);

        }).flatMapMany(c -> userCouponsROFlux
                .flatMap(userCouponsRO -> {
                    userCouponsRO.setValidFrom(c.getValidFrom());
                    userCouponsRO.setValidTo(c.getValidTo());
                    return userCouponsRepository.insert(userCouponsRO);
                }));
    }
}
