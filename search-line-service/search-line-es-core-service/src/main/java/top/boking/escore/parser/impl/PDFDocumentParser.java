package top.boking.escore.parser.impl;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.springframework.stereotype.Component;
import top.boking.escore.parser.AbstractDocumentParser;

import java.io.InputStream;
import java.util.Set;

/**
 * PDF文档解析器
 * 实现桥接模式中的具体实现部分
 */
@Component
public class PDFDocumentParser extends AbstractDocumentParser {

    @Override
    protected String doParseDocument(InputStream input) throws Exception {
        BodyContentHandler handler = new BodyContentHandler(500000);
        Metadata metadata = new Metadata();
        PDFParser pdfParser = new PDFParser();
        pdfParser.parse(input, handler, metadata, new ParseContext());
        return handler.toString();
    }

    @Override
    protected String postProcess(String content) throws Exception {
        // 可以在这里添加PDF特定的后处理逻辑，比如格式化或清理
        return super.postProcess(content);
    }

    @Override
    public Set<String> canParseFileType() {
        return Set.of("pdf");
    }
}