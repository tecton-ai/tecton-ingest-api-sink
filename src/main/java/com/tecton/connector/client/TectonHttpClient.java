package com.tecton.connector.client;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tecton.connector.error.HttpClientException;
import com.tecton.connector.model.TectonApiError;
import com.tecton.connector.model.TectonApiRequest;
import com.tecton.connector.model.TectonApiResponse;
import com.tecton.connector.util.JsonUtil;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Implementation of HttpClient using OkHttp.
 */
public class TectonHttpClient implements HttpClient {

    private static final Logger LOG = LoggerFactory.getLogger(TectonHttpClient.class);

    private final OkHttpClient client;
    private final String clusterEndpoint;
    private final String authToken;
    private final boolean loggingEventDataEnabled;
    private volatile boolean isClosed = false;

    private TectonHttpClient(Builder builder) {
        this.client = builder.client;
        this.clusterEndpoint = builder.clusterEndpoint;
        this.authToken = builder.authToken;
        this.loggingEventDataEnabled = builder.loggingEventDataEnabled;
    }

    public static class Builder {
        private OkHttpClient client;
        private String clusterEndpoint;
        private String authToken;
        private boolean loggingEventDataEnabled;

        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        public Builder clusterEndpoint(String clusterEndpoint) {
            this.clusterEndpoint = clusterEndpoint;
            return this;
        }

        public Builder authToken(String authToken) {
            this.authToken = authToken;
            return this;
        }

        public Builder loggingEventDataEnabled(boolean loggingEventDataEnabled) {
            this.loggingEventDataEnabled = loggingEventDataEnabled;
            return this;
        }

        public TectonHttpClient build() {
            Objects.requireNonNull(client, "OkHttpClient cannot be null");
            Objects.requireNonNull(clusterEndpoint, "Cluster endpoint cannot be null");
            Objects.requireNonNull(authToken, "Auth token cannot be null");
            return new TectonHttpClient(this);
        }
    }

    @Override
    public TectonApiResponse sendSync(TectonApiRequest request) throws HttpClientException {
        ensureNotClosed();
        Request httpRequest = buildHttpRequest(request);
        try (Response response = client.newCall(httpRequest).execute()) {
            return handleResponse(response);
        } catch (IOException e) {
            throw new HttpClientException("Error during HTTP request execution", e);
        }
    }

    @Override
    public CompletableFuture<TectonApiResponse> sendAsync(TectonApiRequest request) {
        ensureNotClosed();
        CompletableFuture<TectonApiResponse> future = new CompletableFuture<>();
        Request httpRequest;
        try {
            httpRequest = buildHttpRequest(request);
        } catch (HttpClientException e) {
            future.completeExceptionally(e);
            return future;
        }

        client.newCall(httpRequest).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                future.completeExceptionally(new HttpClientException("Error during asynchronous HTTP request", e));
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    TectonApiResponse apiResponse = handleResponse(response);
                    future.complete(apiResponse);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                }
            }
        });
        return future;
    }

    private Request buildHttpRequest(TectonApiRequest request) throws HttpClientException {
        try {
            String url = clusterEndpoint + "/ingest";
            String jsonBody = JsonUtil.toJson(request);
            if (loggingEventDataEnabled) {
                LOG.debug("Sending request to URL: {} with body: {}", url, jsonBody);
            } else {
                LOG.debug("Sending request to URL: {}", url);
            }

            return new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(jsonBody, MediaType.get("application/json")))
                    .addHeader("Authorization", "Tecton-key " + authToken)
                    .addHeader("Content-Type", "application/json")
                    .build();
        } catch (Exception e) {
            throw new HttpClientException("Error building HTTP request", e);
        }
    }

    private TectonApiResponse handleResponse(Response response) throws HttpClientException {
        int statusCode = response.code();
        String responseBody;

        try {
            responseBody = response.body() != null ? response.body().string() : null;

            if (response.isSuccessful()) {
                return JsonUtil.fromJson(responseBody, TectonApiResponse.class);
            } else {
                if (responseBody != null) {
                    TectonApiError apiError = JsonUtil.fromJson(responseBody, TectonApiError.class);
                    LOG.error("Tecton API Error: {}", apiError);
                    throw new HttpClientException("Tecton API Error: " + apiError.toString(), statusCode);
                } else {
                    LOG.error("HTTP Error {} with empty response body", statusCode);
                    throw new HttpClientException("HTTP Error with empty response body", statusCode);
                }
            }
        } catch (IOException e) {
            throw new HttpClientException("Error reading response body", e, statusCode);
        }
    }

    private void ensureNotClosed() {
        if (isClosed) {
            throw new IllegalStateException("HttpClient has been closed");
        }
    }

    @Override
    public void close() {
        if (!isClosed) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            isClosed = true;
            LOG.info("TectonHttpClient has been closed");
        }
    }
}
