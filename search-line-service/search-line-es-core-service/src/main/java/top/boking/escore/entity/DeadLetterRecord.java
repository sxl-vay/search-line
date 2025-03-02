package top.boking.escore.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

@TableName("dead_letter_record")
@Data
public class DeadLetterRecord {
    @Id
    private Long id;
    
    private String messageId;
    
    private String messageBody;
    
    private String topic;
    
    private Integer retryTimes;
    
    private String errorMessage;
    
    private LocalDateTime createTime;

}