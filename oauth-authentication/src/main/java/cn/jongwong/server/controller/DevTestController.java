package cn.jongwong.server.controller;


import cn.jongwong.server.util.response.Response;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

@RestController
@Profile({"local"})
public class DevTestController {


    @GetMapping("/admin/hello")
    public Mono<Response<String>> login() {
        // Return the name of the Thymeleaf template
        return Mono.just(Response.success("hello"));  // It will look for 'src/main/resources/templates/login.html'
    }

    @GetMapping("/admin/test")
    public Mono<Response<String>> test() {
        // Return the name of the Thymeleaf template
        return Mono.just(Response.success("test"));   // It will look for 'src/main/resources/templates/login.html'
    }


    @GetMapping("/set-websession")
    public String setWebSession(ServerWebExchange exchange) {
        WebSession webSession = exchange.getSession().block();  // 获取 WebSession
        webSession.getAttributes().put("username", "john_doe");
        return "WebSession data set!";
    }

    @GetMapping("/get-websession")
    public String getWebSession(ServerWebExchange exchange) {
        WebSession webSession = exchange.getSession().block();  // 获取 WebSession
        String username = (String) webSession.getAttributes().get("username");
        return "Username: " + username;
    }

}
