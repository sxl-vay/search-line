package top.boking.escore.parser.impl;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.text.TextContentRenderer;
import org.springframework.stereotype.Component;
import top.boking.escore.parser.AbstractDocumentParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Set;

@Component
public class MarkdownDocumentParser extends AbstractDocumentParser {
    @Override
    protected String doParseDocument(InputStream input) throws Exception {
        // 创建Markdown解析器
        Parser parser = Parser.builder().build();

        // 使用InputStreamReader读取输入流，并指定UTF-8编码
        try (InputStreamReader reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
            // 解析Markdown内容
            Node document = parser.parseReader(reader);

            // 创建文本渲染器并渲染为纯文本
            TextContentRenderer renderer = TextContentRenderer.builder().build();
            return renderer.render(document);
        }
    }

    @Override
    public Set<String> canParseFileType() {
        return Set.of("md");
    }
}
