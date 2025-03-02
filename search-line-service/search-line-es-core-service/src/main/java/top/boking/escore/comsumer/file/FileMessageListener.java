package top.boking.escore.comsumer.file;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.minio.GetObjectResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.data.elasticsearch.core.RefreshPolicy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.xml.sax.SAXException;
import top.boking.escore.consts.ESMQConst;
import top.boking.escore.domain.entity.FileTransferRecord;
import top.boking.escore.domain.entity.KnowledgeBase;
import top.boking.escore.infrastructure.mapper.FileTransferRecordMapper;
import top.boking.escore.repository.KnowledgeBaseRepository;
import top.boking.file.consts.MQConst;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.utils.MinioUtils;
import top.boking.lock.DistributeLock;

import java.io.IOException;
import java.io.InputStream;

@Component
@RocketMQMessageListener(
        consumerGroup = ESMQConst.FILE_SYNC_COMSUMERGROUP
        , topic = (MQConst.FILE_TRANSACTION_TOPIC)
        , maxReconsumeTimes = 1
)
@Slf4j
public class FileMessageListener implements RocketMQListener<SLineFile> {

    private final FileTransferRecordMapper fileTransferRecordMapper;

    private final MinioUtils minioUtils;

    private final KnowledgeBaseRepository knowledgeBaseRepository;

    public FileMessageListener(FileTransferRecordMapper fileTransferRecordMapper, MinioUtils minioUtils, KnowledgeBaseRepository knowledgeBaseRepository) {
        this.fileTransferRecordMapper = fileTransferRecordMapper;
        this.minioUtils = minioUtils;
        this.knowledgeBaseRepository = knowledgeBaseRepository;
    }


    /**
     *
     * 1. 加锁 文件id的分布式锁
     * 2. 判定 校验文件是否已经插入
     * 3. 插入 es
     * 分布式场景下防止重复消费问题
     * 同一个mq消息可能投递到两个节点上去
     * @param event
     */
    @Override
    @Transactional(rollbackFor = Exception.class )
    @DistributeLock(scene = "file_transfer_record",key = "shxl")
    public void onMessage(SLineFile event) {
        QueryWrapper<FileTransferRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("file_id", event.getId());
        Long l = fileTransferRecordMapper.selectCount(queryWrapper);
        if (l > 0) {
            log.info("文件已存在，跳过处理{}", event);
            return;
        }
        fileTransferRecordMapper.insert(FileTransferRecord.create(event.getId()));
        log.info("es:接收到文件服务消息：{}", event);

        try {
            GetObjectResponse file = minioUtils.getFile(event.getStoreFileName());
            //将GetObjectResponse 转换为InputStream
            String content = parsePdf(file);
            KnowledgeBase knowledgeBase = KnowledgeBase.convert(event);
            knowledgeBase.setFileContent(content);
            // 执行写入
            /*CreateRequest<KnowledgeBase> createRequest = new CreateRequest.Builder<KnowledgeBase>()
                    .id(knowledgeBase.getId())
                    .document(knowledgeBase)
                    .build();
            CreateResponse createResponse = elasticsearchClient.create(createRequest);*/
            KnowledgeBase save = knowledgeBaseRepository.save(knowledgeBase, RefreshPolicy.IMMEDIATE);
            if (log.isDebugEnabled()) {
                log.debug("ES写入成功:{}", save);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 使用 Apache Tika 解析 PDF 文件内容
     * @param input PDF 文件路径
     * @return 提取的文本内容
     */
    public static String parsePdf(InputStream input) throws TikaException, IOException, SAXException {
        // 1. 创建 Tika 解析器
        BodyContentHandler handler = new BodyContentHandler(500000);
        Metadata metadata = new Metadata();
        PDFParser pdfParser = new PDFParser();
        // 2. 解析 PDF
        pdfParser.parse(input, handler, metadata, new ParseContext());
        // 3. 返回解析后的文本
        return handler.toString();
    }
}


