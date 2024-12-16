package cn.jongwong.server.util.response;

import lombok.Getter;
import lombok.Setter;
import reactor.core.publisher.Mono;

import java.util.List;


@Getter
@Setter
public class PageResponse<T> extends Response<List<T>> {
    private static final long serialVersionUID = 1L;  // 添加 serialVersionUID

    private int code;
    private String message;
    private List<T> data;
    private Long total;

    public PageResponse(int code, String message, List<T> data, Long total) {
        super(code, message, data);
        this.code = code;
        this.message = message;
        this.data = data;
        this.total = total;
    }

    public static <T> Mono<PageResponse<T>> reactivePageSuccess(Mono<Page<T>> pageMono) {
        return pageMono.map(page -> {
            // 构建 ResponseResult，将 Page 的数据填充到 ResponseResult 中
            return new PageResponse<T>(ResponseErrorCodeEnum.SUCCESS.getCode(), "", page.getData(), page.getTotal()); // 返回完整的 ResponseResult
        });
    }


}
