package cn.jongwong.server.service;

import cn.jongwong.server.common.MapperUtil;
import cn.jongwong.server.common.SnowflakeIdUtils;
import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.dto.order.OrderPayDTO;
import cn.jongwong.server.dto.order.OrderProductItemDTO;
import cn.jongwong.server.dto.order.OrderRefundDTO;
import cn.jongwong.server.dto.order.OrderSubmitDTO;
import cn.jongwong.server.entity.*;
import cn.jongwong.server.enums.OrderItemTypeEnum;
import cn.jongwong.server.enums.OrderStatusEnum;
import cn.jongwong.server.enums.PaymentStatusEnum;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class OrderService {

    @Autowired
    private UserService userService;


    @Autowired
    private PaymentService paymentService;
    @Autowired
    private RefundService refundService;


    @Autowired
    private WeChatPayService weChatPayService;

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
        data.setUpdatedAt(LocalDateTime.now());
        return orderRepository.save(data);
    }

    public Mono<OrderVO> insert(OrderVO data) {

        return userService.getCurrentUserReactive().flatMap((u) -> {
            data.setCreatedByName(u.getName());
            data.setCreatedBy(u.getId());
            data.setCreatedAt(LocalDateTime.now());
            return orderRepository.insert(data);
        });
    }

    @Transactional
    public Mono<OrderVO> cancelById(String id) {
        return userService.getCurrentUserReactive().flatMap((u) -> orderRepository.findById(id).map(order -> {
            if (order.getStatus() != OrderStatusEnum.PENDING_PAYMENT.getCode()) {
                throw new RuntimeException("订单状态不正确");
            }
            order.setUpdatedBy(u.getId());
            order.setUpdatedByName(u.getName());
            order.setUpdatedAt(LocalDateTime.now());
            order.setStatus(OrderStatusEnum.CANCELLED.getCode());

            return order;
                }).flatMap((order) -> {
                    if (order.getCouponsId() == null) {
                        return Mono.just(order);

                    }
                    return userCouponsService.clearAsUsed(order.getCouponsId()).map(uc -> order);
                }).flatMap(this::update).
                flatMap(order -> paymentService.findOneById(id).map(payment -> {
            if (payment.getStatus() == PaymentStatusEnum.PENDING_PAYMENT.getCode()) {
                throw new RuntimeException("支付状态不正确");
            }
            payment.setStatus(PaymentStatusEnum.CANCELLED.getCode());

            return payment;
                }).flatMap(paymentService::update).map(e -> order)));
    }

    public Mono<Page<OrderVO>> queryByUserId(int page, int size, String userId, Integer status) {
        return orderRepository.findPageByDSL(page, size, sqlBuilder -> sqlBuilder.eq("user_id", userId).eq("status", status).sort("status,asc").sort("created_at,desc"))
                .flatMap(e -> {
                    List<String> ids = e.getData().stream()
                            .map(OrderVO::getId)   // 假设 getId() 返回的是 String
                            .toList();
                    var items = orderItemRepository.findAllByDSL(sql -> sql.in("order_id", ids));
                    return items.collectList().map(orderItems -> {
                        e.getData().forEach(order -> {
                            var orderItem = orderItems.stream()
                                    .filter(it -> it.getOrderId().equals(order.getId()))
                                    .toList();
                            order.setItems(orderItem);
                        });
                        return e;
                    });
                });

    }

    @Transactional
    public Mono<OrderVO> findOneByOrderId(String orderId) {
        return orderRepository.findById(orderId)
                .flatMap(order -> {

                    paymentService.findByOrderId(orderId).map(payment -> {
                        order.setPaymentAt(payment.getPaymentAt());
                        return order;
                    });
                    var items = orderItemRepository.findAllByDSL(sql -> sql.eq("order_id", orderId));
                    return items.collectList().map(orderItems -> {
                        order.setItems(orderItems);
                        return order;
                    });
                });

    }

    @Transactional
    public Mono<OrderVO> findOneByOrderNum(String orderNum) {
        return orderRepository.findByNum(orderNum)
                .flatMap(order -> {

                    paymentService.findByOrderId(order.getId()).map(payment -> {
                        order.setPaymentAt(payment.getPaymentAt());
                        return order;
                    });
                    var items = orderItemRepository.findAllByDSL(sql -> sql.eq("num", orderNum));
                    return items.collectList().map(orderItems -> {
                        order.setItems(orderItems);
                        return order;
                    });
                });

    }

    @Transactional
    public Mono<OrderVO> submit(OrderSubmitDTO data) {

        return createBusinessOrder(data);
    }

    @Transactional
    public Mono<OrderVO> payOrder(OrderPayDTO data) {
        return findOneByOrderId(data.getOrderId())
                .flatMap(order -> {
                    return weChatPayService.createJsApiOrder(data.getOpenId(), order).map(re -> {
                        order.setPrepayInfo(re);
                        return order;
                    });
                });
    }

    @Transactional
    public Mono<OrderVO> refund(OrderRefundDTO data) {
        var now = LocalDateTime.now();
        AtomicReference<PaymentVO> payment = new AtomicReference<>();
        return paymentService.findByOrderId(data.getOrderId()).map((e) -> {
                    payment.set(e);
                    return e;
                }).flatMap((p) -> {
                    return findOneByOrderId(data.getOrderId());
                }).flatMap((order) -> {
                    var p = payment.get();
                    if (p == null) {
                        return Mono.error(new Exception("支付记录不存在"));

                    }

                    return weChatPayService.refundJsApiOrder(order, payment.get(), data).flatMap(re -> {
                        var transactionId = re.get("transaction_id");
                        var outRefundNo = re.get("out_refund_no");
                        var refundAmountStr = re.get("refund_amount");
                        // 转成int
                        var refundAmount = Integer.parseInt(refundAmountStr);

                        var refund = RefundVO.builder()
                                .id(UUID.randomUUID().toString())
                                .orderId(data.getOrderId())
                                .amount(refundAmount)
                                .paymentMethod(1)
                                .status(10)
                                .refundAt(now)
                                .transactionId(transactionId)
                                .transactionNo(outRefundNo)
                                .createdAt(now)
                                .createdBy(order.getCreatedBy())
                                .createdByName(order.getCreatedByName())
                                .build();
                        return refundService.insert(refund).map((e) -> order);
                    });


                })
                .map(order -> {
                    order.setStatus(OrderStatusEnum.REFUND_IN_PROGRESS.getCode());
                    return order;
                }).flatMap(this::update);

    }

    @Transactional
    public Mono<OrderVO> finishRefund(String orderNum, String outTradeNo, String transactionId) {

        return orderRepository.findByNum(orderNum).flatMap(order -> {
            order.setStatus(OrderStatusEnum.REFUNDED.getCode());
            var now = LocalDateTime.now();
            order.setPaymentAt(now);
            return refundService.findByOrderId(order.getId()).map(refund -> {
                refund.setUpdatedAt(now);
                refund.setTransactionId(transactionId);
                refund.setTransactionNo(outTradeNo);

                refund.setStatus(PaymentStatusEnum.PAYMENT_SUCCESS.getCode());
                return refund;
            }).flatMap(refundService::update).map(e -> order).flatMap(this::update);
        }).doOnError(e -> {
            e.printStackTrace();
        });
    }


    @Transactional
    public Mono<OrderVO> finishPayment(String orderNum, String outTradeNo, String transactionId) {

        return orderRepository.findByNum(orderNum).flatMap(order -> {
            order.setStatus(OrderStatusEnum.PENDING_DELIVERY.getCode());
            var now = LocalDateTime.now();
            order.setPaymentAt(now);
            return paymentService.findByOrderId(order.getId()).map(payment -> {
                payment.setUpdatedAt(now);
                payment.setTransactionId(transactionId);
                payment.setTransactionNo(outTradeNo);
                payment.setStatus(PaymentStatusEnum.PAYMENT_SUCCESS.getCode());
                return payment;
            }).flatMap(paymentService::update).map(e -> order).flatMap(this::update);
        }).doOnError(e -> {
            e.printStackTrace();
        });
    }



    @Transactional
    public Mono<OrderVO> createBusinessOrder(OrderSubmitDTO data) {
        var now = LocalDateTime.now();
        String orderNum = String.valueOf(SnowflakeIdUtils.generateId());

        var order = OrderVO.builder().id(UUID.randomUUID().toString())
                .deliveryPointId(data.getDeliveryPointId())
                .deliveryPointName(data.getDeliveryPointName())
                .num(orderNum)
                .createdAt(now)
                .deliveryPointAddress(data.getDeliveryPointAddress())
                .status(OrderStatusEnum.PENDING_PAYMENT.getCode())
                .build();
        MapperUtil.merge(order, data);


        return userService.getCurrentUserReactive().flatMap(u -> {
            order.setUserId(u.getId());
            order.setCreatedBy(u.getId());
            order.setCreatedByName(u.getName());
            return Mono.just(order);
        }).flatMap(o -> {

            var ids = data.getProducts().stream()
                    .map(OrderProductItemDTO::getGroupProductId)
                    .toList();
            Flux<ClientPurchaseGroupProductVO> productsMono = groupProductService.findAllByIds(ids).map(e -> {
                if (e == null) {
                    throw new RuntimeException("商品不存在");
                }
                return e;
            });


            return productsMono.collectList().map(re -> {
                int total = re.stream()
                        .mapToInt(p -> p.getPrice() * data.getProducts().stream()
                                .filter(it -> it.getGroupProductId().equals(p.getId()))
                                .findFirst()
                                .map(it -> it.getCount())
                                .orElse(0)) // 避免空指针异常
                        .sum();

                if (total != data.getAmountProduct()) {
                    throw new RuntimeException("商品金额不匹配");
                }

                re.forEach(p -> {
                    OrderProductItemDTO find = data.getProducts().stream()
                            .filter(it -> it.getId().equals(p.getId()))
                            .findFirst().orElse(null);
                    ;
                    if (find != null) {

                        if (p.getArchivedStatus() != ProductArchivedStatus.COMPLETED.getCode()) {
                            throw new RuntimeException("商品状态不能为" + ProductArchivedStatus.fromCode(p.getArchivedStatus()).getDescription());
                        }

                        if (p.getListedStatus() != ProductListedStatus.LISTED.getCode()) {
                            throw new RuntimeException("商品上架状态不能为" + ProductListedStatus.fromCode(p.getListedStatus()).getDescription());
                        }

                        var leftover = p.getMaxStock() - p.getSoldQuantity();
                        if (leftover < find.getCount()) {
                            throw new RuntimeException("商品库存不足");
                        }

                        if (p.getPrice().compareTo(find.getPrice()) != 0) {
                            throw new RuntimeException("价格已经发生变化，请重新刷新页面数据");
                        }
                    }

                });


                return order;
            });
        }).flatMap((o) -> {
            if (data.getCouponsId() == null) {
                return Mono.just(o);
            }
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
                    if (!coupons.getDiscountAmount().equals(data.getAmountDiscount())) {
                        throw new RuntimeException("优惠券金额不匹配");
                    }


                    if (coupons.getDisable().equals(1)) {
                        throw new RuntimeException("优惠券已停用");
                    }
                    return userCoupon;
                }).flatMap((uc) -> {
                    return userCouponsService.markAsUsed(data.getCouponsId()).map(_uc -> {
                        return order;
                    });

                });
            });

        }).flatMap(this::insert).flatMap(
                savedOrder -> {
                    // 保存订单明细
                    List<OrderItemVO> orderItems = new ArrayList<>();
                    for (OrderProductItemDTO product : data.getProducts()) {
                        OrderItemVO item = OrderItemVO.builder()
                                .orderId(savedOrder.getId())
                                .type(OrderItemTypeEnum.PRODUCT.getCode())
                                .amount(product.getPrice() * product.getCount())
                                .productId(product.getId())
                                .productCode(product.getCode())
                                .productName(product.getName())
                                .groupProductId(product.getGroupProductId())
                                .productImageUrl(product.getImageUrl())
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
        ).flatMap(savedOrder -> {
            var payment = PaymentVO.builder()
                    .id(UUID.randomUUID().toString())
                    .orderId(savedOrder.getId())
                    .createdAt(savedOrder.getCreatedAt())
                    .createdBy(savedOrder.getCreatedBy())
                    .createdByName(savedOrder.getCreatedByName())
                    .amount(savedOrder.getAmountTotal())
                    .status(PaymentStatusEnum.PENDING_PAYMENT.getCode())
                    .build();
            return paymentService.insert(payment).map(p -> {
                return savedOrder;
            });
        });
    }
}
