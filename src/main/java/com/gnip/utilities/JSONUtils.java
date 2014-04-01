package com.gnip.utilities;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class JSONUtils {
    static final ThreadLocal<ObjectMapper> objectMapperThreadLocal = new ThreadLocal<ObjectMapper>() {
        @Override
        protected ObjectMapper initialValue() {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper;
        }
    };

    public static ObjectMapper getObjectMapper() {
        return objectMapperThreadLocal.get();
    }

    public static JsonNode parseTree(String json) throws IOException {
        if (json == null) {
            throw new IllegalArgumentException("Input string cannot be null");
        } else {
            return getObjectMapper().readTree(json);
        }
    }

    public static String getResourceAsString(String resourcePath) throws IOException {
        return IOUtils.toString(getResourceAsStream(resourcePath));
    }

    public static InputStream getResourceAsStream(String resource) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(resource);
    }

}
