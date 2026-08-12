#!/bin/bash

ENDPOINT="http://localhost:8080/api/subreddits"

echo "Fetching subreddits from $ENDPOINT..."
curl -s -X GET "$ENDPOINT" -H "Accept: application/json" | jq .