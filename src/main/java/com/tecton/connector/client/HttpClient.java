package com.tecton.connector.client;

import com.tecton.connector.error.HttpClientException;
import com.tecton.connector.model.TectonApiRequest;
import com.tecton.connector.model.TectonApiResponse;

import java.io.Closeable;
import java.util.concurrent.CompletableFuture;

/**
 * Defines the contract for HTTP clients interacting with the Tecton Ingest API.
 */
public interface HttpClient extends Closeable {

    /**
     * Sends a synchronous request to the Tecton Ingest API.
     *
     * @param request The Tecton API request to send.
     * @return The Tecton API response received.
     * @throws HttpClientException If an error occurs during the HTTP request.
     */
    TectonApiResponse sendSync(TectonApiRequest request) throws HttpClientException;

    /**
     * Sends an asynchronous request to the Tecton Ingest API.
     *
     * @param request The Tecton API request to send.
     * @return A CompletableFuture that will be completed with the response or an exception.
     */
    CompletableFuture<TectonApiResponse> sendAsync(TectonApiRequest request);

    /**
     * Closes the HTTP client and releases any resources held.
     */
    @Override
    void close();
}
