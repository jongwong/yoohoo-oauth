package cn.jongwong.server.repository;


import cn.jongwong.server.common.GenericReactiveRepository;
import cn.jongwong.server.entity.UserCouponsRO;
import cn.jongwong.server.entity.UserCouponsVO;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface UserCouponsRepository extends GenericReactiveRepository<UserCouponsVO, String> {
    Mono<UserCouponsVO> findByCouponsId(String couponsId);


    @Query("""
            SELECT
                uc.id,
                uc.user_id,
                uc.coupons_id, 
                uc.is_used,
                uc.created_at, 
                uc.used_at,
                uc.valid_from, 
                uc.valid_to, 
                uc.dynamic_scope_type,
                uc.dynamic_scope_id,
                c.name AS coupons_name, 
                c.type AS coupons_type, 
                c.discount_amount, 
                c.discount_percentage, 
                c.min_spend, 
                c.max_discount, 
                c.valid_from AS coupons_valid_from, 
                c.valid_to AS coupons_valid_to, 
                c.status AS coupons_status
            FROM
            `yoohoo-oauth`.tb_user_coupons uc
            LEFT JOIN
            `yoohoo-oauth`.tb_coupons c
            ON uc.coupons_id = c.id
            WHERE uc.user_id = :userId
            """)
    Flux<UserCouponsRO> findUserCouponsByUserId(String userId);
}

