# Introduction

This Sink Connector will transform data from a Kafka topic into a batch of json messages that will be written via HTTP to a configured Push Source via Tecton's Ingest API.

## Configuration

| Name                              | Description                                                                                                                       | Type     | Default | Valid Values | Importance |
|-----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------|----------|---------|--------------|------------|
| tecton.http.cluster.endpoint      | The endpoint of your Tecton cluster, formatted as: https://<your_cluster>.tecton.ai                                               | string   |         |              | high       |
| tecton.http.auth.token            | The authorisation token used to authenticate requests to the Tecton Ingest API.                                                   | password |         |              | high       |
| tecton.http.connect.timeout       | The HTTP connect timeout for the Tecton Ingest API in seconds.                                                                    | int      | 30      |              | medium     |
| tecton.http.request.timeout       | The HTTP request timeout for the Tecton Ingest API in seconds.                                                                    | int      | 30      |              | medium     |
| tecton.http.async.enabled         | Enables HTTP asynchronous sending to allow concurrent requests to Tecton Ingest API. Event order cannot be guaranteed.            | boolean  | true    |              | medium     |
| tecton.http.concurrency.limit     | Limits the number of concurrent HTTP requests to the Tecton Ingest API when asynchronous sending is enabled.                      | int      | 50      |              | medium     |
| tecton.workspace.name             | The name of the Tecton workspace where the Push Sources(s) are defined                                                            | string   |         |              | high       |
| tecton.push.source.name           | The name of the Tecton Push Source to write the record(s) to. If not defined, Sink will use topic name as the Push Source name.   | string   |         |              | medium     |
| tecton.dry.run.enabled            | When set to True, the request will be validated but no events will be written to the Online Store.                                | boolean  | true    |              | medium     |
| tecton.batch.max.size             | The maximum size of the batch of events sent to Tecton. There is currently no limit for Ingest API, but Tecton recommends 10.     | int      | 10      |              | medium     |
| tecton.kafka.timestamp.enabled    | Indicates whether to include the Kafka timestamp in the Tecton record.                                                            | boolean  | false   |              | low        |
| tecton.kafka.key.enabled          | Indicates whether to include the Kafka key in the Tecton record.                                                                  | boolean  | false   |              | low        |
| tecton.kafka.headers.enabled      | Indicates whether to include the Kafka headers in the Tecton record.                                                              | boolean  | false   |              | low        |
| tecton.logging.event.data.enabled | Determines whether the event data should be logged for debugging purposes. Enabling could risk sensitive data appearing in logs.  | boolean  | false   |              | low        |
| tecton.kafka.sanitise.keys.enabled| A mechanism to remove special characters from JSON keys and replace them with underscores.										| boolean  | false   |              | low        |


### Example Configuration

```
# Connector class configuration
name=tecton-ingest-api-sink
topics=<source-topics>
tasks.max=1
connector.class=com.tecton.kafka.connect.TectonHttpSinkConnector

# Tecton HTTP-related configurations
tecton.http.cluster.endpoint=https://<tecton-cluster-endpoint>
tecton.http.auth.token=<auth-token>
tecton.http.connect.timeout=30
tecton.http.request.timeout=30
tecton.http.async.enabled=true
tecton.http.concurrency.limit=5

# Tecton payload related configurations
tecton.workspace.name=<workspace-name>
tecton.push.source.name=<push-source-name>
tecton.dry.run.enabled=false
tecton.batch.max.size=10

# Kafka-related configurations
tecton.kafka.timestamp.enabled=false
tecton.kafka.key.enabled=false
tecton.kafka.headers.enabled=false
tecton.kafka.sanitise.keys.enabled=false

# Logging configuration - warning will print event payload data to logs
tecton.logging.event.data.enabled=false

# Optional: Configure errant record reporter
errors.log.enable=true
errors.tolerance=all
errors.log.include.messages=true
errors.retry.delay.max.ms=60000
errors.deadletterqueue.topic.name=<dlq-topic>
errors.deadletterqueue.topic.replication.factor=1
errors.deadletterqueue.context.headers.enable=true
```

## Building the source

```
mvn clean package
```

## Debugging
```
./bin/debug.sh
```

Start the connector with debugging enabled. This will wait for a debugger to attach.
This assumes that a Kafka Connect cluster is already installed and binaries in path.

```
export SUSPEND='y'
./bin/debug.sh
```