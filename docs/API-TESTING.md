# Linkly — API testen (curl & Colima)

Projektordner: **`~/Development/Projects/linkly`**

## Voraussetzungen

```bash
# Colima starten (statt Docker Desktop)
colima start

# Postgres starten
cd ~/Development/Projects/linkly
docker compose up -d

# App starten (eigenes Terminal)
./gradlew bootRun
```

App läuft auf: **http://localhost:8080**

---

## curl — alle Endpoints

### Health (App lebt?)

```bash
curl -s http://localhost:8080/actuator/health | python3 -m json.tool
```

Erwartung: `"status": "UP"`

---

### Short-Link anlegen (POST)

```bash
curl -i -X POST http://localhost:8080/api/links \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://example.com/very/long/path"}'
```

Erwartung:
- Status **201 Created**
- JSON mit `code`, `url`, `shortUrl`
- `code` merken für die nächsten Befehle!

**Variable setzen (praktisch):**

```bash
RESPONSE=$(curl -s -X POST http://localhost:8080/api/links \
  -H 'Content-Type: application/json' \
  -d '{"url":"https://kotlinlang.org"}')

echo "$RESPONSE"
CODE=$(echo "$RESPONSE" | python3 -c "import sys,json; print(json.load(sys.stdin)['code'])")
echo "Code: $CODE"
```

---

### Short-Link als JSON lesen (GET)

```bash
curl -i "http://localhost:8080/api/links/$CODE"
```

Erwartung: **200** + JSON

Ohne Variable (Code manuell einsetzen):

```bash
curl -i http://localhost:8080/api/links/abc1234
```

---

### Redirect testen (GET /r/...)

Nur Header sehen (Browser-Verhalten simulieren):

```bash
curl -i "http://localhost:8080/r/$CODE"
```

Erwartung:
- Status **302 Found**
- Header `Location: https://...` (lange URL)

Redirect **mitfolgen** (curl lädt Zielseite):

```bash
curl -i -L "http://localhost:8080/r/$CODE"
```

---

### Fehlerfälle

**Unbekannter Code → 404:**

```bash
curl -i http://localhost:8080/api/links/doesnotexist
curl -i http://localhost:8080/r/doesnotexist
```

**Ungültiger Body (später mit Validierung → 400):**

```bash
curl -i -X POST http://localhost:8080/api/links \
  -H 'Content-Type: application/json' \
  -d '{"url":"not-a-url"}'
```

---

## Persistenz testen (Postgres)

1. Link anlegen (POST)
2. App stoppen (`Ctrl+C` in bootRun-Terminal)
3. App neu starten (`./gradlew bootRun`)
4. Gleichen Code per GET abrufen

→ Link noch da = Postgres funktioniert.

**In DB nachschauen (optional):**

```bash
cd ~/Development/Projects/linkly
docker compose exec postgres psql -U linkly -d linkly -c "SELECT * FROM links;"
```

---

## Colima — nützliche Befehle

```bash
colima start          # Docker-Runtime starten
colima stop           # stoppen
colima status         # läuft Colima?
docker compose ps     # läuft Postgres?
docker compose down   # Postgres stoppen
docker compose logs postgres   # DB-Logs
```

---

## curl-Flags (Merksatz)

| Flag | Bedeutung |
|------|-----------|
| `-i` | Response-Header + Body anzeigen |
| `-s` | Kein Progress-Balken (still) |
| `-L` | Redirects folgen |
| `-X POST` | HTTP-Methode setzen |
| `-H '...'` | Header setzen |
| `-d '...'` | Request-Body |
