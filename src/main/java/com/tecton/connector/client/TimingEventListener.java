package com.tecton.connector.client;

import okhttp3.Call;
import okhttp3.EventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * An OkHttp EventListener that logs the duration of HTTP calls.
 */
public class TimingEventListener extends EventListener {

    private static final Logger LOG = LoggerFactory.getLogger(TimingEventListener.class);

    private long callStartNanos;

    @Override
    public void callStart(Call call) {
        callStartNanos = System.nanoTime();
        super.callStart(call);
    }

    @Override
    public void callEnd(Call call) {
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - callStartNanos);
        LOG.debug("Call to {} completed in {} ms", call.request().url(), elapsedMillis);
        super.callEnd(call);
    }

    @Override
    public void callFailed(Call call, IOException ioe) {
        long elapsedMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - callStartNanos);
        LOG.warn("Call to {} failed in {} ms: {}", call.request().url(), elapsedMillis, ioe.getMessage());
        super.callFailed(call, ioe);
    }
}
