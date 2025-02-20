package top.boking.escore.comsumer;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import top.boking.escore.consts.ESMQConst;
import top.boking.escore.consts.IndexConsts;
import top.boking.escore.domain.entity.KnowledgeBase;
import top.boking.file.consts.MQConst;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.service.SLineFileCoreService;

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

    public FileMessageListener(ElasticsearchClient elasticsearchClient, SLineFileCoreService sLineFileCoreService) {
        this.elasticsearchClient = elasticsearchClient;
        this.sLineFileCoreService = sLineFileCoreService;
    }

    //幂等控制
    @Override
    public void onMessage(SLineFile event) {
        if (sLineFileCoreService.checkFileExist(event.getId())) {
            log.info("文件已存在，跳过处理{}", event);
            return;
        }
        //todo 将文件服务消息中文件内容添加到es中
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
                log.error("批量写入ES出现错误：{}", response.items());
            } else {
                log.info("成功批量写入{}条数据到ES", knowledgeBases.size());
            }
        } catch (IOException e) {
            log.error("ES批量写入异常", e);
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
        BodyContentHandler handler = new BodyContentHandler();
        Metadata metadata = new Metadata();
        PDFParser pdfParser = new PDFParser();

        // 2. 解析 PDF
        pdfParser.parse(input, handler, metadata, new ParseContext());

        // 3. 返回解析后的文本
        return handler.toString();
    }
}


