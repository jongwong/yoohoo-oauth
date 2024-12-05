package cn.jongwong.authorization.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class HelloController {

    @GetMapping("/ping")
    public Mono<String> getPing() {
        return Mono.just("pong");  // 返回纯文本响应
    }

}
