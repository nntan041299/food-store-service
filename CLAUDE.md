# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

A Spring Boot REST API for a QR-based food ordering system. Three roles: `ADMIN` (full platform control), `SHOP_OWNER` (manages their shop(s), products, inventory, orders), `CUSTOMER` (no account — scans a QR code that encodes `shopId` + `tableNumber` to browse the menu and place an order).

Tech stack: Java 25, Spring Boot 4.0.0, Spring Security + JWT (jjwt 0.11.5), PostgreSQL, Flyway, Spring Data JPA, MapStruct 1.6.3, Lombok, Maven, Docker Compose.

Only the `auth` module is implemented so far. Future modules: `shop/`, `product/`, `category/`, `inventory/`, `order/`, `qr/` — follow the same package layout as `auth/`.

## Commands

```bash
docker-compose up -d        # start PostgreSQL
./mvnw spring-boot:run      # run the app (port 8080, context-path /food-store-service/api)
./mvnw test                 # run all tests
./mvnw test -Dtest=ClassName#methodName   # run a single test
./mvnw clean package        # build
```

There is no `mvnw`/`mvnw.cmd` wrapper script checked in (it was deleted) — use a locally installed Maven (`mvn`) if `./mvnw` is unavailable, or regenerate the wrapper with `mvn wrapper:wrapper`.

Required env vars (see `src/main/resources/application.yml`): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `JWT_ACCESS_EXPIRATION`, `JWT_REFRESH_EXPIRATION`, `ALLOWED_ORIGIN`.

## Architecture

Package root: `com.twochickendevs.foodstoreservice`

```
auth/        # login, register, JWT refresh, user CRUD
  controller/  dto/  entity/  mapper/  repository/  service/
security/    # JwtAuthFilter, SecurityConfig, ApplicationConfig (AuthenticationProvider/PasswordEncoder beans), JwtUtil
common/      # BaseEntity, ErrorResponse, GlobalExceptionHandler, AuditingConfig
```

Each future domain module (`shop/`, `product/`, etc.) should mirror the `auth/` package structure (`controller/dto/entity/mapper/repository/service`).

### Security
- Stateless JWT auth. `JwtAuthFilter` runs before `UsernamePasswordAuthenticationFilter` (see `SecurityConfig`).
- Only `/auth/**` is `permitAll()`; everything else requires authentication.
- `@EnableMethodSecurity` is on — use `@PreAuthorize` for role checks on individual endpoints.
- Customer-facing endpoints (menu browsing, order placement) are expected to be public/token-free, identified instead by `shopId`/`tableNumber` from the QR code — when adding these routes, also open them up in `SecurityConfig`.

### Persistence
- All JPA entities extend `common.entity.BaseEntity`, which provides `createdAt`/`updatedAt`/`createdBy`/`updatedBy` via Spring Data JPA Auditing (`AuditingConfig`, `@EntityListeners(AuditingEntityListener.class)`). Don't redeclare these fields on subclasses.
- `spring.jpa.hibernate.ddl-auto` is `none` — schema changes go through Flyway only.
- Flyway migrations live in `src/main/resources/db/migration/foodstore/`, naming convention `V{major}.{minor}.{patch}.{sequence}__{description}.sql` (e.g. `V0.00.00.1__create_users_table.sql`). Flyway history table is `flyway_schema_history_store`.

### Error handling
- `common.exception.GlobalExceptionHandler` is the single `@RestControllerAdvice`; it maps validation errors, `BadCredentialsException`, `UsernameNotFoundException`, `DisabledException`, `AccessDeniedException`, and a catch-all `Exception` to `ErrorResponse`. Add new domain exception mappings here rather than handling errors in controllers.

### Mapping
- Use MapStruct for entity ↔ DTO conversion (see `auth.mapper.UserMapper`). Place mappers in the `mapper/` sub-package of each module.

### DTO conventions
- Request/response objects live in each module's `dto/` package, named `*Request` / `*Response`.

## Authentication API

Base path note: `server.servlet.context-path` is `/food-store-service/api`, but `SecurityConfig` matches paths against `/auth/**` (post-context-path). Endpoints (see `AuthController`):
- `POST /auth/login` — returns access + refresh token (`TokenResponse`)
- `POST /auth/refresh` — exchange refresh token for new access token
- `POST /auth/register` — create a user
- Protected endpoints require `Authorization: Bearer <accessToken>`.
