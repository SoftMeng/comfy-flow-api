package com.mexx.comfy.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.quarkus.jackson.ObjectMapperCustomizer;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Produces;
import java.time.LocalDateTime;

/**
 * Description:替换掉默认的ObjectMapper.  .<br>
 * <p>Created Time: 2022/8/24 13:44 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public class CustomObjectMapper {

    @Singleton
    @Produces
    ObjectMapper objectMapper(Instance<ObjectMapperCustomizer> customizers) {
        // Custom `ObjectMapper`
        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        // long -> String
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        // Long -> String
        module.addSerializer(Long.class, ToStringSerializer.instance);
        // custom types
        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        mapper.registerModule(module);
        // Apply all ObjectMapperCustomizer beans (incl. Quarkus)
        for (ObjectMapperCustomizer customizer : customizers) {
            customizer.customize(mapper);
        }
        return mapper;
    }
}
