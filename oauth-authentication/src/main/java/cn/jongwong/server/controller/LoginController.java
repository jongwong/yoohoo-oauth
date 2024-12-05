package cn.jongwong.server.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Controller
public class LoginController {
    @GetMapping("/logout-success")
    public String logoutSuccess(Model model) {
        // Add any necessary data to the model
        model.addAttribute("message", "You have been logged out successfully.");
        return "logout-success";  // Return the view name for the logout success page
    }

    @GetMapping("/login")
    public Mono<String> login() {
        // Return the name of the Thymeleaf template
        return Mono.just("login");  // It will look for 'src/main/resources/templates/login.html'
    }

    @GetMapping("/home")
    public Mono<String> Home(ServerWebExchange exchange) {
        return Mono.just("home");
    }


}
