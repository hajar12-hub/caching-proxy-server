#!/bin/bash

PORT=${1:-8080}
ORIGIN=${2:-http://dummyjson.com}

echo "================================"
echo "  Starting Caching Proxy Server "
echo "================================"
echo "Port   : $PORT"
echo "Origin : $ORIGIN"
echo "================================"

./mvnw spring-boot:run "-Dspring-boot.run.arguments=--port=$PORT --origin=$ORIGIN"