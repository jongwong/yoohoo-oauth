package cn.jongwong.server.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class HelloController {


    @GetMapping("/hello")
    public Mono<String> login() {
        // Return the name of the Thymeleaf template
        return Mono.just("hello");  // It will look for 'src/main/resources/templates/login.html'
    }

    @GetMapping("/test")
    public Mono<String> test() {
        // Return the name of the Thymeleaf template
        return Mono.just("test");  // It will look for 'src/main/resources/templates/login.html'
    }


}
