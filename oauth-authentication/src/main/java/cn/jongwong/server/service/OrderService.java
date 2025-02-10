package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.dto.order.OrderProductItemDTO;
import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.ClientPurchaseGroupProductVO;
import cn.jongwong.server.entity.OrderItemVO;
import cn.jongwong.server.entity.OrderVO;
import cn.jongwong.server.enums.OrderItemTypeEnum;
import cn.jongwong.server.enums.OrderStatusEnum;
import cn.jongwong.server.enums.product.ProductArchivedStatus;
import cn.jongwong.server.enums.product.ProductListedStatus;
import cn.jongwong.server.repository.OrderItemRepository;
import cn.jongwong.server.repository.OrderRepository;
import cn.jongwong.server.service.product.PurchaseGroupProductService;
import cn.jongwong.server.util.response.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserCouponsService userCouponsService;

    @Autowired
    private CouponsService couponsService;


    @Autowired
    private PurchaseGroupProductService groupProductService;

    @Autowired
    private OrderItemRepository orderItemRepository;

    public Mono<OrderVO> update(OrderVO data) {
        return orderRepository.insert(data);
    }

    public Mono<Page<OrderVO>> queryByUserId(int page, int size, String userId) {
        // TODO
        return Mono.empty();

    }

    @Transactional
    public Mono<OrderVO> submit(OrderSubmitDTO data) {

        var now = LocalDateTime.now();
        var order = OrderVO.builder().id(UUID.randomUUID().toString())
                .deliveryPointId(data.getDeliveryPointId())
                .deliveryPointName(data.getDeliveryPointName())
                .deliveryPointAddress(data.getDeliveryPointAddress())
                .status(OrderStatusEnum.PENDING_PAYMENT.getCode())
                .build();
        MapperUtil.merge(order, data);


        return userService.getCurrentUser().flatMap(u -> {
            order.setCreatedAt(LocalDateTime.now());
            order.setCreatedBy(u.getId());
            order.setUserId(u.getId());
            order.setCreatedByName(u.getName());
            return Mono.just(order);
        }).flatMap(o -> {

            var ids = data.getProducts().stream()
                    .map(OrderProductItemDTO::getId)
                    .toList();

            Flux<ClientPurchaseGroupProductVO> productsMono = groupProductService.findAllByIds(ids);


            return productsMono.collectList().map(re -> {
                System.out.printf("-------re-------%s%n", re);
                var total = re.stream().map(ClientPurchaseGroupProductVO::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
                System.out.printf("-------total-------%s%n", total);
                System.out.printf("-------data.getAmountProduct()-------%s%n", data.getAmountProduct());
                if (total.compareTo(data.getAmountProduct()) != 0) {
                    throw new RuntimeException("商品金额不匹配");
                }
                re.forEach(p -> {
                    var find = data.getProducts().stream()
                            .filter(it -> it.getId().equals(p.getId()))  // 根据 p.getId() 找到对应的产品
                            .findFirst().get();  // 获取第一个匹配的元素

                    if (p.getArchivedStatus() != ProductArchivedStatus.COMPLETED.getCode()) {
                        throw new RuntimeException("商品状态不能为" + ProductArchivedStatus.fromCode(p.getArchivedStatus()).getDescription());
                    }

                    if (p.getListedStatus() != ProductListedStatus.LISTED.getCode()) {
                        throw new RuntimeException("商品上架状态不能为" + ProductListedStatus.fromCode(p.getListedStatus()).getDescription());
                    }

                    var leftover = p.getMaxStock() - p.getSoldQuantity();
                    if (leftover < find.getNum()) {
                        throw new RuntimeException("商品库存不足");
                    }

                    if (p.getPrice().compareTo(find.getPrice()) != 0) {
                        throw new RuntimeException("价格已经发生变化，请重新刷新页面数据");
                    }
                });


                return order;
            });
        }).flatMap((o) -> {
            //校验优惠券金额是否存在
            return userCouponsService.findById(data.getCouponsId()).map(userCoupon -> {

                // 校验优惠券时间
                if (userCoupon.getValidTo().isBefore(now)) {
                    throw new RuntimeException("优惠券已过期");
                }
                if (userCoupon.getValidFrom().isAfter(now)) {
                    throw new RuntimeException("优惠券还未生效");
                }
                if (userCoupon.getIsUsed()) {
                    throw new RuntimeException("优惠券已经使用过");
                }


                return userCoupon;
            }).flatMap(userCoupon -> {

                return couponsService.findById(userCoupon.getCouponsId()).map(coupons -> {
                    if (coupons.getDiscountAmount().compareTo(data.getAmountDiscount()) != 0) {
                        throw new RuntimeException("优惠券金额不匹配");
                    }

                    if (coupons.getDisable().equals(1)) {
                        throw new RuntimeException("优惠券已停用");
                    }
                    return userCoupon;
                });
            });

        }).flatMap(userCoupon -> {
            return userCouponsService.markAsUsed(data.getCouponsId()).map(uc -> {
                return order;
            });
        }).flatMap(this::update).flatMap(
                savedOrder -> {
                    // 保存订单明细
                    List<OrderItemVO> orderItems = new ArrayList<>();
                    for (OrderProductItemDTO product : data.getProducts()) {
                        OrderItemVO item = OrderItemVO.builder()
                                .orderId(savedOrder.getId())
                                .type(OrderItemTypeEnum.PRODUCT.getCode())
                                .amount(product.getPrice().multiply(BigDecimal.valueOf(product.getNum())))
                                .refId(product.getId())
                                .refCode(product.getCode())
                                .refName(product.getName())
                                .createdAt(LocalDateTime.now())
                                .updatedAt(LocalDateTime.now())
                                .build();
                        orderItems.add(item);
                    }

                    // 保存所有的订单明细
                    return orderItemRepository.saveRefAll(orderItems).collectList().map(
                            items -> {
                                savedOrder.setItems(items);
                                return savedOrder;
                            }
                    );
                }
        );
    }
}
