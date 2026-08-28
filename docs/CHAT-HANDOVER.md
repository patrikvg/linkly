# Linkly — Chat-Übergabe

> Diese Datei ist für **neue Chat-Sessions** gedacht.  
> Am Anfang sagen: *„Lies `docs/CHAT-HANDOVER.md` und mach genau so weiter.“*

---

## Wer lernt hier?

**Patro** — Kotlin-Basics sind drin, Spring Boot war neu. Ziel: **Backend + Systemdesign** wirklich verstehen und als SWE anwenden können — nicht nur Code abschreiben.

---

## Lernmodus (WICHTIG — unbedingt einhalten)

### So will Patro lernen

1. **Langsam und ausführlich** — lieber ein Konzept gründlich als fünf oberflächlich.
2. **Patro schreibt den Code selbst** — der Assistent schreibt **nicht** ganze Features fertig.
3. **Ablauf pro Schritt:**
   - Konzept erklären (*Was? Warum? Wofür?*)
   - Klare, kleine Aufgabe stellen (ein Schritt)
   - Patro implementiert
   - Patro zeigt Code (oder sagt „fertig“)
   - Assistent **reviewt** Zeile für Zeile, korrigiert, erklärt Fehler
   - **Verständnisfragen** stellen — Patro antwortet mit eigenen Worten
   - Erst dann nächster Schritt
4. **Bei Unklarheit:** mehr erklären + Links zu offiziellen Docs (Spring, Kotlin, JPA) — nicht nur „mach X“.
5. **Assistent darf vorbereiten:** Infrastruktur (Docker, Gradle-Deps, `application.yml`), Cheat Sheets, Test-Skripte — **aber kein Feature-Code**, den Patro lernen soll.
6. **Fehler sind ok** — daraus lernen (z.B. `LinkRepository()` statt Injection, `save(code, url)` statt Entity, 302 vs 404).
7. **Ziel:** Irgendwann reichen kurze Specs, weil das Modell im Kopf sitzt — aber **noch nicht**. Jetzt noch ausführlich.

### Was der Assistent NICHT tun soll

- Ganzen Controller/Service/Entity in einem Rutsch schreiben
- Nur oberflächliche Aufgabenlisten ohne Erklärung
- Annahmen treffen statt nachzufragen bei größeren Entscheidungen
- Commits/Pushes ohne explizite Bitte

### Was der Assistent tun soll

- Nach jedem Milestone Verständnisfragen stellen
- Side-by-side „vorher/nachher“ zeigen (Map → Repository, etc.)
- Tabellen für HTTP-Status, Schichten, Verantwortlichkeiten
- `curl`-Beispiele verweisen auf `docs/API-TESTING.md`

---

## Projekt

| | |
|---|---|
| **Name** | Linkly — URL-Shortener zum Lernen |
| **Pfad** | `~/Development/Projects/linkly` |
| **Sprache** | Kotlin |
| **Framework** | Spring Boot 4.1.1 |
| **Build** | Gradle (Kotlin DSL) |
| **DB** | PostgreSQL 16 (Docker) |
| **Docker** | **Colima** (nicht Docker Desktop) |
| **Java** | 21 |

---

## Was bereits fertig ist

### Milestone 1 — HTTP + Schichten (in-Memory, selbst gebaut)
- [x] `HelloController` — erstes `@RestController`-Vorbild
- [x] `CreateLinkRequest`, `LinkResponse` — DTOs
- [x] `LinkService` — Code-Generierung, Geschäftslogik
- [x] `LinkController` — POST/GET/Redirect
- [x] Status-Codes: 201, 200, 302, 404
- [x] Dependency Injection (Konstruktor)

### Milestone 2 — Persistenz (Postgres + JPA, selbst gebaut)
- [x] `LinkEntity` — JPA Entity (`@Entity`, `@Id`)
- [x] `LinkRepository` — `JpaRepository` + `findByCode`
- [x] `LinkService` nutzt Repository statt `ConcurrentHashMap`
- [x] `docker-compose.yml` + `application.yml`
- [x] Persistenz getestet (Link überlebt Neustart)
- [x] API getestet mit curl — funktioniert

### Infrastruktur / Docs (vom Assistenten vorbereitet)
- [x] `docs/API-TESTING.md` — curl & Colima
- [x] `scripts/test-api.sh` — Smoke-Test
- [x] Tests: H2 in-memory für `./gradlew build`

---

## Architektur (aktuell)

