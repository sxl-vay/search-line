package top.boking.escore.parser;

import java.io.InputStream;

/**
 * 文档解析门面接口
 */
public interface DocumentParserFacade {
    /**
     * 解析文档内容
     *
     * @param input    文档输入流
     * @param fileType 文件类型
     * @return 解析后的文本内容
     * @throws Exception 解析异常
     */
    String parseDocument(InputStream input, String fileType) throws Exception;
}