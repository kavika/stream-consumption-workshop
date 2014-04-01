package com.gnip;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.gnip.processor.SimpleParseProcessor;
import com.gnip.producer.MessageProducer;
import com.gnip.producer.QueueConsumer;
import com.gnip.producer.QueueFactory;
import com.gnip.utilities.TaskManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.log4j.Logger;

import java.util.concurrent.LinkedBlockingQueue;

public class StreamConsumptionApplication {
    private final static Logger logger = Logger.getLogger(StreamConsumptionApplication.class);
    private final Environment environment;
    private final TaskManager taskManager;
    private final MetricRegistry metricRegistry;
    private final Counter counter;
    private final MessageProducer producer;
    private final LinkedBlockingQueue<String> msgQueue;

    public static void main(String[] args) {
        StreamConsumptionApplication streamConsumptionApplication = new StreamConsumptionApplication();
        try {
            streamConsumptionApplication.start();
        } catch (Exception e) {
            logger.error("Unexpected error occured.", e);
        }
    }

    public StreamConsumptionApplication() {
        Injector injector = Guice.createInjector(new ConsumerModule());
        environment = injector.getInstance(Environment.class);
        taskManager = injector.getInstance(TaskManager.class);
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        msgQueue = injector.getInstance(QueueFactory.class).getMsgQueue();
        producer = injector.getInstance(MessageProducer.class);

        metricRegistry = injector.getInstance(MetricRegistry.class);

        counter = metricRegistry.counter("Messages Consumed");

    }

    private void setUpMessageProcessors(MessageProducer producer) {
        for (int i = 0; i < 5; i++) {
            taskManager.submit(new QueueConsumer(msgQueue,
                    producer,
                    counter,
                    new SimpleParseProcessor(environment)));
        }
    }

    public void start() throws Exception {
        setUpMessageProcessors(producer);
        // Attempts to establish a connection.
        producer.start();
        //        producer.stop();

    }

}
