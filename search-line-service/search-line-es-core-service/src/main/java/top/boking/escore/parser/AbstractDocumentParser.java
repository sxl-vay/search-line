package top.boking.escore.parser;

import java.io.InputStream;
import java.util.Set;

/**
 * 抽象文档解析器
 * 实现桥接模式中的抽象部分
 */
public abstract class AbstractDocumentParser {

    /**
     * 模板方法，定义文档解析的基本流程
     *
     * @param input 文档输入流
     * @return 解析后的文本内容
     * @throws Exception 解析异常
     */
    public final String parse(InputStream input) throws Exception {
        // 前置处理
        preProcess(input);

        // 执行具体的解析
        String content = doParseDocument(input);

        // 后置处理
        return postProcess(content);
    }

    /**
     * 解析前的预处理操作
     *
     * @param input 输入流
     * @throws Exception 处理异常
     */
    protected void preProcess(InputStream input) throws Exception {
        // 默认实现为空，子类可以根据需要覆盖
    }

    /**
     * 具体的文档解析实现
     *
     * @param input 输入流
     * @return 解析后的文本内容
     * @throws Exception 解析异常
     */
    protected abstract String doParseDocument(InputStream input) throws Exception;

    /**
     * 解析后的后处理操作
     *
     * @param content 解析后的内容
     * @return 处理后的内容
     * @throws Exception 处理异常
     */
    protected String postProcess(String content) throws Exception {
        // 默认直接返回解析结果，子类可以根据需要覆盖
        return content;
    }

    public abstract Set<String> canParseFileType();

}