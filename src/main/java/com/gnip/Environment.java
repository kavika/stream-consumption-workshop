package com.gnip;

import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Singleton
public class Environment {
    private static final Logger logger = Logger.getLogger(Environment.class);

    public static final String LANGUAGES_KEY = "LANGUAGES";

    private final Properties props;

    public Environment() {
        props = new Properties();
        InputStream properties = Environment.class.getClassLoader().getResourceAsStream("config.properties");
        try {
            props.load(properties);
        } catch (IOException e) {
            logger.error("Could not load properties, streams cannot be configured");
            throw new RuntimeException("Could not load properties");
        }
    }

    public String consumerKey() {
        return String.valueOf(props.get("consumer.key"));
    }

    public String consumerSecret() {
        return String.valueOf(props.get("consumer.secret"));
    }

    public String tokenKey() {
        return String.valueOf(props.get("token.key"));
    }

    public String tokenSecret() {
        return String.valueOf(props.get("token.secret"));
    }

    public String redisHost() {
        return String.valueOf(props.get("redis.host"));
    }
}
