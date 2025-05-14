package xyz.sadiulhakim.config;

import org.quartz.spi.ThreadPool;

public class QuartzVirtualThreadPool implements ThreadPool {
    @Override
    public boolean runInThread(Runnable runnable) {
        Thread.startVirtualThread(runnable);
        return true;
    }

    @Override
    public int blockForAvailableThreads() {
        // Virtual threads are effectively unlimited
        return Integer.MAX_VALUE;
    }

    @Override
    public void initialize() {
        // No initialization needed for virtual threads
    }

    @Override
    public void shutdown(boolean waitForJobsToComplete) {
        // Nothing to shutdown for virtual threads
    }

    @Override
    public int getPoolSize() {
        // Return a dummy value since virtual threads don't have a fixed pool size
        return Integer.MAX_VALUE;
    }

    @Override
    public void setInstanceId(String schedInstId) {
        // Not needed
    }

    @Override
    public void setInstanceName(String schedName) {
        // Not needed
    }
}
