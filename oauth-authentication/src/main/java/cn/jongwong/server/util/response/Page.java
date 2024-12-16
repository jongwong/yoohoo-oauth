package cn.jongwong.server.util.response;

import lombok.Data;

import java.util.List;

@Data
public class Page<T> {
    private List<T> data;
    private long total;
    private int page;
    private int size;

    public Page(List<T> data, long total, int page, int size) {
        this.data = data;
        this.total = total;
        this.page = page;
        this.size = size;
    }

    // Getters and setters
}
