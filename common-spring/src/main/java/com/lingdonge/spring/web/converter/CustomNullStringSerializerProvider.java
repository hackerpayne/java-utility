package com.lingdonge.spring.web.converter;

import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * 自定义String序列化工具，使null转成""输出。
 */
public class CustomNullStringSerializerProvider extends DefaultSerializerProvider {

    private static final long serialVersionUID = 1L;

    public CustomNullStringSerializerProvider() {
        super();
    }

    public CustomNullStringSerializerProvider(CustomNullStringSerializerProvider provider,
                                              SerializationConfig config, SerializerFactory jsf) {
        super(provider, config, jsf);
    }

    @Override
    public CustomNullStringSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
        return new CustomNullStringSerializerProvider(this, config, jsf);
    }

    /**
     * 这是最关键的部分，用于处理仅对String类型数据生效的null。
     *
     * @param property
     * @return
     * @throws JsonMappingException
     */
    @Override
    public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
        if (property.getType().getRawClass().equals(String.class) || property.getType().getRawClass().equals(Date.class)) {
            return EmptyStringSerializer.INSTANCE;
        } else if (property.getType().getRawClass().equals(Integer.class)) {
            return EmptyIntegerSerializer.INSTANCE;
        } else if (property.getType().getRawClass().equals(Double.class)) {
            return EmptyDoubleSerializer.INSTANCE;
        } else if (property.getType().getRawClass().equals(Boolean.class)) {
            return EmptyBooleanSerializer.INSTANCE;
        } else if (property.getType().getRawClass().equals(List.class) || property.getType().getRawClass().equals(Set.class)) {
            return EmptyArraySerializer.INSTANCE;
        } else {
            return super.findNullValueSerializer(property);
        }
    }
}

