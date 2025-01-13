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
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;

@Component
public class SnakeCaseParamWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // 获取查询参数
        MultiValueMap<String, String> queryParams = exchange.getRequest().getQueryParams();
        // 转换 snake_case 为 camelCase
        MultiValueMap<String, String> modifiedParams = new LinkedMultiValueMap<>();
        queryParams.forEach((key, values) -> {
            String camelKey = snakeToCamel(key);
            values.forEach(value -> {

                // 校验是否为日期时间参数
                if (isDateTimeParameter(key, value, values)) {
                    try {
                        // 转换为 LocalDateTime 格式
                        LocalDateTime dateTimeValue = convertTimestampToLocalDateTime(value);
                        modifiedParams.add(camelKey, dateTimeValue.toString());
                    } catch (DateTimeParseException e) {
                        throw new IllegalArgumentException("Invalid date format for parameter: " + key, e);
                    }
                } else {
                    // 非日期时间参数直接加入
                    modifiedParams.add(camelKey, value);
                }
            });
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

    private boolean isDateTimeParameter(String camelKey, String value, List<String> values) {
        // 检查参数名是否满足条件：以 time_ 开头或以 _at 结尾
        boolean isKeyMatch = camelKey.startsWith("time_") || camelKey.endsWith("_at");
        // 检查值是否符合时间戳格式（13 位数字）
        boolean isValueValid = isValidTimestamp(value);

        // 检查 values 是否只有一个元素
        boolean isSingleValue = values.size() == 1;

        // 参数名符合条件，值符合条件，并且 values 长度为 1 才返回 true
        return isKeyMatch && isValueValid && isSingleValue;
    }

    private boolean isValidTimestamp(String value) {
        // 检查值是否为 13 位时间戳（毫秒级）
        return value.matches("\\d{13}");
    }

    public LocalDateTime convertTimestampToLocalDateTime(String str) {
        // 1. 将时间戳字符串转换为 long 类型
        long timestamp = Long.parseLong(str);

        // 2. 将时间戳转换为 Instant
        Instant instant = Instant.ofEpochMilli(timestamp);

        // 3. 转换为 LocalDateTime，根据系统时区
        LocalDateTime localDateTime = instant.atZone(ZoneId.systemDefault()).toLocalDateTime();

        return localDateTime;
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
