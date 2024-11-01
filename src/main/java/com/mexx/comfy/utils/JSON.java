package com.mexx.comfy.utils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Description: 序列化 .<br>
 * <p>Created Time: 2023/12/14 17:23 </p>
 *
 * @author <a href="mail to: mengxiangyuancc@gmail.com" rel="nofollow">孟祥元</a>
 */
public final class JSON {
    private static final Logger logger = LoggerFactory.getLogger(JSON.class);
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.setSerializationInclusion(Include.NON_NULL);
        OBJECT_MAPPER.configure(Feature.ALLOW_SINGLE_QUOTES, true);
        OBJECT_MAPPER.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        OBJECT_MAPPER.configure(Feature.ALLOW_NUMERIC_LEADING_ZEROS, true);
        OBJECT_MAPPER.configure(Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);
        OBJECT_MAPPER.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        OBJECT_MAPPER.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"));
        OBJECT_MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CAMEL_CASE);
        OBJECT_MAPPER.setTimeZone(TimeZone.getDefault());
        SimpleModule module = new SimpleModule();
        module.addSerializer(Long.TYPE, ToStringSerializer.instance);
        module.addSerializer(Long.class, ToStringSerializer.instance);
        module.addSerializer(JsonObject.class, new JsonObjectSerializer());
        module.addSerializer(JsonArray.class, new JsonArraySerializer());
        module.addSerializer(LocalDateTime.class, LocalDateTimeSerializer.INSTANCE);
        module.addDeserializer(LocalDateTime.class, LocalDateTimeDeserializer.INSTANCE);
        OBJECT_MAPPER.registerModule(module);
    }

    public JSON() {
    }

    public static JavaType makeJavaType(Class<?> parametrized, Class<?>... parameterClasses) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(parametrized, parameterClasses);
    }

    public static JavaType makeJavaType(Class<?> rawType, JavaType... parameterTypes) {
        return OBJECT_MAPPER.getTypeFactory().constructParametricType(rawType, parameterTypes);
    }

    public static String toString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        } else {
            return value instanceof String ? (String) value : toJSONString(value);
        }
    }

    public static String toJSONString(Object value) {
        if (Objects.isNull(value)) {
            return null;
        } else {
            try {
                return OBJECT_MAPPER.writeValueAsString(value);
            } catch (Exception var2) {
                logger.error(var2.getMessage(), var2);
                return null;
            }
        }
    }

    public static String toPrettyString(Object value) {
        try {
            return OBJECT_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(value);
        } catch (Exception var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static JsonNode fromJavaObject(Object value) {
        JsonNode result = null;
        if (Objects.nonNull(value) && value instanceof String) {
            result = parseObject((String) value);
        } else {
            result = OBJECT_MAPPER.valueToTree(value);
        }

        return result;
    }

    public static JsonNode parseObject(String content) {
        try {
            return OBJECT_MAPPER.readTree(content);
        } catch (Exception var2) {
            logger.error(var2.getMessage(), var2);
            return null;
        }
    }

    public static JsonNode getJsonElement(JsonNode node, String name) {
        return node.get(name);
    }

    public static JsonNode getJsonElement(JsonNode node, int index) {
        return node.get(index);
    }

    public static <T> T toJavaObject(TreeNode node, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.treeToValue(node, clazz);
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T toJavaObject(TreeNode node, JavaType javaType) {
        return OBJECT_MAPPER.convertValue(node, javaType);
    }

    public static <T> T toJavaObject(TreeNode node, TypeReference<T> typeReference) {
        return OBJECT_MAPPER.convertValue(node, typeReference);
    }

    public static <T> T toJavaObject(TreeNode node, Type type) {
        return toJavaObject(node, OBJECT_MAPPER.constructType(type));
    }

    public static <E> List<E> toJavaList(TreeNode node, Class<E> clazz) {
        return (List) toJavaObject(node, makeJavaType(List.class, clazz));
    }

    public static List<Object> toJavaList(TreeNode node) {
        return (List) toJavaObject(node, new TypeReference<List<Object>>() {
        });
    }

    public static <V> Map<String, V> toJavaMap(TreeNode node, Class<V> clazz) {
        return (Map) toJavaObject(node, makeJavaType(Map.class, String.class, clazz));
    }

    public static Map<String, Object> toJavaMap(TreeNode node) {
        return (Map) toJavaObject(node, new TypeReference<Map<String, Object>>() {
        });
    }

    public static <T> T toJavaObject(String content, Class<T> clazz) {
        if (Objects.isNull(content)) {
            return null;
        } else {
            try {
                return OBJECT_MAPPER.readValue(content, clazz);
            } catch (Exception var3) {
                logger.error(var3.getMessage(), var3);
                return null;
            }
        }
    }

    public static <T> T toJavaObject(String content, JavaType javaType) {
        try {
            return OBJECT_MAPPER.readValue(content, javaType);
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T toJavaObject(String content, TypeReference<T> typeReference) {
        try {
            return OBJECT_MAPPER.readValue(content, typeReference);
        } catch (Exception var3) {
            logger.error(var3.getMessage(), var3);
            return null;
        }
    }

    public static <T> T toJavaObject(String content, Type type) {
        return toJavaObject(content, OBJECT_MAPPER.constructType(type));
    }

    public static <E> List<E> toJavaList(String content, Class<E> clazz) {
        return (List) toJavaObject(content, makeJavaType(List.class, clazz));
    }

    public static List<Object> toJavaList(String content) {
        return (List) toJavaObject(content, new TypeReference<List<Object>>() {
        });
    }

    public static <V> Map<String, V> toJavaMap(String content, Class<V> clazz) {
        return (Map) toJavaObject(content, makeJavaType(Map.class, String.class, clazz));
    }

    public static Map<String, Object> toJavaMap(String content) {
        return (Map) toJavaObject(content, new TypeReference<Map<String, Object>>() {
        });
    }
}
