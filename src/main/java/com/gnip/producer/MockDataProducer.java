package com.gnip.producer;

import com.gnip.utilities.JSONUtils;
import com.google.common.util.concurrent.RateLimiter;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.BlockingQueue;

@Singleton
public class MockDataProducer implements MessageProducer {
    private final static Logger logger = Logger.getLogger(MockDataProducer.class);
    boolean keepRunning = true;
    private final RateLimiter rateLimiter;
    private BlockingQueue<String> theQueue;

    @Inject
    public MockDataProducer(QueueFactory queueFactory) {
        this.theQueue = queueFactory.getMsgQueue();
        rateLimiter = RateLimiter.create(100);
    }

    @Override
    public boolean isDone() {
        return !keepRunning;
    }

    @Override
    public void start() {
        while (keepRunning) {
            try {
                String sampleTweet = JSONUtils.getResourceAsString("data/fakeTweet.json");
                theQueue.add(sampleTweet);
                rateLimiter.acquire(1);
            } catch (IOException e) {
                logger.error("Failed to load tweet.", e);
            }
        }
    }

    @Override
    public void stop() {
        keepRunning = false;
    }
}
