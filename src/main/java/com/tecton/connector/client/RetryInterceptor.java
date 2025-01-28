package com.tecton.connector.client;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http2.ConnectionShutdownException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * An OkHttp Interceptor that handles retrying requests on transient failures.
 */
public class RetryInterceptor implements Interceptor {

    private static final Logger LOG = LoggerFactory.getLogger(RetryInterceptor.class);

    private final int maxRetries;
    private final long retryBackoffMillis;

    /**
     * Constructs a RetryInterceptor with the specified retry settings.
     *
     * @param maxRetries         The maximum number of retry attempts.
     * @param retryBackoffMillis The initial backoff duration in milliseconds between retries.
     */
    public RetryInterceptor(int maxRetries, long retryBackoffMillis) {
        this.maxRetries = maxRetries;
        this.retryBackoffMillis = retryBackoffMillis;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        IOException lastException = null;

        for (int attempt = 1; attempt <= maxRetries + 1; attempt++) {
            try {
                Response response = chain.proceed(request);
                if (response.isSuccessful()) {
                    return response;
                } else {
                    LOG.warn("Request failed with status code {}, attempt {}/{}", response.code(), attempt, maxRetries + 1);
                    response.close(); // Ensure response is closed
                }
            } catch (IOException e) {
                lastException = e;
                if (!shouldRetryOnException(e) || attempt == maxRetries + 1) {
                    throw e;
                }
                LOG.warn("Request failed due to exception on attempt {}/{}: {}", attempt, maxRetries + 1, e.getMessage());
            }

            try {
                sleepBeforeRetry(attempt);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Retry interrupted", e);
            }
        }

        if (lastException != null) {
            throw lastException;
        } else {
            throw new IOException("Request failed after " + maxRetries + " retries");
        }
    }

    /**
     * Determines if the request should be retried based on the exception.
     *
     * @param e The IOException encountered.
     * @return True if the exception is transient and the request should be retried.
     */
    private boolean shouldRetryOnException(IOException e) {
        return !(e instanceof ConnectionShutdownException);
    }

    private void sleepBeforeRetry(int attempt) throws InterruptedException {
        long backoff = retryBackoffMillis * (1L << (attempt - 1)); // Exponential backoff
        Thread.sleep(backoff);
    }
}
