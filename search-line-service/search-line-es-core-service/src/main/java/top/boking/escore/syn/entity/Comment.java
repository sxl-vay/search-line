package top.boking.escore.syn.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

@Data
@Document(indexName = "comment")
public class Comment implements PushBashEntity {
    @Id
    private Long id;
    private String module;
    private Long targetId;
    private Long commentor;
    private Date addTime;
    private String backupContent;
    private Long blog;
    private Date dayWeibo;
    private Long parent;
    private Long transmitCount;
    private Long commentCount;
    private String tenantKey;
    private String privy;
    private String longitude;
    private String latitude;
    private String address;
    @Field(type = FieldType.Text, analyzer = "ik_smart_pinyin")
    private String content;
    private String client;
    private String visittype;
    private String targetname;
    private String subtargetId;
    private String commentType;
    private String stepName;
    private String external;
    private Long rootId;
    private Integer isInclude;
    private Integer flowType;
    private Long secondid;
    private Integer deleteType;
    private Date updateTime;
    private Long creator;
    private Date createTime;
    private String customFlag;
}