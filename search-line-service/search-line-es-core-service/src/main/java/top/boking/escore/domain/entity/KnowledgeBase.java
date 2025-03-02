package top.boking.escore.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import top.boking.escore.consts.IndexConsts;
import top.boking.file.domain.entity.SLineFile;

import java.util.Date;

@Data
@Document(indexName = IndexConsts.FILE_KNOWLEDGE_INDEX)
public class KnowledgeBase {
    @Id
    private String id;
    @Field(type = FieldType.Text, analyzer = "ik_smart_pinyin")
    private String fileName;

    @Field(type = FieldType.Text, analyzer = "ik_smart_pinyin")
    private String fileContent;

    @Field(type = FieldType.Keyword)
    private String fileType;

    @Field(type = FieldType.Date)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date uploadTime;

    @MultiField(
        mainField = @Field(type = FieldType.Text, analyzer = "ik_smart"),
        otherFields = {
            @InnerField(suffix = "keyword", type = FieldType.Keyword)
        }
    )
    private String author;

    @Field(type = FieldType.Binary)
    private String attachment;

    @Field(type = FieldType.Object)
    private MetaData metadata;

    @Data
    public static class MetaData {
        @Field(type = FieldType.Keyword)
        private String creator;

        @Field(type = FieldType.Date)
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        private Date createDate;

        @Field(type = FieldType.Integer)
        private Integer pages;
    }

    public static KnowledgeBase convert(SLineFile file) {
        KnowledgeBase knowledgeBase = new KnowledgeBase();
        knowledgeBase.setId(file.getId().toString());
        knowledgeBase.setFileName(file.getName());
        knowledgeBase.setFileType(file.getSuffix());
        knowledgeBase.setUploadTime(file.getGmtCreate());
        knowledgeBase.setAuthor(file.getOwner().toString());
        return knowledgeBase;
    }
}