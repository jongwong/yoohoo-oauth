package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.MiniAppMenuVO;
import cn.jongwong.server.service.MiniAppMenuService;
import cn.jongwong.server.util.response.Response;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/client/menus")
@RequiredArgsConstructor
@Tag(name = "Menu", description = "菜单管理 API")
public class ClientMiniAppMenuController {

    private final MiniAppMenuService miniAppMenuService;

    // 获取所有菜单项
    @GetMapping("/category")
    public Mono<Response<List<MiniAppMenuVO>>> getAllMenus() {
        return miniAppMenuService.getAllMenus().collectList().map(Response::ok);
    }

}
