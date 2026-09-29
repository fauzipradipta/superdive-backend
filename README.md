# superdive-backend

REST API for the Superdive CRM — a dive-shop system tracking customers, their
diving certifications, products and orders.

Companion repository: [`superdive`](https://github.com/fauzipradipta/superdive)
(React frontend, deployed on Vercel).

| | |
|---|---|
| **Stack** | Spring Boot 3.4.2, Java 21, Maven (wrapper), MySQL 8.4 |
| **Auth** | Stateless JWT (jjwt 0.12.6), BCrypt password hashing |
| **Production** | `https://api.superdive-crm.tech` — Docker Compose on a Hostinger VPS behind nginx |

---

## Table of contents

- [Architecture](#architecture)
- [Project layout](#project-layout)
- [Data model](#data-model)
- [API reference](#api-reference)
- [Authentication](#authentication)
- [Configuration](#configuration)
- [Running locally](#running-locally)
- [Deployment](#deployment)
- [CI](#ci)

---

## Architecture

A conventional layered Spring Boot application:

```
HTTP request
    ↓
JwtAuthenticationFilter        ← reads "Authorization: Bearer <token>", populates SecurityContext
    ↓
SecurityFilterChain            ← /api/auth/** public, everything else requires a valid JWT
    ↓
@RestController                ← HTTP concerns only: status codes, request/response bodies
    ↓
@Service                       ← business rules, @Transactional boundaries, DTO ↔ entity mapping
    ↓
JpaRepository                  ← data access
    ↓
MySQL
```

Requests and responses cross the controller boundary as **DTOs**, never as JPA
entities, except where a controller returns an entity directly (`Customer`,
`Orders`) — see [Known rough edges](#known-rough-edges).

## Project layout

```
src/main/java/com/example/superdive/backend/
├── BackendApplication.java        Spring Boot entry point
├── config/
│   ├── CorsConfig.java            CORS allow-list, driven by CORS_ALLOWED_ORIGINS
│   ├── SecurityConfig.java        Filter chain, public routes, BCrypt encoder
│   └── CustomDateDeserializer.java
├── controller/                    HTTP layer (Auth, Customer, Orders, Product, Reference, DivingData)
├── service/                       Business logic
├── repository/                    Spring Data JPA interfaces
├── entity/                        JPA entities (the database schema)
├── dto/                           Request/response shapes
│   ├── Request/                   Inbound payloads
│   └── Response/                  Outbound payloads
├── enums/                         PaymentStatus, ProductType
├── exception/                     Domain exceptions
└── security/                      JWT issuing/parsing, user details, current-user lookup

src/main/resources/
├── application.properties         Shared config + profile selection
├── application-local.properties   Local machine (safe hardcoded defaults)
├── application-dev.properties     Staging on the VPS
└── application-prod.properties    Production on the VPS

deploy/
├── deploy.sh                      Pull + rebuild + restart one environment
└── nginx/api.conf                 nginx site template for the VPS
```

## Data model

```
User ──< Customer ──< DivingData          A user owns customers; a customer has
          │    │                          certifications and references.
          │    └──< Reference
          │    └──< EmergencyContact
          │    └──< Product
          └──< Orders ──< OrdersItem >── Product
```

| Entity | Table | Notes |
|---|---|---|
| `User` | `users` | Login account. Unique `email`, BCrypt `password`. |
| `Customer` | `customers` | Unique `name`, `phoneNum`, `dob`, `diver` flag. Owned by a `User`. |
| `DivingData` | `diving_data` | Certification: `agencyName`, `level`. Belongs to a customer. |
| `EmergencyContact` | `emergency_contact` | `ecName`, `ecPhone`. |
| `Reference` | `reference` | Referring person: `referenceName`, `referencePhoneNum`. |
| `Product` | `product` | `name`, `type` (`ProductType`), `details`, `price`. |
| `Orders` | `orders` | `totalPrice`, `ordersDate`, `paymentStatus` (defaults to `UNPAID`). |
| `OrdersItem` | `orders_item` | Line item: `qty`, `price`, links an order to a product. |

**Enums**

- `PaymentStatus` — `UNPAID`, `PARTIAL`, `PAID`, `OVERDUE`, `REFUNDED`.
  `PaymentStatus.from(String)` accepts either the enum name (`"UNPAID"`) or the
  label the UI shows (`"Unpaid"`), so the frontend can send back whichever it holds.
- `ProductType` — `Retail`, `Course`, `Trip`, `Service`.

Schema is managed by Hibernate `ddl-auto=update` in every profile. There is no
migration tool yet; see [Known rough edges](#known-rough-edges).

## API reference

All paths are relative to the deployment root (`https://api.superdive-crm.tech`).
Every endpoint except `/api/auth/**` requires `Authorization: Bearer <token>`.

### Auth — public

| Method | Path | Body | Returns |
|---|---|---|---|
| `POST` | `/api/auth/register` | `UserRequestDTO` (`firstname`, `lastname`, `email`, `password`) | `UserResponseDTO` — 409 if the email exists |
| `POST` | `/api/auth/login` | `LoginRequestDTO` (`email`, `password`) | `AuthResponseDTO` (`token`, `type: "Bearer"`, `user`) — 401 on bad credentials |

### Customers

| Method | Path | Notes |
|---|---|---|
| `POST` | `/api/create-customer` | Body `CustomerDTO`. 409 if the name already exists. |
| `GET` | `/api/customers/{id}` | Single customer. 404 if missing. |
| `GET` | `/api/all-customer` | Paged. Query: `page` (1-based, default `1`), `limit` (default `50`). Returns `{ data, currentPage, totalItems, totalPages }`. |
| `PUT` | `/api/customers/update-customer/{id}` | Body `CustomerDTO`. 404 if missing. |

### Orders

| Method | Path | Notes |
|---|---|---|
| `POST` | `/api/create-orders` | Body `OrdersDTO`. |
| `POST` | `/api/{ordersId}/items` | Add a product line to an existing order. |
| `GET` | `/api/orders/{id}` | Single order. |
| `PATCH` | `/api/orders/{id}/payment-status` | Body `PaymentStatusRequestDTO`. |
| `GET` | `/api/payment-statuses` | The `PaymentStatus` values, for populating dropdowns. |
| `GET` | `/api/all-orders` | All orders. Not paginated. |
| `GET` | `/api/customer-orders-history/{customerId}` | Order history for one customer. |

### Products, references, diving data

| Method | Path | Notes |
|---|---|---|
| `GET` | `/api/products?type=<ProductType>` | Products of one type. |
| `GET` | `/api/all-products` | All products. |
| `POST` | `/api/reference` | Body `ReferenceDTO`. |
| `POST` | `/api/diving-data` | Body `DivingData`. |

Spring Boot Actuator is on the classpath; its endpoints live under `/actuator`.

## Authentication

Registration hashes the password with BCrypt and stores the user. Login verifies
the password and returns a signed JWT whose **subject is the user's email**.

On each request `JwtAuthenticationFilter` reads the `Authorization` header. A
missing, malformed, expired or otherwise invalid token is *not* an error — the
filter simply continues unauthenticated, and the security chain then rejects the
request with 401/403. This keeps public routes reachable with a stale token in
the header.

Sessions are `STATELESS` and CSRF is disabled: there is no cookie to protect.

`AuthenticatedUserProvider.getCurrentUser()` resolves the `User` entity for the
current request, for code that needs the owner rather than just the principal.

**Token lifetime** is `JWT_EXPIRATION`, default 24 hours. Rotating `JWT_SECRET`
invalidates every outstanding token, forcing all users to log in again — an
acceptable, non-destructive operation.

## Configuration

`spring.profiles.active` comes from `SPRING_PROFILES_ACTIVE` and defaults to
`local`. Each profile resolves its settings differently:

| | `local` | `dev` | `prod` |
|---|---|---|---|
| Source of config | Hardcoded fallbacks | Environment only | Environment only |
| Missing secret | Uses a safe default | Fails to start | Fails to start |
| SQL logging | On | On | Off |
| Error stack traces in responses | — | Always | Never |

`local` is deliberately the only profile allowed to hardcode values, so a fresh
clone runs with no setup. `dev` and `prod` have **no fallbacks on purpose**: a
missing secret fails the boot instead of silently using a known value.

### Environment variables

Used by `dev` and `prod` (and by `docker-compose.yml` in every environment):

| Variable | Purpose |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `local`, `dev` or `prod` |
| `COMPOSE_PROJECT_NAME` | Namespaces containers, network and volume so environments can share a VPS |
| `APP_HOST_PORT` / `MYSQL_HOST_PORT` | Host ports; give each environment its own pair |
| `MYSQL_DATABASE` / `MYSQL_ROOT_PASSWORD` | Database name and root password |
| `JWT_SECRET` | Base64, must decode to ≥32 bytes for HS256 (`openssl rand -base64 48`) |
| `JWT_EXPIRATION` | Token validity in ms (default `86400000`) |
| `CORS_ALLOWED_ORIGINS` | Exact frontend origin — scheme + host + port, **no trailing slash** |

Copy `.env.example` to `.env.local` / `.env.dev` / `.env.prod` and fill it in.
Only `.env.example` is committed; filled-in files are gitignored and live on the
VPS and nowhere else.

### CORS

`CorsConfig` publishes a `CorsConfigurationSource` bean rather than a
`WebMvcConfigurer`. This is deliberate: `WebMvcConfigurer`-based CORS mappings
only apply after a request has passed the security filter chain, which is too
late for preflight. Spring Security's `.cors(Customizer.withDefaults())` picks up
this bean instead.

`CORS_ALLOWED_ORIGINS` must match the frontend origin **exactly**. The deployed
frontend is `https://superdive-alpha.vercel.app`; local development is
`http://localhost:3000`.

> Do not add `@CrossOrigin` to controllers. Those annotations override the global
> config with a hardcoded origin and will silently break deployed environments.

## Running locally

**Requirements:** JDK 21, Docker (or a local MySQL on 3306).

### With Docker Compose (recommended)

```bash
cp .env.example .env.local     # fill in the values
docker compose --env-file .env.local up -d --build
```

Brings up MySQL and the backend together. Both host ports bind to `127.0.0.1`
only — nothing is reachable from outside the machine.

### Directly, against your own MySQL

```bash
./mvnw spring-boot:run          # Linux/macOS
.\mvnw.cmd spring-boot:run      # Windows
```

Runs under the `local` profile: MySQL at `localhost:3306/superdive`, user `root`,
a throwaway JWT secret, CORS open to `http://localhost:3000`. Override any of it
with the matching environment variable.

### Tests

```bash
./mvnw test
```

`BackendApplicationTests` is a `@SpringBootTest` that boots the whole context and
needs a reachable MySQL. Exclude it when you don't have one:

```bash
./mvnw test -Dtest='!BackendApplicationTests' -DfailIfNoTests=false
```

## Deployment

Production runs on a Hostinger VPS: Docker Compose (backend + MySQL, bound to
loopback) behind nginx, which terminates TLS and proxies to the app.

```
Internet → nginx :443 (Let's Encrypt)  →  127.0.0.1:8080 → backend container → mysql container
```

### Deploying

```bash
./deploy/deploy.sh prod    # pulls master, rebuilds, restarts
./deploy/deploy.sh dev     # pulls development, rebuilds, restarts
```

The script fetches, `git reset --hard origin/<branch>`, rebuilds the image,
restarts the stack and prunes orphaned layers. It expects a filled-in
`.env.<env>` next to `docker-compose.yml`.

**Deploys are manual.** Pushing to `master` does not update the VPS — you must
run the script. (The frontend, by contrast, deploys automatically via Vercel.)

### nginx

`deploy/nginx/api.conf` is the site template. Install one copy per environment:

```bash
sudo cp deploy/nginx/api.conf /etc/nginx/sites-available/superdive-prod
sudo ln -s /etc/nginx/sites-available/superdive-prod /etc/nginx/sites-enabled/
sudo nginx -t && sudo systemctl reload nginx
sudo certbot --nginx -d api.superdive-crm.tech
```

For the dev copy, change `server_name` to the dev host and `proxy_pass` to the
dev `APP_HOST_PORT`.

### Inspecting the production database

MySQL binds to `127.0.0.1` on the VPS and is not reachable from the internet.
From a shell on the VPS:

```bash
docker exec -it <project>-mysql-1 mysql -uroot -p<password> superdive
```

From a GUI client, tunnel over SSH: point the client at `127.0.0.1:<MYSQL_HOST_PORT>`
with the SSH tunnel targeting the VPS.

### Compose notes

- `COMPOSE_PROJECT_NAME` determines the volume name. **Changing it points the
  stack at a different (empty) volume** — the old data is orphaned, not deleted,
  but the app will look wiped. Keep it stable across redeploys.
- `MYSQL_ROOT_PASSWORD` only takes effect when the data volume is first
  initialised. Changing it later does not change the password stored in MySQL;
  it only breaks the backend's ability to connect. Rotate the password inside
  MySQL and the env file together.
- Database ports bind to `127.0.0.1` deliberately: a bare `3307:3306` would
  expose the database to the whole internet, and Docker's iptables rules bypass
  `ufw`.

## CI

`Jenkinsfile` defines a build/test pipeline: prepare → build → unit tests →
package → archive the jar. It runs on Linux or Windows agents via the Maven
wrapper, and needs a JDK installation named `jdk-21` on the controller.

Parameters: `RUN_DB_TESTS` (include the `@SpringBootTest`), `SKIP_TESTS`
(emergency builds), `DEPLOY`.

**The Deploy stage is a placeholder** — it echoes and deploys nothing. Jenkins is
optional; nothing in the deployment path depends on it.

## Known rough edges

Worth knowing before you extend this:

- **No schema migrations.** Every profile uses `ddl-auto=update`, which never
  drops or rewrites a column. A destructive schema change has to be done by hand.
  Flyway or Liquibase is the obvious next step.
- **Entities leak through the API.** `CustomerController` and `OrdersController`
  return `Customer`/`Orders` entities directly, so lazy-loading behaviour and
  entity field names become part of the public contract.
- **`/api/all-orders` is unpaginated** while `/api/all-customer` is paged.
- **`OrdersController` is annotated `@Controller`, not `@RestController`.** It
  works because every method returns `ResponseEntity`, but it is inconsistent
  with the rest of the codebase.
- **Route naming is inconsistent** — `/create-customer` and `/all-customer` sit
  alongside `/customers/{id}`; `/api/{ordersId}/items` has no resource prefix.
