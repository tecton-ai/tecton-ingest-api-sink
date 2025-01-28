#!/usr/bin/env bash

: ${SUSPEND:='n'}

set -e

mvn clean package
export KAFKA_JMX_OPTS="-Xdebug -agentlib:jdwp=transport=dt_socket,server=y,suspend=${SUSPEND},address=5005"


connect-standalone config/connect-standalone.properties config/tecton-sink-fraud.properties config/tecton-sink-transactions.properties config/s3-sink-transactions.properties