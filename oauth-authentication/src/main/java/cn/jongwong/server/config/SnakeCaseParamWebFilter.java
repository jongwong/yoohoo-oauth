package cn.jongwong.server.config;

import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Map;

@Component
public class SnakeCaseParamWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取查询参数
        Map<String, String> queryParams = exchange.getRequest().getQueryParams().toSingleValueMap();
        // 转换 snake_case 为 camelCase
        MultiValueMap<String, String> modifiedParams = new LinkedMultiValueMap<>();
        queryParams.forEach((key, value) -> {
            // 转换参数名为 camelCase，并保留对应的值
            modifiedParams.add(snakeToCamel(key), value);
        });

        // 使用 UriComponentsBuilder 创建新的 URI，并替换查询参数
        String newUri = UriComponentsBuilder.fromUri(exchange.getRequest().getURI())
                .replaceQueryParams(modifiedParams)
                .toUriString();

        // 创建新的请求，替换为转换后的 URI
        ServerWebExchange modifiedExchange = exchange.mutate()
                .request(r -> r.uri(URI.create(newUri)))  // 设置新的 URI
                .build();

        return chain.filter(modifiedExchange);  // 继续过滤链
    }

    // 将 snake_case 转换为 camelCase
    private String snakeToCamel(String snakeCase) {
        StringBuilder result = new StringBuilder();
        boolean toUpperCase = false;

        for (char c : snakeCase.toCharArray()) {
            if (c == '_') {
                toUpperCase = true;
            } else {
                if (toUpperCase) {
                    result.append(Character.toUpperCase(c));
                    toUpperCase = false;
                } else {
                    result.append(Character.toLowerCase(c));
                }
            }
        }
        return result.toString();
    }
}
