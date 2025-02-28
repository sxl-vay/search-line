package top.boking.escore.domain.request;

import lombok.Data;
import top.boking.escore.domain.enums.SearchType;

import java.io.Serializable;

@Data
public class SearchRequest implements Serializable {
    private SearchType type;
    private String searchText;
    private Integer pageSize;
    private Integer pageNum;

}
