#!/bin/bash

echo "================================"
echo "  Clearing Proxy Cache          "
echo "================================"

./mvnw spring-boot:run "-Dspring-boot.run.arguments=--clear-cache"

echo "================================"
echo "  Cache cleared successfully    "
echo "================================"