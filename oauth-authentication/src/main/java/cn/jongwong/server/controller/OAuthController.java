package cn.jongwong.server.controller;

import cn.jongwong.server.dto.authentication.AuthorizationForm;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
public class OAuthController {

    // 显示授权页面
    @GetMapping("/oauth2/authorize")
    public Mono<String> showAuthorizePage(
            @RequestParam String client_id,
            @RequestParam String redirect_uri,
            @RequestParam String response_type,
            @RequestParam String scope,
            @RequestParam(required = false) String state,
            Model model) {

        // 检查用户是否已认证
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            // 用户未登录，重定向到登录页面
            return Mono.just("redirect:/login");
        }

        // 将参数传递到授权页面
        model.addAttribute("client_id", client_id);
        model.addAttribute("redirect_uri", redirect_uri);
        model.addAttribute("response_type", response_type);
        model.addAttribute("scope", scope);
        model.addAttribute("state", state);

        // 你可以在这里添加自定义的认证逻辑，要求用户登录等
        return Mono.just("authorize");  // 这是你的授权页面的视图名
    }

    // 处理用户同意授权的请求
    @PostMapping("/oauth2/authorize")
    public Mono<String> processAuthorization(@ModelAttribute AuthorizationForm form) {

        // 从 AuthorizationForm 中获取 approve 参数，判断用户是否同意授权
        boolean approve = form.isApprove();
        String client_id = form.getClient_id();
        String redirect_uri = form.getRedirect_uri();
        String response_type = form.getResponse_type();
        String scope = form.getScope();
        String state = form.getState();

        // 打印参数信息（可选）
        System.out.printf("client_id=%s, redirect_uri=%s, response_type=%s, scope=%s, state=%s%n",
                client_id, redirect_uri, response_type, scope, state);

        if (approve) {
            // 用户同意授权，生成一个授权码
            String authorizationCode = "generated-authorization-code";  // 这里应该根据实际逻辑生成授权码

            // 将授权码重定向回客户端的 redirect_uri，附带授权码和 state 参数
            String redirectUrl = redirect_uri + "?code=" + authorizationCode + "&state=" + state;

            // 返回重定向到客户端的 URL
            return Mono.just("redirect:" + redirectUrl);
        } else {
            // 用户拒绝授权，重定向到一个错误页面或返回授权失败
            return Mono.just("redirect:/error");  // 你可以处理错误页面
        }
    }
}
