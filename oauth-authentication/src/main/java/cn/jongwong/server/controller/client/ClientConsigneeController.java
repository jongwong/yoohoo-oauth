package cn.jongwong.server.controller.client;

import cn.jongwong.server.entity.ConsigneeVO;
import cn.jongwong.server.repository.ConsigneeRepository;
import cn.jongwong.server.service.UserService;
import cn.jongwong.server.util.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;


@RestController
@RequestMapping("/client/consignee")
public class ClientConsigneeController {


    @Autowired
    private ConsigneeRepository consigneeRepository;


    @Autowired
    private UserService userService;

    // 获取最近的配送点
    @GetMapping()
    public Mono<Response<List<ConsigneeVO>>> getAllConsignee() {

        return userService.getCurrentUser().flatMap(u -> {
            ConsigneeVO data = new ConsigneeVO();
            data.setUserId(u.getId());
            Example<ConsigneeVO> example = Example.of(data, ExampleMatcher.matching()
                    .withMatcher("user_id", ExampleMatcher.GenericPropertyMatcher.of(ExampleMatcher.StringMatcher.EXACT)) // 精确匹配
                    .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING) // 对其他字段进行模糊匹配
                    .withIgnoreCase()); // 忽略大小写
            return Mono.just(example);
        }).flatMap((example) -> consigneeRepository.findAll(example)
                .collectList() // 将 Flux 转换为 List
                .map(Response::ok));


    }
}
