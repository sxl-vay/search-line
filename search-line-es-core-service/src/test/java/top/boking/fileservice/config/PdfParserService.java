package top.boking.fileservice.config;


import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.pdf.PDFParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class PdfParserService {

    /**
     * 使用 Apache Tika 解析 PDF 文件内容
     * @param filePath PDF 文件路径
     * @return 提取的文本内容
     */
    public static String parsePdf(String filePath) {
        try (InputStream input = new FileInputStream(new File(filePath))) {
            // 1. 创建 Tika 解析器
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            PDFParser pdfParser = new PDFParser();

            // 2. 解析 PDF
            pdfParser.parse(input, handler, metadata, new ParseContext());

            // 3. 返回解析后的文本
            return handler.toString();
        } catch (IOException | SAXException | TikaException e) {
            e.printStackTrace();
            return "PDF 解析失败：" + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String pdfContent = parsePdf("/Users/sxl/Downloads/test2.pdf");
        System.out.println("解析出的 PDF 内容：\n" + pdfContent);
    }
}
