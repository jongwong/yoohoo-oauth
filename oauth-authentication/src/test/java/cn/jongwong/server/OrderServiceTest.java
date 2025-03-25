package cn.jongwong.server;

import cn.jongwong.server.config.wechatpay.WeChatPayService;
import cn.jongwong.server.dto.order.OrderPayDTO;
import cn.jongwong.server.entity.PaymentVO;
import cn.jongwong.server.enums.PaymentStatusEnum;
import cn.jongwong.server.service.OrderService;
import cn.jongwong.server.service.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wechat.pay.java.core.notification.Notification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@SpringBootTest
@WebAppConfiguration
public class OrderServiceTest {


    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private WeChatPayService weChatPayService;

    @Autowired
    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        // 模拟用户认证
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "testuser", // 用户名
                "password", // 密码
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")) // 权限
        );

        // 手动将认证信息设置到 SecurityContext
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    public void getUserByMobileNumber() {
        var order = new OrderPayDTO();
        order.setOrderId("52d577ac-ac8c-47a4-be55-81b132075240");
        order.setOpenId("o2FE96jbCVe0Oo6gmB7lsMFHJR4I");
        var userVO = orderService.payOrder(order).block();
    }

    @Test
    public void paymentCreate() {
        var payment = PaymentVO.builder()
                .id(UUID.randomUUID().toString())
                .amount(1)
                .status(PaymentStatusEnum.PENDING_PAYMENT.getCode())
                .build();
        paymentService.insert(payment);
    }


    public void handlePaymentCallback() {
        try {
            var xx = weChatPayService.handlePaymentCallback("6u3hpXrd5SWoMkghB3aILTn1UK9D45IDzppTUIPFPoN7FT8GY7s2DUPv32w31IJrEZO40sUvNoq2vdJZYRIHjZ4fwH1reN4bbLoKgNSGQq/3q7+3sO7Fq9C/f8vM3tyjrhhEs7CjTY32JLG5/uzQxeM7Khj6Lj1DubrIqIuqM+Z23NCtTfa4xEFUsXLq6P9T20V0E9zpc/r9tmcpY1yOrgULmjpXV+kpcQFVRRXTqeOlBRgfgKeSLxIofWJ0qzvWMLkbI+mzVU9/3fBx0/D0eKfsH0J4XNN92JDhTBfxxk4l/1XAh7uixIVqqz/fkk/3dOVNpFgZLTag/YNTaNtxIYQdi19yXTPPA4XLiJcLRlUjOpVb+KAN0csFftmA7ydSHvigGO2XLi1NVx7UgCxaqFWo+WfDSq+GUoHp9sfNhHFcFf+BY54Hhmy3ORhwkXdDvmeNIOK9n6lOMdnPWtXv7MsxrJaWbRhcTtJYrsXbc9+NV2SIn1oC8WBE2dZIfjdhfsM0LJuvV+6Tyq9qa1twCkYx18iqpIsppkC0xMeWubYD81Rfnvz0gjFlyHp9hIh3CX3VxnsTsCqhwWPqQAOa5G7kInPPqusYCoMpWV35gl80abUwD1q8w3TWYSb4vIPWcV4k3a8rPDKpqmPFnJ5W8qpe+g==", "transaction", "wCo0pIUp98Kk");
            System.out.printf("-------xx-------%s%n", xx);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void readValuePaymentCallback() {

        String json = "{"
                + "\"id\":\"484006eb-c7b4-53b9-ba29-286e09f003ee\","
                + "\"create_time\":\"2025-03-08T15:35:34+08:00\","
                + "\"resource_type\":\"encrypt-resource\","
                + "\"event_type\":\"TRANSACTION.SUCCESS\","
                + "\"summary\":\"支付成功\","
                + "\"resource\":{"
                + "  \"original_type\":\"transaction\","
                + "  \"algorithm\":\"AEAD_AES_256_GCM\","
                + "  \"ciphertext\":\"/hHdTaC3Xcc0692JjRyTbGnKuO/cQJvNXGxxWKlvbqUYMcoC7Ixk/Lmyr0sMkA2+L0oDl9nUb/5Ys5IBXj9DixspNFBB9gSLZNxsUn/G/Ws85DAOoTtSB7RBToFHr0bEXvAXme2xvWirqYODhmOM51NVurT3ReLzjQgpLEv5z34Lm7+Aop/ilskYd+gA8kujLnPRH9OOJ8Wjo3ixcuBscbgWbYZq7qw36ivC8377w3aFuui4QGonusBgv/oIwQG2/WNYiXIyOl2ctnp5Dtr3+UIwgcRWjE0Zi1EE5cb/3xFMPsgcB4f7TQzal9iZwU3Rs3Xu3ZyZ3F0FoItaQ65+Wdq0FEpaHQe1bKOEWqKYgFfefbQMnMasDfULMHCIoKfvYNWF+HN7txFVEHLsNy2x0X+ET/HKQ7y+106s8WXfho7q0jFvlFIk6J1o5EalleaxrALC5+8aOEU0kiQyH8jyONKDxKP9C0epLzUcSl3ii6L/GeQt/HX4lFoiL0Q6BGdb3MGRHbU+1IqXgV2pElFd+qZN2s9xA52HpYnoWC+GbduIVAhBqshEtlUq2f6NtwRa3V78w+MTXv6Eyj35G1IZ4Vs8sJZF1hsl7U19kjubk/yoGVtzp5gA+v4NJZn5zsNNtJC5GA==\","
                + "  \"associated_data\":\"transaction\","
                + "  \"nonce\":\"49XFUUh4VrAr\""
                + "}"
                + "}";


        try {
            Notification callback = objectMapper.readValue(json, Notification.class);

            System.out.println("解析成功: " + callback.getResource().getCiphertext());

            var source = callback.getResource();
            var reStr = weChatPayService.handlePaymentCallback(source.getCiphertext(), source.getAssociatedData(), source.getNonce());
            System.out.printf("-------reStr-------%s%n", reStr);

            var result = objectMapper.readValue(json, Map.class);
            System.out.printf("-------re-------%s%n", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    public void finishPayment() {
        // 在这里可以使用 SecurityContextHolder.getContext().getAuthentication() 来验证当前的认证用户
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("--333----" + authentication);
        orderService.finishPayment("553475223693955072", "tesst", "lll").block();
    }






}
