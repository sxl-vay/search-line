package top.boking.comment.domain.request;

import jakarta.validation.constraints.Min;
import lombok.Data;

/**
 * 分页参数
 */
@Data
public class PageParam {
    /**
     * 页码，从1开始
     */
    @Min(value = 1, message = "页码必须大于0")
    private Integer pageNum = 1;

    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于0")
    private Integer pageSize = 10;
}