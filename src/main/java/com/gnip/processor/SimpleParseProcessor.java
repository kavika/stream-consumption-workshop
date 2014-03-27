package com.gnip.processor;

import com.fasterxml.jackson.databind.JsonNode;
import com.gnip.Environment;
import com.gnip.utilities.JSONUtils;
import com.google.inject.Inject;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.io.IOException;

public class SimpleParseProcessor implements MessageProcessor {

    private Jedis jedis;

    @Inject
    public SimpleParseProcessor(Environment environment) {
        this.jedis = new Jedis(environment.redisHost());
        this.jedis.connect();
    }

    private final static Logger logger = Logger.getLogger(SimpleParseProcessor.class);

    public void processLine(String aMessage) {
        try {
            JsonNode jsonNode = JSONUtils.parseTree(aMessage);
            if (jsonNode.has("id") && jsonNode.has("text") && jsonNode.has("lang")) {
                String lang = jsonNode.get("lang").asText();
                if (StringUtils.isNotEmpty(lang)) {
                    jedis.hincrBy(Environment.LANGUAGES_KEY, lang, 1);
                }
//                System.out.println(aMessage);
            }
        } catch (IOException e) {
            logger.error("Parse failure for " + aMessage, e);
        }
    }
}
