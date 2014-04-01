package com.gnip.producer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.LinkedBlockingQueue;

@Singleton
public class QueueFactory {
    private final LinkedBlockingQueue<String> msgQueue;

    @Inject
    public QueueFactory() {
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        msgQueue = new LinkedBlockingQueue<String>(100000);
    }

    public LinkedBlockingQueue<String> getMsgQueue() {
        return msgQueue;
    }
}

