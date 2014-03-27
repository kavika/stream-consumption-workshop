package com.gnip;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;
import redis.clients.jedis.Jedis;

import java.util.Map;

public class AnalysisApplication {
    private final static Logger logger = Logger.getLogger(AnalysisApplication.class);
    private final Environment environment;
    private final Jedis jedis;

    public static void main(String[] args) {
        AnalysisApplication analysisApplication = new AnalysisApplication();
        try {
            analysisApplication.runAnalysis();
        } catch (Exception e) {
            logger.error("Unexpected error occured.", e);
        }
    }

    public AnalysisApplication() {
        Injector injector = Guice.createInjector(new ConsumerModule());
        environment = injector.getInstance(Environment.class);
        jedis = new Jedis(environment.redisHost());
        jedis.connect();
    }


    public void runAnalysis() throws Exception {
        Map<String,String> keysAndValues = jedis.hgetAll(Environment.LANGUAGES_KEY);
        for (String language : keysAndValues.keySet()) {
            System.out.println("Language: " + language + " Count: " + keysAndValues.get(language));

        }
    }

}
