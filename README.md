# food-store-service

A backend REST API service for managing food store orders. Built with Spring Boot and designed to serve three distinct user roles: Admin, Shop Owner, and Customer.

---

## Project Overview

This service powers a QR-based food ordering system where customers scan a QR code at their table to browse a shop's menu and place orders. Shop owners manage their products and handle incoming orders. Admins have full visibility and control over the entire platform.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.0 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | PostgreSQL |
| Migrations | Flyway |
| ORM | Spring Data JPA |
| Mapping | MapStruct 1.6.3 |
| Boilerplate | Lombok |
| Build | Maven |
| Container | Docker + Docker Compose |

---

## User Roles

### ADMIN
- Full access to all resources across the platform.
- Can manage all user accounts (shop owners and customers): create, update, activate/deactivate, delete.
- Can view and manage all shops, products, inventory, and orders.
- Responsible for onboarding new shop owners.

### SHOP_OWNER
- Manages one or more shops they own.
- **Shop management**: create, update, delete their shop(s).
- **Product management**: add, update, delete products within their shop. Products belong to categories (e.g., Food, Drink, Milk, Snack, etc.).
- **Inventory management**: track and update stock levels for each product.
- **Order management**: view and edit orders placed by customers at their shop (e.g., update order status, modify items if needed).

### CUSTOMER
- No account required; identified by QR code context.
- Scans a QR code placed on a table. The QR encodes the **shop ID** and **table/desk number**.
- After scanning, sees the menu (product list with categories) for that specific shop.
- Selects items and places an order. The order is automatically associated with the scanned desk number so the shop knows where to deliver.

---

## Core Domain Concepts

### User
Represents an authenticated user of the system (Admin or Shop Owner). Stored in the `users` table.
- Fields: `id`, `username`, `email`, `full_name`, `password` (hashed), `role`, `is_active`, `created_at`, `updated_at`, `created_by`, `updated_by`.
- Roles currently in DB: `USER`, `ADMIN`. The application will expand roles to include `SHOP_OWNER`.

### Shop
A food store registered on the platform, owned by a `SHOP_OWNER` user.
- Has a name, description, address, and operating status.
- Generates QR codes that encode `shopId` + `tableNumber`.

### Product
An item available for purchase in a shop.
- Belongs to a **Category** (Food, Drink, Milk, Snack, etc.).
- Has a name, description, price, image, and availability flag.

### Category
A classification for products within a shop (e.g., Food, Drink, Milk, Snack).

### Inventory
Tracks stock levels for each product in a shop.
- Shop owners can update inventory counts.
- Orders decrement inventory automatically.

### Order
Created when a customer places a request from a QR-scanned table.
- Linked to a `shopId` and `tableNumber` (from QR code).
- Contains one or more `OrderItem` entries (product + quantity + price snapshot).
- Has a status lifecycle: `PENDING → CONFIRMED → PREPARING → READY → SERVED → CANCELLED`.

### QR Code
Encodes a URL or token containing `shopId` and `tableNumber`. Customers scan this to open the menu for that specific shop and table without logging in.

---

## Package Structure

```
com.twochickendevs.foodstoreservice
├── auth/                  # User authentication (login, register, JWT refresh)
│   ├── controller/
│   ├── dto/
│   ├── entity/
│   ├── mapper/
│   ├── repository/
│   └── service/
├── security/              # JWT filter, Spring Security config, ApplicationConfig
├── common/                # Shared base entity, error DTOs, global exception handler, auditing config
└── FoodStoreServiceApplication.java
```

Future modules to be added: `shop/`, `product/`, `category/`, `inventory/`, `order/`, `qr/`.

---

## Authentication

JWT-based stateless authentication.
- `POST /api/auth/login` — returns `accessToken` + `refreshToken`.
- `POST /api/auth/refresh` — exchange refresh token for a new access token.
- `POST /api/auth/register` — create a new user (Admin only for shop owner creation; public for customers if needed).
- All protected endpoints require `Authorization: Bearer <accessToken>` header.

---

## Database Migrations

Flyway manages schema versioning. Migration files live at:
```
src/main/resources/db/migration/foodstore/
```
Naming convention: `V{major}.{minor}.{patch}.{sequence}__{description}.sql`

Example: `V0.00.00.1__create_users_table.sql`

---

## Running Locally

### Prerequisites
- Docker & Docker Compose
- Java 25
- Maven

### Start the database
```bash
docker-compose up -d
```

### Run the application
```bash
./mvnw spring-boot:run
```

### Configuration
Application config is in `src/main/resources/application.yml`. Key properties:
- `spring.datasource.*` — PostgreSQL connection
- `spring.flyway.*` — migration settings
- JWT secret and expiry settings

---

## Conventions for AI Assistants

- **Base entity**: All JPA entities should extend `BaseEntity` (provides `id`, `createdAt`, `updatedAt`, `createdBy`, `updatedBy` via Spring Data Auditing).
- **Mapper**: Use MapStruct for entity ↔ DTO conversion. Place mappers in the `mapper/` sub-package of each module.
- **DTOs**: Request and response objects live in the `dto/` sub-package. Name them `*Request` / `*Response`.
- **Error handling**: Throw domain exceptions and let `GlobalExceptionHandler` map them to `ErrorResponse`.
- **Role checks**: Use Spring Security method-level annotations (`@PreAuthorize`) to enforce role-based access.
- **No customer login**: Customer-facing endpoints (menu browsing, order placement) are public or token-free; the QR code context (shopId + tableNumber) is passed as a path or query parameter.
