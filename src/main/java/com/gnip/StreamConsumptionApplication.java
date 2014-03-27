package com.gnip;

import com.codahale.metrics.Counter;
import com.codahale.metrics.MetricRegistry;
import com.gnip.processor.MessageProcessor;
import com.gnip.processor.SimpleParseProcessor;
import com.gnip.utilities.TaskManager;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.endpoint.StreamingEndpoint;
import com.twitter.hbc.core.event.Event;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class StreamConsumptionApplication {
    private final static Logger logger = Logger.getLogger(StreamConsumptionApplication.class);
    private final Environment environment;
    private final BlockingQueue<String> msgQueue;
    private final BlockingQueue<Event> eventQueue;
    private final TaskManager taskManager;
    private final MetricRegistry metricRegistry;
    private final Counter counter;

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
        metricRegistry = injector.getInstance(MetricRegistry.class);

        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        msgQueue = new LinkedBlockingQueue<String>(100000);
        eventQueue = new LinkedBlockingQueue<Event>(1000);
        counter = metricRegistry.counter("Messages Consumed");
    }

    private void setUpMessageProcessors(Client hosebirdClient) {

        for (int i = 0; i < 5; i++) {
            taskManager.submit(new QueueConsumer(hosebirdClient, counter, new SimpleParseProcessor(environment)));
        }
    }

    public void start() throws Exception {
        /** Declare the host you want to connect to, the endpoint, and authentication (basic auth or oauth) */
        Hosts hosebirdHosts = new HttpHosts(Constants.STREAM_HOST);
        StreamingEndpoint endpoint = new StatusesSampleEndpoint();
        // Optional: set up some followings and track terms

        // These secrets should be read from a config file
        Authentication hosebirdAuth = new OAuth1(
                environment.consumerKey(),
                environment.consumerSecret(),
                environment.tokenKey(),
                environment.tokenSecret());

        ClientBuilder builder = new ClientBuilder()
                .name("Hosebird-Client-01")                              // optional: mainly for the logs
                .hosts(hosebirdHosts)
                .authentication(hosebirdAuth)
                .endpoint(endpoint)
                .processor(new StringDelimitedProcessor(msgQueue))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        Client hosebirdClient = builder.build();

        setUpMessageProcessors(hosebirdClient);

        // Attempts to establish a connection.
        hosebirdClient.connect();

//        hosebirdClient.stop();

        // Print some stats
        System.out.printf("The client read %d messages!\n", hosebirdClient.getStatsTracker().getNumMessages());
    }

    public class QueueConsumer implements Runnable {
        private Client hosebirdClient;
        private Counter counter;
        private MessageProcessor messageProcessor;

        public QueueConsumer(Client hosebirdClient, Counter counter, MessageProcessor messageProcessor) {
            this.hosebirdClient = hosebirdClient;
            this.counter = counter;
            this.messageProcessor = messageProcessor;
        }

        @Override
        public void run() {
            while (!hosebirdClient.isDone()) {
                try {
                    String message = msgQueue.take();
                    counter.inc();
                    messageProcessor.processLine(message);
                } catch (InterruptedException e) {
                    logger.error("Failed to take", e);
                }
            }
        }
    }

}
