# Pensionat App (Booking Service)

Del av ett mikrotjänstsystem (3 tjänster totalt) för ett bokningssystem. Den här tjänsten äger **rum** och **bokningar**.

## Vad tjänsten gör

### Rum (`/api/rooms`)
- Skapa, hämta, uppdatera och ta bort rum.
- Rum har rumsnummer, antal sängar, sängtyp (`SINGLE_BED`, `DOUBLE_BED`, `TWIN_ROOM`) och pris per natt.
- Sök lediga rum för ett datumintervall (`GET /api/rooms/available`).
- Ett rum går inte att ta bort om det har aktiva bokningar.
- Seed-data: 10 rum skapas automatiskt om databasen är tom.

### Bokningar (`/api/bookings`)
- Skapa en bokning för en kund (identifieras via e-post) på ett specifikt rum och datumintervall.
- Uppdatera eller avboka (mjuk borttagning via status `CANCELLED`) en bokning.
- Hämta bokningar via ID eller via kundens e-post.
- Validering: start-/slutdatum krävs, startdatum får inte vara bakåt i tiden, slutdatum måste vara efter startdatum, rummet får inte redan vara bokat under perioden, och extrasäng kan bara bokas i dubbelrum.
- Interna endpoints som `customer-service` anropar vid borttagning av en kund:
  - `GET /api/bookings/active-bookings/{customerId}` – finns aktiva bokningar?
  - `POST /api/bookings/unlink-bookings/{customerId}` – nollställer `customerId` på kundens bokningar (historiken bevaras men kopplas loss från kunden).

### Endpoints i korthet

| Metod | Path | Beskrivning | Kräver JWT |
|---|---|---|---|
| GET | `/api/rooms` | Lista alla rum | Nej |
| GET | `/api/rooms/{id}` | Hämta ett rum | Nej |
| GET | `/api/rooms/available?startDate=&endDate=` | Lediga rum för period | Nej |
| POST | `/api/rooms` | Skapa rum | Ja |
| PUT | `/api/rooms/{id}` | Uppdatera rum | Ja |
| DELETE | `/api/rooms/{id}` | Ta bort rum | Ja |
| GET | `/api/bookings` | Lista alla bokningar | Ja |
| GET | `/api/bookings/{id}` | Hämta en bokning | Ja |
| GET | `/api/bookings/by-email?email=` | Bokningar för en kund | Ja |
| POST | `/api/bookings` | Skapa bokning | Ja |
| PUT | `/api/bookings/{id}` | Uppdatera bokning | Ja |
| PATCH | `/api/bookings/{id}/cancel` | Avboka | Ja |
| GET | `/api/bookings/active-bookings/{id}` | Har kund aktiva bokningar (internt anrop) | Ja |
| POST | `/api/bookings/unlink-bookings/{id}` | Koppla loss kund från bokningar (internt anrop) | Ja |

## Hur tjänsterna pratar med varandra

- **JWT**: Tjänsten litar på samma JWT-tokens som `customer-service` genererar (delad `JWT_SECRET`). `JwtFilter` läser `Authorization: Bearer <token>` och sätter en autentiserad användare i security-kontexten, men gör i dagsläget ingen ny koll mot kunddatabasen — token räcker för att räknas som inloggad här.
- **Anrop till customer-service** (via `CustomerClient`, byggd på `RestClient`):
  - `GET {customer-service.base-url}/api/customers/by-email?email=` – slår upp kunden vid bokning, så att bokningen kan sparas med rätt `customerId` och visas med kunduppgifter.
  - `GET {customer-service.base-url}/api/customers/{id}` – slår upp kunduppgifter när en bokning ska visas (t.ex. namn/e-post i svaret).
  - Den inkommande JWT-token vidarebefordras automatiskt till `customer-service` i dessa anrop (via en request-interceptor), så att kundtjänsten kan lita på anropet.
  - Om `customer-service` inte går att nå eller svarar med fel kastas `CustomerServiceUnavailableException` (404 tolkas som "kund saknas", övriga fel som tjänsten otillgänglig).
- Tjänsten är alltså **producent** för `customer-service` (svarar på aktiva bokningar/unlink vid kundborttagning) och **konsument** av `customer-service` (slår upp kunduppgifter vid bokning/visning).

## Konfiguration (miljövariabler)

```
DB_URL=jdbc:mysql://localhost:3306/pensionat_db
DB_USERNAME=root
DB_PASSWORD=<ditt-db-lösenord>
JWT_SECRET=<samma hemlighet som i customer-service>
CUSTOMER_SERVICE_BASE_URL=http://customer-service:8081   # url till customer-service
```

Tjänsten lyssnar på port **8083** (`server.port=8083`).

> **Obs:** `JWT_SECRET` måste vara identisk i `customer-service` och `pensionat-app`, annars kan inte tokens verifieras korrekt mellan tjänsterna.

## Starta tjänsten

> Repot innehåller i dagsläget ingen `Dockerfile`/`docker-compose.yml` bland de filer som delats här. Om ni har samma upplägg som `customer-service` (multi-stage build, Alpine + Temurin 17, MySQL med healthcheck) går det bra att spegla den strukturen. Säg till om ni vill att jag skriver ett förslag på `Dockerfile` och `docker-compose.yml` för det här repot också.

### Lokalt utan Docker

Kräver Java 17, Maven och en lokal MySQL-instans.

```bash
./mvnw spring-boot:run
```

Se till att `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET` och `CUSTOMER_SERVICE_BASE_URL` finns tillgängliga, t.ex. via en `.env`-fil (läses automatiskt med `dotenv-java`).

## Starta hela systemet

Se README för `customer-service` för hur de två tjänsterna kopplas ihop via en gemensam `docker-compose.yml`. Kort sammanfattat behöver `pensionat-app`:

- Nå `customer-service` på `CUSTOMER_SERVICE_BASE_URL` (t.ex. `http://customer-service:8081` i ett Docker-nätverk).
- Ha samma `JWT_SECRET` som `customer-service`.
- En egen MySQL-databas för rum och bokningar.

Systemet innehåller även `review-service` (recensioner av rum, port 8082). Den tjänsten anropar inte `pensionat-app` och tvärtom — kopplingen mellan dem är enbart logisk (`roomId` i en recension pekar mot ett rum här) samt delad `JWT_SECRET`.

## Teknisk stack

- Java 17, Spring Boot (Web, Data JPA, Security, Validation)
- MySQL 8
- JWT (jjwt) för autentisering
- `RestClient` för kommunikation med `customer-service`
