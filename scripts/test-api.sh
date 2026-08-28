#!/usr/bin/env bash
# Linkly API — schneller Smoke-Test
# Nutzung: ./scripts/test-api.sh
# Voraussetzung: App läuft auf localhost:8080

set -euo pipefail

BASE="${LINKLY_BASE_URL:-http://localhost:8080}"

echo "== Health =="
curl -sf "$BASE/actuator/health" | python3 -m json.tool
echo

echo "== Create link =="
RESPONSE=$(curl -sf -X POST "$BASE/api/links" \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/test-'$(date +%s)'"}')
echo "$RESPONSE" | python3 -m json.tool
CODE=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['code'])")
echo "Code: $CODE"
echo

echo "== Get link (JSON) =="
curl -sf "$BASE/api/links/$CODE" | python3 -m json.tool
echo

echo "== Redirect (headers only) =="
curl -si "$BASE/r/$CODE" | head -5
echo

echo "== Not found (404) =="
curl -si "$BASE/api/links/notfound123" | head -5
echo

echo "OK — alle Checks durch."
