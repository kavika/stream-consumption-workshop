package com.gnip.producer;

public interface MessageProducer {
    boolean isDone();
    void start();
    void stop();
}
