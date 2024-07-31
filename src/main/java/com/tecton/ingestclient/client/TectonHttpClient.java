package com.tecton.ingestclient.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.tecton.ingestclient.util.JsonUtil;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * Interface for Tecton HTTP Client.
 */
public interface TectonHttpClient {

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
    return new TectonHttpClientImpl(config);
  }
}


/**
 * Implementation of TectonHttpClient.
 */
class TectonHttpClientImpl implements TectonHttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(TectonHttpClientImpl.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String ACCEPT_HEADER = "Accept";
  private static final String APPLICATION_JSON_VALUE = "application/json";
  private static final String TECTON_KEY_FORMAT = "Tecton-key %s";
  private static final String ENDPOINT_PATH = "/ingest";
  private static final Set<Integer> RETRIABLE_ERROR_CODES =
      Set.of(408, 425, 429, 500, 502, 503, 504);

  private final URI tectonApiBaseEndpoint;
  private final HttpClient client;
  private final TectonHttpSinkConnectorConfig config;
  private final Semaphore concurrencySemaphore;

  /**
   * Initializes a new instance of {@code TectonHttpClientImpl}.
   *
   * @param config The configuration object containing details such as the base endpoint of the
   *        Tecton API, the API key for authentication, connection timeout, and request timeout.
   */
  public TectonHttpClientImpl(TectonHttpSinkConnectorConfig config) {
    this.tectonApiBaseEndpoint = URI.create(config.httpClusterEndpoint);
    LOG.debug("Initializing TectonHttpClient with endpoint: {}", tectonApiBaseEndpoint);
    this.config = config;
    this.client = HttpClient.newBuilder().connectTimeout(config.httpConnectTimeout)
        .version(HttpClient.Version.HTTP_2).build();
    this.concurrencySemaphore = new Semaphore(config.httpConcurrencyLimit);
  }

  @Override
  public TectonApiResponse sendSync(TectonApiRequest requestData)
      throws ConnectException, RetriableException {
    LOG.debug("Sending synchronous request to Tecton Ingest API");
    acquireSemaphore();
    try {
      HttpRequest request = constructHttpRequest(requestData);
      long startTime = System.currentTimeMillis();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      long endTime = System.currentTimeMillis();
      TectonApiResponse apiResponse = processResponse(response);
      LOG.debug("Received synchronous response from Tecton API in {} ms, Response: {}",
          endTime - startTime, apiResponse);
      return apiResponse;
    } catch (IOException | InterruptedException e) {
      LOG.warn("Retriable error occurred: {}", e.toString());
      throw new RetriableException("Retriable error occurred", e);
    } finally {
      releaseSemaphore();
    }
  }

  @Override
  public CompletableFuture<TectonApiResponse> sendAsync(TectonApiRequest requestData) {
    LOG.debug("Sending asynchronous request to Tecton Ingest API");
    CompletableFuture<TectonApiResponse> future = new CompletableFuture<>();
    if (!acquireSemaphoreNonBlocking(future)) {
      return future;
    }
    try {
      HttpRequest request = constructHttpRequest(requestData);
      long startTime = System.currentTimeMillis();
      client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).thenApply(response -> {
        long endTime = System.currentTimeMillis();
        TectonApiResponse apiResponse = processResponse(response);
        LOG.debug("Received asynchronous response from Tecton API in {} ms, Response: {}",
            endTime - startTime, apiResponse);
        releaseSemaphore();
        future.complete(apiResponse);
        return null;
      }).exceptionally(e -> handleAsyncException(e, future));
    } catch (Exception e) {
      future.completeExceptionally(new RetriableException("Error during async request", e));
    }
    return future;
  }

  @Override
  public List<CompletableFuture<TectonApiResponse>> sendAsyncBatch(
      List<TectonApiRequest> batchRequestData) {
    LOG.debug("Sending a batch of {} asynchronous requests to Tecton API", batchRequestData.size());
    return batchRequestData.stream().map(this::sendAsync).collect(Collectors.toList());
  }

  /**
   * Attempts to acquire a semaphore without blocking.
   *
   * @param future The future to complete if semaphore acquisition fails.
   * @return True if the semaphore was acquired, otherwise false.
   */
  private boolean acquireSemaphoreNonBlocking(CompletableFuture<?> future) {
    try {
      if (!concurrencySemaphore.tryAcquire(config.httpRequestTimeout.toSeconds(),
          TimeUnit.SECONDS)) {
        future.completeExceptionally(
            new RetriableException("Failed to acquire semaphore within timeout"));
        return false;
      }
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      future.completeExceptionally(
          new RetriableException("Thread was interrupted while acquiring semaphore", e));
      return false;
    }
  }

  /**
   * Acquires the semaphore, blocking until it is available or the timeout is reached.
   *
   * @throws RetriableException If the semaphore could not be acquired within the timeout.
   */
  private void acquireSemaphore() throws RetriableException {
    try {
      if (!concurrencySemaphore.tryAcquire(config.httpRequestTimeout.toSeconds(),
          TimeUnit.SECONDS)) {
        throw new RetriableException("Failed to acquire semaphore within timeout");
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RetriableException("Thread was interrupted while acquiring semaphore", e);
    }
  }

  /**
   * Releases the semaphore.
   */
  private void releaseSemaphore() {
    concurrencySemaphore.release();
  }

  /**
   * Handles exceptions that occur during asynchronous requests.
   *
   * @param e The exception that occurred.
   * @param future The future to complete exceptionally.
   * @return Always returns null.
   */
  private Void handleAsyncException(Throwable e, CompletableFuture<TectonApiResponse> future) {
    releaseSemaphore();
    Throwable cause = e.getCause();
    if (cause instanceof IOException && cause.getMessage().contains("GOAWAY received")) {
      LOG.warn("GOAWAY received, triggering Kafka Connect retry mechanism.");
      future.completeExceptionally(
          new RetriableException("Retriable error occurred: GOAWAY received", cause));
    } else {
      LOG.error("Error during asynchronous request: {}", e.toString());
      future.completeExceptionally(e);
    }
    return null;
  }

  /**
   * Constructs the HTTP request for the Tecton API.
   *
   * @param requestData The request data to be sent.
   * @return The constructed HTTP request.
   */
  private HttpRequest constructHttpRequest(TectonApiRequest requestData) {
    try {
      String jsonPayload = JsonUtil.toJson(requestData);
      URI endpoint = tectonApiBaseEndpoint.resolve(ENDPOINT_PATH);
      return HttpRequest.newBuilder(endpoint)
          .header(AUTHORIZATION_HEADER, String.format(TECTON_KEY_FORMAT, config.httpAuthToken))
          .header(CONTENT_TYPE_HEADER, APPLICATION_JSON_VALUE)
          .header(ACCEPT_HEADER, APPLICATION_JSON_VALUE).timeout(config.httpRequestTimeout)
          .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
    } catch (Exception e) {
      LOG.error("Failed to convert request data to JSON", e);
      throw new DataException("Error converting request data to JSON", e);
    }
  }

  /**
   * Processes the HTTP response from the Tecton API.
   *
   * @param response The HTTP response received.
   * @return The processed API response.
   * @throws ConnectException If a non-retriable error occurs.
   * @throws RetriableException If a retriable error occurs.
   */
  private TectonApiResponse processResponse(HttpResponse<String> response)
      throws ConnectException, RetriableException {
    int statusCode = response.statusCode();
    String responseBody = response.body();
    LOG.debug("Processing Tecton API response with status code: {}", statusCode);
    if (statusCode >= 200 && statusCode < 300) {
      return JsonUtil.fromJson(responseBody, TectonApiResponse.class);
    } else {
      handleErrorResponse(statusCode, responseBody);
      return null; // This line will never be reached as handleErrorResponse will always throw an
                   // exception
    }
  }

  /**
   * Handles error responses from the Tecton API.
   *
   * @param statusCode The status code of the HTTP response.
   * @param responseBody The body of the HTTP response.
   * @throws ConnectException If a non-retriable error occurs.
   * @throws RetriableException If a retriable error occurs.
   */
  private void handleErrorResponse(int statusCode, String responseBody)
      throws ConnectException, RetriableException {
    try {
      TectonApiError error = JsonUtil.fromJson(responseBody, TectonApiError.class);
      logApiError(error);
    } catch (DataException e) {
      LOG.error("Failed to parse error response body: {}", responseBody, e);
    }
    if (RETRIABLE_ERROR_CODES.contains(statusCode)) {
      LOG.warn("Retriable error from Tecton API with status code: {}", statusCode);
      throw new RetriableException(
          "Retriable error from Tecton API with status code: " + statusCode);
    } else {
      LOG.error("Non-retriable error from Tecton API with status code: {}", statusCode);
      throw new ConnectException(
          "Non-retriable error from Tecton API with status code: " + statusCode);
    }
  }

  /**
   * Logs detailed API error information.
   *
   * @param error The Tecton API error object.
   */
  private void logApiError(TectonApiError error) {
    if (error.getRecordErrors() != null && !error.getRecordErrors().isEmpty()) {
      error.getRecordErrors().forEach(err -> LOG.error("Error from Tecton API: {}", error));
    } else if (error.getRequestError() != null) {
      LOG.error("Request Error from Tecton API: {}", error);
    }
  }
}
