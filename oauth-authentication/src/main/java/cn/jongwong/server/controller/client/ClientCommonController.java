package cn.jongwong.server.controller.client;


import cn.jongwong.server.util.response.Response;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController()
@RequestMapping("/client")
public class ClientCommonController {
    // 获取最近的配送点
    @GetMapping("/delivery/fee")
    public Mono<Response<Double>> getDeliveryFee() {
        var fee = 1.5;
        System.out.printf("-------222.-------%s%n", 222);
        // String 转成 浮点数
        return Mono.just(fee).map(Response::ok);
    }
}
