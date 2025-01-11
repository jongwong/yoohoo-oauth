package cn.jongwong.server.dto.product;

import lombok.Data;

@Data
public class QueryProductBatchDTO {
    private String[] ids;
}
