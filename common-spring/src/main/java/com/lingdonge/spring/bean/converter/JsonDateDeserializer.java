package com.lingdonge.spring.bean.converter;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.lindonge.core.dates.DatePattern;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.boot.jackson.JsonComponent;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

/**
 * springboot中默认接收的日期自动转换不支持yyyy-MM-dd HH:mm:ss格式
 */
@JsonComponent
public class JsonDateDeserializer extends JsonDeserializer<Date> {
//    private String[] patterns = {"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"};

    @Override
    public Date deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        String dateAsString = jsonParser.getText();
        Date parseDate = null;
        try {
            parseDate = DateUtils.parseDate(dateAsString, DatePattern.NORMAL_PATTERS);
        } catch (ParseException e) {
            throw new IllegalArgumentException(e.getCause());
        }
        return parseDate;
    }
}