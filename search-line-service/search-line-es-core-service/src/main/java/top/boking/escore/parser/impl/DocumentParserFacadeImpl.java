package top.boking.escore.parser.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import top.boking.escore.parser.AbstractDocumentParser;
import top.boking.escore.parser.DocumentParserFacade;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DocumentParserFacadeImpl implements DocumentParserFacade {

    private final Map<String, AbstractDocumentParser> parserMap = new ConcurrentHashMap<>();

    public DocumentParserFacadeImpl(ApplicationContext applicationContext) {
        // 获取所有AbstractDocumentParser的实现类
        Map<String, AbstractDocumentParser> parsers = applicationContext.getBeansOfType(AbstractDocumentParser.class);

        // 遍历所有解析器实现，根据类名推断支持的文件类型
        parsers.forEach((beanName, parser) -> {

            Set<String> types = parser.canParseFileType();

            for (String type : types) {
                if (parserMap.containsKey(type)) {
                    AbstractDocumentParser parser1 = parserMap.get(type);
                    //将两个重复的解析器名称抛出异常
                    throw new UnsupportedOperationException("重复的解析器名称: " + parser1.getClass().getName() + ", " + parser.getClass().getName());
                }
                parserMap.put(type, parser);
            }
            // 可以继续添加其他文件类型的映射规则
        });
    }

    @Override
    public String parseDocument(InputStream input, String fileType) throws Exception {
        AbstractDocumentParser parser = parserMap.get(fileType.toLowerCase());
        if (parser == null) {
            throw new UnsupportedOperationException("不支持的文件类型: " + fileType);
        }
        return parser.parse(input);
    }
}