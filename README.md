# SmartTodoBackend

SmartTodoBackend är ett Spring Boot 3-baserat REST- och WebSocket-API som driver SmartTodo-plattformen. Tjänsten hanterar användarregistrering, inloggning, todo-hantering med premiumbegränsningar, Stripe-betalningar, realtidschatt mellan användare och administratörer samt en AI-baserad funktion för att generera vanebeskrivningar.

## Funktioner

- **Autentisering & roller**  
  Spring Security med JWT för inloggning och rollhantering (`ROLE_USER`, `ROLE_PREMIUM`, `ROLE_ADMIN`). `AuthTokenFilter` och `CustomUserDetailService` injicerar roller i både HTTP- och STOMP-flöden.

- **Todo-hantering & premiumgränser**  
  CRUD-endpoints för todos med MapStruct-mappning. Gratisanvändare begränsas till 5 aktiva todos medan premiumanvändare saknar begränsning. Daglig reset markerar färdigställda todos som ofullständiga.

- **Stripe-betalningar**  
  `StripeService` skapar checkout-sessioner, hanterar webhooks och uppgraderar användare till premium. `StripeConfig` läser nycklar via miljövariabler.

- **Realtidschatt**  
  WebSocket/STOMP (med SockJS fallback) för supportkanaler. Vanliga användare publicerar i `/topic/support/admin`, administratörer svarar via `/user/{email}/queue/support`.

- **AI-hjälp**  
  `AiController` använder Spring AI och OpenAI-modeller för att föreslå engagerande beskrivningar av vanor.

- **Schemalagda jobb**  
  `DailyResetService` körs nattetid (`@EnableScheduling`) och återställer todos som markerats som klara.

## Teknisk stack

- Spring Boot 3.5 · Java 21
- Spring Security, JWT (`jjwt`)
- Spring Data JPA · MySQL
- Spring WebSocket/STOMP
- Spring AI (OpenAI)
- Stripe Java SDK
- MapStruct · Lombok
- JUnit 5 · AssertJ

## Förutsättningar

- JDK 21
- Maven 3.9+ (använd `mvnw`/`mvnw.cmd` som följer med projektet)
- MySQL 8 (eller Docker för att köra databasen via `compose.yaml`)
- Stripe API-nycklar & webhook-secret
- OpenAI API-nyckel (för AI-funktionen)

## Kom igång lokalt

```bash
git clone https://github.com/Azadan/SmartTodo.git
cd SmartTodo
./mvnw clean install
```

Starta MySQL (valfritt steg genom Docker):

```bash
docker compose up -d mysql
```

Starta applikationen:

```bash
./mvnw spring-boot:run
# Windows (PowerShell):
.\mvnw.cmd spring-boot:run
```

API:t exponeras på `http://localhost:8080/`. WebSocket-endpoint finns på `/ws`.

## Bygg

```bash
./mvnw clean package
```

Det färdiga paketet finns i `target/SmartTodo-0.0.1-SNAPSHOT.jar`.

## Miljövariabler

Konfigurera dessa (t.ex. via `application.properties`, `SPRING_APPLICATION_JSON` eller `.env` i IDE):

- `spring.datasource.url`, `spring.datasource.username`, `spring.datasource.password`, `spring.datasource.driver-class-name`
- `jwt.secret` (minst 32 tecken), `jwt.expiration` (millisekunder, t.ex. `86400000` för 24 h)
- `stripe.api.key`, `stripe.price.id`, `stripe.webhook.secret`
- `app.frontend.url` (används för Stripe success/cancel-URL:er)
- `spring.ai.openai.api-key` (krävs av Spring AI)

## Scripts & verktyg

- `./mvnw spring-boot:run` — startar dev-servern med hot reload (DevTools aktiverat)
- `./mvnw test` — kör enhetstester (bl.a. `TodoServiceTest`, `DailyRestServiceTest`)
- `./mvnw clean verify` — full build inklusive tester
- `docker compose up mysql` — startar MySQL i Docker (port 3306)

## Kodstruktur (översikt)

- `src/main/java/com/smarttodo/smarttodo/`
  - `Controller/` — REST- och STOMP-endpoints (`AuthController`, `TodoController`, `ChatController`, m.fl.)
  - `Service/` — affärslogik (`TodoService`, `StripeService`, `DailyResetService`, `AuthenticationService`)
  - `Config/` — säkerhet, Stripe och WebSocket-konfigurationer
  - `Security/` — JWT-filter, entrypoints och STOMP-interceptor
  - `Entity/`, `Repository/` — JPA-modeller och datalager
  - `DTO/`, `Request/`, `Response/` — MapStruct DTO:er, request- och responsobjekt
  - `Utility/` — hjälpfunktioner (t.ex. `JwtUtil`)
- `src/main/resources/` — `application.properties`, statiska resurser och Mallar
- `src/test/java/` — enhetstester för tjänstelagret
