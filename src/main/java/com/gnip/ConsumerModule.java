package com.gnip;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Slf4jReporter;
import com.gnip.producer.HoseyBirdProducer;
import com.gnip.producer.MessageProducer;
import com.gnip.producer.MockDataProducer;
import com.google.inject.AbstractModule;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class ConsumerModule extends AbstractModule {
    private final MetricRegistry metricRegistry;

    public ConsumerModule() {
        metricRegistry = new MetricRegistry();
        final Slf4jReporter reporter = Slf4jReporter.forRegistry(metricRegistry)
                .outputTo(LoggerFactory.getLogger("com.gnip.metrics"))
                .convertRatesTo(TimeUnit.SECONDS)
                .convertDurationsTo(TimeUnit.MILLISECONDS)
                .build();

        reporter.start(10, TimeUnit.SECONDS);
    }

    @Override
    protected void configure() {
        bind(MetricRegistry.class).toInstance(metricRegistry);
        bind(MessageProducer.class).to(HoseyBirdProducer.class);
//        bind(MessageProducer.class).to(MockDataProducer.class);
    }
}
