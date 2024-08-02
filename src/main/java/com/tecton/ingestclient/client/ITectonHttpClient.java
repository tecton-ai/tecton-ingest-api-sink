package com.tecton.ingestclient.client;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.RetriableException;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * Interface for Tecton HTTP Client.
 */
public interface ITectonHttpClient {

  /**
   * Sends a synchronous request to the Tecton Ingest API.
   *
   * @param requestData The request data to be sent.
   * @return The API response.
   * @throws ConnectException If a non-retriable error occurs.
   * @throws RetriableException If a retriable error occurs.
   */
  TectonApiResponse sendSync(TectonApiRequest requestData)
      throws ConnectException, RetriableException;

  /**
   * Sends an asynchronous request to the Tecton Ingest API.
   *
   * @param requestData The request data to be sent.
   * @return A CompletableFuture representing the API response.
   */
  CompletableFuture<TectonApiResponse> sendAsync(TectonApiRequest requestData);

  /**
   * Sends a batch of asynchronous requests to the Tecton Ingest API.
   *
   * @param batchRequestData The list of request data to be sent.
   * @return A list of CompletableFutures representing the API responses.
   */
  List<CompletableFuture<TectonApiResponse>> sendAsyncBatch(
      List<TectonApiRequest> batchRequestData);

  /**
   * Factory method to create an instance of TectonHttpClient.
   *
   * @param config The configuration for the Tecton HTTP Sink Connector.
   * @return An instance of TectonHttpClient.
   */
  static TectonHttpClient create(TectonHttpSinkConnectorConfig config) {
    return new TectonHttpClient(config);
  }
}
