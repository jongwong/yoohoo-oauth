package cn.jongwong.server;

import cn.jongwong.server.dto.order.OrderPayDTO;
import cn.jongwong.server.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;

@SpringBootTest
@WebAppConfiguration
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;


    @Test
    public void getUserByMobileNumber() {
        var order = new OrderPayDTO();
        order.setOrderId("52d577ac-ac8c-47a4-be55-81b132075240");
        order.setOpenId("o2FE96jbCVe0Oo6gmB7lsMFHJR4I");
        var userVO = orderService.payOrder(order).block();
    }


}