```
HTTP Request
    ↓
LinkController     ← HTTP, Status-Codes, JSON/Redirect
    ↓
LinkService        ← Logik: Code generieren, Entity/Response bauen
    ↓
LinkRepository     ← DB-Zugriff (Spring Data JPA)
    ↓
LinkEntity / Postgres (Tabelle: links)
```

**API-Endpoints:**

| Methode | Pfad | Status | Beschreibung |
|---------|------|--------|--------------|
| POST | `/api/links` | 201 | Short-Link anlegen |
| GET | `/api/links/{code}` | 200/404 | Link als JSON |
| GET | `/r/{code}` | 302/404 | Redirect zur langen URL |
| GET | `/actuator/health` | 200 | Health-Check |

---

## Projektstruktur (relevant)

```
linkly/
├── docs/
│   ├── CHAT-HANDOVER.md      ← diese Datei
│   └── API-TESTING.md        ← curl-Referenz
├── scripts/
│   └── test-api.sh
├── docker-compose.yml
├── build.gradle.kts
└── src/main/kotlin/com/linkly/
    ├── LinklyApplication.kt
    ├── hello/HelloController.kt
    └── link/
        ├── CreateLinkRequest.kt
        ├── LinkResponse.kt
        ├── LinkEntity.kt
        ├── LinkRepository.kt
        ├── LinkService.kt
        └── LinkController.kt
```

---

## Entwicklung starten

```bash
colima start
cd ~/Development/Projects/linkly
docker compose up -d
./gradlew bootRun
```

Testen: `./scripts/test-api.sh` oder Befehle in `docs/API-TESTING.md`

---

## Konzepte, die Patro schon kennt

- `@RestController`, `@GetMapping`, `@PostMapping`, `@RequestBody`, `@PathVariable`
- `@Service`, `@Entity`, Dependency Injection via Konstruktor
- HTTP: GET vs POST, 201/200/302/404
- `ResponseEntity` für Redirect (302 + `Location`-Header)
- Schichten: Controller / Service / Repository
- DTO vs Entity (`LinkResponse` vs `LinkEntity`)
- `JpaRepository` — `save()` geerbt, `findByCode()` deklariert
- Repository **nicht** mit `()` instanziieren — Spring injiziert
- `findByCode` liefert `LinkEntity?`, nicht `String`
- In-Memory → Postgres: gleiche API, anderer Speicher

---

## Bekannte kleine Baustellen (optional aufräumen)

- `LinkService.kt` hat evtl. noch ungenutzten Import (`ConcurrentHashMap`) — entfernen
- Keine URL-Validierung (`@Valid`, `@URL`) — **nächster sinnvoller Schritt**
- Keine dedizierten API-Tests (nur `contextLoads`)
- Code-Kollisionen bei Short-Code: einfaches Überschreiben, noch nicht „neu würfeln bis frei“
- Keine Flyway/Liquibase Migrations — Hibernate `ddl-auto: update`

---

## Nächste sinnvolle Milestones (Reihenfolge)

1. **Validierung** — `@Valid` auf Controller, `@NotBlank`/`@URL` auf `CreateLinkRequest` → 400 bei ungültiger URL  
   *(kleiner Schritt, wenig Setup)*

2. **API-Tests** — `@WebMvcTest` oder `@SpringBootTest` + MockMvc für POST/GET/404

3. **Systemdesign-Themen am gleichen Projekt:**
   - Idempotenz, Rate Limiting
   - Caching (Redis)
   - Click-Analytics (zweite Entity)
   - Flyway Migrations

4. **Später:** Auth, Observability, Deployment

→ Immer gleicher Lernmodus: erklären → Patro codet → review → Fragen.

---

## Nützliche Quellen (Patro nutzt diese)

- Spring REST Guide: https://spring.io/guides/gs/rest-service/
- Spring Data JPA: https://spring.io/guides/gs/accessing-data-jpa/
- Spring Web MVC: https://docs.spring.io/spring-framework/reference/web/webmvc.html
- Kotlin data classes: https://kotlinlang.org/docs/data-classes.html

---

## Beispiel-Prompt für neuen Chat

```
Ich arbeite am Linkly-Projekt (Kotlin/Spring Boot URL-Shortener).
Lies bitte docs/CHAT-HANDOVER.md — besonders den Lernmodus.
Mach genau so weiter: ich schreibe den Code, du erklärst und reviewst.
Lass uns mit [Validierung / API-Tests / …] weitermachen.
```

---

*Stand: August 2026 — nach Milestone Postgres (persistente Links)*
