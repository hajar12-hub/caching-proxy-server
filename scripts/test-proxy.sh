#!/bin/bash

BASE_URL="http://localhost:8080"

echo "================================"
echo "  Caching Proxy - Test Script   "
echo "================================"

# Test 1 — Premier appel (MISS)
echo ""
echo "Test 1: GET /products — expecting X-Cache: MISS"
RESPONSE=$(curl -s -D - "$BASE_URL/products" -o /dev/null)
X_CACHE=$(echo "$RESPONSE" | grep -i "X-Cache" | tr -d '\r')
echo "Result : $X_CACHE"

# Test 2 — Deuxieme appel (HIT)
echo ""
echo "Test 2: GET /products — expecting X-Cache: HIT"
RESPONSE=$(curl -s -D - "$BASE_URL/products" -o /dev/null)
X_CACHE=$(echo "$RESPONSE" | grep -i "X-Cache" | tr -d '\r')
echo "Result : $X_CACHE"

# Test 3 — Endpoint different (MISS)
echo ""
echo "Test 3: GET /products/1 — expecting X-Cache: MISS"
RESPONSE=$(curl -s -D - "$BASE_URL/products/1" -o /dev/null)
X_CACHE=$(echo "$RESPONSE" | grep -i "X-Cache" | tr -d '\r')
echo "Result : $X_CACHE"

# Test 4 — Meme endpoint (HIT)
echo ""
echo "Test 4: GET /products/1 — expecting X-Cache: HIT"
RESPONSE=$(curl -s -D - "$BASE_URL/products/1" -o /dev/null)
X_CACHE=$(echo "$RESPONSE" | grep -i "X-Cache" | tr -d '\r')
echo "Result : $X_CACHE"

echo ""
echo "================================"
echo "  Tests completed               "
echo "================================"