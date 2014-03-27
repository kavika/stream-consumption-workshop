package com.gnip.utilities;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Singleton
public class TaskManager {
    private final ExecutorService executor;
    private int streamDefaultWorkers = Runtime.getRuntime().availableProcessors();

    @Inject
    public TaskManager() {
        executor = Executors.newFixedThreadPool(streamDefaultWorkers);
    }

    public void submit(Runnable runnable) {
        executor.submit(runnable);
    }
}
