package com.tecton.ingestclient.client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.errors.DataException;
import org.apache.kafka.connect.errors.RetriableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecton.kafka.connect.TectonHttpSinkConnectorConfig;

/**
 * A client for sending HTTP requests to the Tecton API.
 *
 * <p>This class provides mechanisms to construct and send requests to the Tecton API, process
 * the responses, and parse them into appropriate models.</p>
 */
public class TectonHttpClient {
  private static final Logger LOG = LoggerFactory.getLogger(TectonHttpClient.class);
  private static final String AUTHORIZATION_HEADER = "Authorization";
  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String ACCEPT_HEADER = "Accept";
  private static final String APPLICATION_JSON_VALUE = "application/json";
  private static final String TECTON_KEY_FORMAT = "Tecton-key %s";
  private static final String ENDPOINT_PATH = "/ingest";
  private static final Set<Integer> RETRIABLE_ERROR_CODES =
      Collections.unmodifiableSet(new HashSet<>(Arrays.asList(408, 425, 429, 500, 502, 503, 504)));

  private final URI tectonApiBaseEndpoint;
  private final HttpClient client;
  private final ObjectMapper objectMapper;
  private final TectonHttpSinkConnectorConfig config;
  private final Semaphore concurrencySemaphore;


  /**
   * Initialises a new instance of {@code TectonHttpClient}.
   *
   * @param config The configuration object containing details such as the base endpoint of the 
   *               Tecton API, the API key for authentication, connection timeout, and request timeout.
   */
  public TectonHttpClient(TectonHttpSinkConnectorConfig config) {
    this.tectonApiBaseEndpoint = URI.create(config.httpClusterEndpoint);
    LOG.debug("Initialising TectonHttpClient with endpoint: {}", tectonApiBaseEndpoint);
    this.config = config;
    this.client = buildHttpClient(config.httpConnectTimeout);
    this.objectMapper = new ObjectMapper();
    this.concurrencySemaphore = new Semaphore(config.httpConcurrencyLimit);

  }

  /**
   * Sends a synchronous request to the Tecton API.
   *
   * @param requestData The data to be sent to the Tecton API.
   * @return The response from the Tecton API.
   * @throws ConnectException If there's a non-retriable error from the Tecton API.
   * @throws RetriableException If there's a retriable error.
   */
  public TectonApiResponse sendSync(TectonApiRequest requestData)
      throws ConnectException, RetriableException {      
    LOG.debug("Sending synchronous request to Tecton Ingest API");
    
      try {
        concurrencySemaphore.acquire();
      } catch (InterruptedException e) {
        LOG.warn("Interrupted while waiting to acquire semaphore", e);
        Thread.currentThread().interrupt();
        throw new RetriableException("Interrupted while waiting to acquire semaphore", e);
      }
      
      try {
          HttpRequest request = constructHttpRequest(requestData);
          long startTime = System.currentTimeMillis();
          HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
          long endTime = System.currentTimeMillis();
          LOG.debug("Received synchronous response from Tecton API in {} ms", endTime - startTime);
          
          return processResponse(response);
      } catch (IOException | InterruptedException e) {
          LOG.warn("Retriable error occurred", e);
          throw new RetriableException("Retriable error occurred", e);
      }
  }

  /**
   * Sends an asynchronous request to the Tecton API.
   *
   * @param requestData The data to be sent to the Tecton API.
   * @return A future representing the response from the Tecton API.
   */
  public CompletableFuture<TectonApiResponse> sendAsync(TectonApiRequest requestData) {
    LOG.debug("Sending asynchronous request to Tecton Ingest API");

    try {
      concurrencySemaphore.acquire();
    } catch (InterruptedException e) {
      LOG.warn("Interrupted while waiting to acquire semaphore", e);
      Thread.currentThread().interrupt();
      return CompletableFuture.failedFuture(new RetriableException("Interrupted while waiting to acquire semaphore", e));
    }

    HttpRequest request;
    try {
        request = constructHttpRequest(requestData);
    } catch (Exception e) {
        concurrencySemaphore.release();
        return CompletableFuture.failedFuture(e);
    }

    long startTime = System.currentTimeMillis();
    
    return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
        .thenApply(response -> {
            long endTime = System.currentTimeMillis();
            LOG.debug("Received asynchronous response from Tecton API in {} ms", endTime - startTime);
            concurrencySemaphore.release();
            return processResponse(response);
        }).exceptionally(e -> {
          concurrencySemaphore.release();
          throw new RetriableException("Error during asynchronous request", e);
      });
  }

