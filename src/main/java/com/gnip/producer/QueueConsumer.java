package com.gnip.producer;

import com.codahale.metrics.Counter;
import com.gnip.processor.MessageProcessor;
import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

public class QueueConsumer implements Runnable {
    private final static Logger logger = Logger.getLogger(QueueConsumer.class);

    private LinkedBlockingQueue<String> queue;
    private MessageProducer producer;
    private Counter counter;
    private MessageProcessor messageProcessor;

    public QueueConsumer(LinkedBlockingQueue<String> messageQueue,
                         MessageProducer producer,
                         Counter counter,
                         MessageProcessor messageProcessor) {
        this.queue = messageQueue;
        this.producer = producer;
        this.counter = counter;
        this.messageProcessor = messageProcessor;
    }

    @Override
    public void run() {
        while (!producer.isDone()) {
            try {
                String message = queue.take();
                counter.inc();
                messageProcessor.processLine(message);
            } catch (Throwable t) {
                logger.error("Failure to consume message", t);
            }
        }
    }
}
