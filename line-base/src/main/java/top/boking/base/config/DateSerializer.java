package top.boking.base.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import top.boking.base.util.GlobalDataFormatTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 统一的日期序列化处理器
 */
public class DateSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date date, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(GlobalDataFormatTemplate.DATE_FORMAT);
            gen.writeString(formatter.format(date));
        } else {
            gen.writeNull();
        }
    }
}