  /**
   * Sends a batch of asynchronous requests to the Tecton API.
   *
   * @param batchRequestData A list of data to be sent to the Tecton API.
   * @return A list of futures representing the responses from the Tecton API for each request.
   */
  public List<CompletableFuture<TectonApiResponse>> sendAsyncBatch(
      List<TectonApiRequest> batchRequestData) {
    LOG.debug("Sending batch of asynchronous requests to Tecton API with {} requests", batchRequestData.size());

    return batchRequestData.stream().map(this::sendAsync).collect(Collectors.toList());
  }

  private HttpClient buildHttpClient(Duration connectTimeout) {
    LOG.debug("Building HTTP client with connection timeout: {}", connectTimeout);

    return HttpClient.newBuilder().connectTimeout(connectTimeout).version(HttpClient.Version.HTTP_2)
        .build();
  }
  
  private HttpRequest constructHttpRequest(TectonApiRequest requestData) {
    try {
      String jsonPayload = objectMapper.writeValueAsString(requestData);
      URI endpoint = tectonApiBaseEndpoint.resolve(ENDPOINT_PATH);
      
      LOG.debug("Constructing HTTP request for endpoint: {}", endpoint);

      return HttpRequest.newBuilder(endpoint)
          .header(AUTHORIZATION_HEADER, String.format(TECTON_KEY_FORMAT, config.httpAuthToken))
          .header(CONTENT_TYPE_HEADER, APPLICATION_JSON_VALUE)
          .header(ACCEPT_HEADER, APPLICATION_JSON_VALUE).timeout(config.httpRequestTimeout)
          .POST(HttpRequest.BodyPublishers.ofString(jsonPayload)).build();
    } catch (JsonProcessingException e) {
      LOG.error("Failed to convert request data to JSON: {}", requestData, e);
      throw new DataException("Error converting request data to JSON", e);
    }
  }

  private TectonApiResponse processResponse(HttpResponse<String> response) {
    LOG.debug("Processing Tecton API response with status code: {}", response.statusCode());

    if (isSuccessStatus(response.statusCode())) {
      return parseSuccessfulResponse(response);
    } else {
       handleAndThrowError(response);
       // Will never reach here
       return null;
    }
  }

  private boolean isSuccessStatus(int statusCode) {
    return statusCode == 200;
  }

  private TectonApiResponse parseSuccessfulResponse(HttpResponse<String> response) {
    try {
      return objectMapper.readValue(response.body(), TectonApiResponse.class);
    } catch (JsonProcessingException e) {
      LOG.error("Error parsing Tecton API response", e);
      throw new DataException("Error parsing Tecton API response", e);
    }
  }

  private void handleAndThrowError(HttpResponse<String> response) {
    if (response.body() != null && !response.body().isEmpty()) {
        try {
            TectonApiError apiError = objectMapper.readValue(response.body(), TectonApiError.class);
            String errorMessage = String.format("Error from Tecton API. HTTP status: %d, Message: %s",
                    response.statusCode(), apiError.getRequestError().getErrorMessage());
            throwAppropriateException(response.statusCode(), errorMessage);
        } catch (JsonProcessingException e) {
            String errorMessage = String.format("Error parsing Tecton API error response. HTTP status: %d",
                    response.statusCode());
            throwAppropriateException(response.statusCode(), errorMessage);
        }
    } else {
        String errorMessage = String.format("Error from Tecton API with no error body. HTTP status: %d",
                response.statusCode());
        throwAppropriateException(response.statusCode(), errorMessage);
    }
}

  private void throwAppropriateException(int statusCode, String errorMessage) {
      if (RETRIABLE_ERROR_CODES.contains(statusCode)) {
          throw new RetriableException(errorMessage);
      } else {
          throw new ConnectException(errorMessage);
      }
  }


}
