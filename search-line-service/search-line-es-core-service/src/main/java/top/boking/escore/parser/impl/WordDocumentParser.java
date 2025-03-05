package top.boking.escore.parser.impl;

import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Component;
import top.boking.escore.parser.AbstractDocumentParser;

import java.io.InputStream;
import java.util.Set;

/**
 * Word文档解析器
 * 实现桥接模式中的具体实现部分
 */
@Component
public class WordDocumentParser extends AbstractDocumentParser {

    @Override
    protected String doParseDocument(InputStream input) throws Exception {
        try (XWPFDocument document = new XWPFDocument(input)) {
            XWPFWordExtractor extractor = new XWPFWordExtractor(document);
            return extractor.getText();
        }
    }

    @Override
    protected String postProcess(String content) throws Exception {
        // 可以在这里添加Word文档特定的后处理逻辑，比如清理特殊格式字符
        return super.postProcess(content);
    }

    @Override
    public Set<String> canParseFileType() {
        return Set.of("doc", "docx");
    }
}