#!/bin/bash

SCHEMA_DIR=$1
CONTAINER_ID=$2
HOSTNAME=$3
PORT=$4

echo "Received create topic for schemas under $SCHEMA_DIR, for container=$CONTAINER_ID, hostname=$HOSTNAME, port=$PORT"

for file in $SCHEMA_DIR/*.json;
do
     [ -e "$file" ] || continue
     filename=$(basename -- "$file")
     topic="${filename%.*}"
     echo "Creating topic: $topic"
     docker exec -i -t $CONTAINER_ID opt/kafka/bin/kafka-topics.sh --create --topic $topic --bootstrap-server $HOSTNAME:$PORT
done