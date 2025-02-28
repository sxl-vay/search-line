package top.boking.escore.domain.enums;

import lombok.Getter;

/**
 * 搜索类型枚举
 */
@Getter
public enum SearchType {
    
    post("postList", "文章搜索"),
    picture("","");

    private final String code;
    private final String desc;

    SearchType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static SearchType getByCode(String code) {
        for (SearchType type : values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }
}