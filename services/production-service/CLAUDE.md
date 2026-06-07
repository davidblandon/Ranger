# Production Service

Spring Boot microservice for the Production domain. Part of a microservices project
(`rh-service`, `frontend`, `docker` live as siblings). **This file covers only this service.**

## Stack

- Java 21 (compiled with a newer JDK), Spring Boot 3.5, built with Maven (`pom.xml`).
- Spring Web (REST) + Spring Data JPA + Bean Validation. PostgreSQL at runtime; H2 for tests.
- API docs via springdoc OpenAPI at `/swagger-ui.html`.

## Architecture

Layered (clean) architecture. Dependencies point inward:
`controller → service → repository → domain`. The domain knows nothing about the outer layers.
DTOs cross the web boundary; entities never leave the service layer.

```
src/main/java/com/example/productionservice/
├── ProductionServiceApplication.java   @SpringBootApplication entry point
├── domain/<entity>/        JPA @Entity classes (Product, Material, Order, Batch, Partner)
├── repository/<entity>/    Spring Data interfaces (extend JpaRepository<T, Long>)
├── service/<entity>/       Service interfaces (business contract)
│   └── impl/               @Service implementations (business logic + @Transactional)
├── controller/<entity>/    @RestController REST endpoints (/api/<entities>)
├── dto/<entity>/           Request/response records (the API contract; ids, not entities)
├── mapper/<entity>/        @Component entity↔DTO mappers
└── exception/              Exception types + @RestControllerAdvice global handler
```

Each entity (`product`, `material`, `order`, `batch`, `partner`) has its own sub-package in
every layer. Follow this pattern when adding a new entity.

## Conventions

- **Interfaces define contracts; `impl/` holds implementations** (e.g. `ProductService` +
  `ProductServiceImpl`). Inject dependencies via constructor on `final` fields.
- **One class = one responsibility.** Controllers do HTTP mapping + `@Valid` only; services hold
  business logic and transactions; repositories only data access. Don't mix layers.
- **Services accept request DTOs and return response DTOs**, mapping inside the transaction
  (`open-in-view=false`, so never return lazy entities to controllers).
- Service CRUD method names: `createX / getXById / getAllX / updateX / deleteX` (ids are `Long`).
- Throw `NotFoundException` (404) / `BadRequestException` (400) instead of returning `null`;
  the global handler renders them as an `ApiError`.
- Validate input with `jakarta.validation` constraints on request DTOs.
- Naming: `PascalCase` classes, `camelCase` members, lowercase packages. Prefer clear over clever.

## Business rules (in the service layer)

- **Order**: `price = Σ product.price`; `benefice = Σ (product.price − product.cost)`. Stock is
  consumed (−1 per product) when an order is `COMPLETED` and restored when it leaves that state or
  is deleted; rejects completion when stock is insufficient.
- **Batch**: `cost = Σ material.cost`; receiving a batch (`create`) increases each linked material's
  stock by one.
- Referenced ids (`partnerId`, `productIds`, `materialIds`, `batchId`) are validated to exist.

## Build & run

```bash
mvn test                                 # unit + web + integration tests (run on H2, no DB needed)
mvn clean package                        # build the jar
docker compose -f ../../docker/docker-compose.yml up -d production-db   # start Postgres
mvn spring-boot:run                      # run against Postgres (DB_* env vars override defaults)
```

App listens on `8080` (Docker maps it to `8082`). Datasource defaults target a local Postgres and
are overridable via `DB_HOST/DB_PORT/DB_NAME/DB_USER/DB_PASSWORD`.

## Notes

- `target/` is build output — never edit `.class` files there.
- Tests live under `src/test/java/...` mirroring the main packages; the test profile
  (`application-test.properties`) uses in-memory H2 so tests need no running Postgres.
