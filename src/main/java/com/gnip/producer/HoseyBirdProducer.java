package com.gnip.producer;

import com.gnip.Environment;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
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

@Singleton
public class HoseyBirdProducer implements MessageProducer {
    private final static Logger logger = Logger.getLogger(HoseyBirdProducer.class);

    private Environment environment;
    private BlockingQueue<String> msgQueue;
    private BlockingQueue<Event> eventQueue;
    private Client hosebirdClient;

    @Inject
    public HoseyBirdProducer(Environment environment, QueueFactory queueFactory) {
        this.environment = environment;
        this.msgQueue = msgQueue;
        /** Set up your blocking queues: Be sure to size these properly based on expected TPS of your stream */
        eventQueue = new LinkedBlockingQueue<Event>(1000);
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
                .processor(new StringDelimitedProcessor(queueFactory.getMsgQueue()))
                .eventMessageQueue(eventQueue);                          // optional: use this if you want to process client events

        hosebirdClient = builder.build();

    }

    @Override
    public boolean isDone() {
        return hosebirdClient.isDone();
    }

    @Override
    public void start() {

        hosebirdClient.connect();
    }

    @Override
    public void stop() {
        hosebirdClient.stop();
    }

}
