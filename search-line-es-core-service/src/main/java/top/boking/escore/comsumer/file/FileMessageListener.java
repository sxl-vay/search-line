package top.boking.escore.comsumer.file;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import top.boking.escore.consts.ESMQConst;
import top.boking.escore.consts.IndexConsts;
import top.boking.escore.domain.entity.FileTransferRecord;
import top.boking.escore.domain.entity.KnowledgeBase;
import top.boking.escore.infrastructure.mapper.FileTransferRecordMapper;
import top.boking.file.consts.MQConst;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.service.SLineFileCoreService;
import top.boking.lock.DistributeLock;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RocketMQMessageListener(
        consumerGroup = ESMQConst.COMSUMERGROUP
        , topic = (MQConst.FILE_TRANSACTION_TOPIC)
        , maxReconsumeTimes = 2
)
@Slf4j
public class FileMessageListener implements RocketMQListener<SLineFile> {

    private final ElasticsearchClient elasticsearchClient;

    private final SLineFileCoreService sLineFileCoreService;

    private final FileTransferRecordMapper fileTransferRecordMapper;

    public FileMessageListener(ElasticsearchClient elasticsearchClient, SLineFileCoreService sLineFileCoreService, FileTransferRecordMapper fileTransferRecordMapper) {
        this.elasticsearchClient = elasticsearchClient;
        this.sLineFileCoreService = sLineFileCoreService;
        this.fileTransferRecordMapper = fileTransferRecordMapper;
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

        List<KnowledgeBase> knowledgeBases = Arrays.asList(event)
                .stream()
                .map(sLineFile -> {
                    KnowledgeBase knowledgeBase = KnowledgeBase.convert(sLineFile);
                    String path = System.getProperty("user.dir");
                    String dirPath = path + File.separator + "filedir";
                    File localFile = new File(dirPath + File.separator + event.getStoreFileName());
                    try {
                        String parsePdf = parsePdf(localFile);
                        knowledgeBase.setFileContent(parsePdf);
                        return knowledgeBase;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
        BulkRequest.Builder builder = new BulkRequest.Builder();

        // 构建批量请求
        for (KnowledgeBase kb : knowledgeBases) {
            builder.operations(op -> op.index(idx -> idx.index(IndexConsts.FILE_KNOWLEDGE_INDEX).id(String.valueOf(System.currentTimeMillis())).document(kb)));
        }

        try {
            // 执行批量写入
            BulkResponse response = elasticsearchClient.bulk(builder.build());
            if (response.errors()) {
                throw new RuntimeException("ES批量写入异常");
            } else {
                log.info("成功批量写入{}条数据到ES", knowledgeBases.size());
            }
        } catch (IOException e) {
            throw new RuntimeException("ES批量写入时IO异常:",e);
        }
    }

    /**
     * 使用 Apache Tika 解析 PDF 文件内容
     * @param filePath PDF 文件路径
     * @return 提取的文本内容
     */
    public static String parsePdf(File filePath) throws Exception{
        InputStream input = new FileInputStream(filePath);
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


