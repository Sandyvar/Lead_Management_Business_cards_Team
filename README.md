# Lead CRM Backend

A Spring Boot backend scaffold for lead management.

## Stack

- Java 17
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Jakarta Validation
- BCrypt password hashing
- H2 for local development
- Actuator health endpoints

## Architecture

```text
src
└── main
    ├── java/com/project/leadcrm
    │   ├── controller   REST APIs
    │   ├── service      Business logic and transaction boundaries
    │   ├── repository   JPA repositories
    │   ├── model        Entities and enums
    │   ├── dto          Request, response, and error contracts
    │   ├── config       Security, password, CORS, and app configuration
    │   └── util         Helpers
    └── resources
        ├── application.yml
        ├── application-prod.yml
        └── static/templates
```

## User Model

Users are persisted in the `users` table with:

- `fullName`
- `email`, unique and normalized to lowercase
- `passwordHash`, stored with BCrypt and never returned by API responses
- `role`: `ADMIN`, `MANAGER`, or `SALES_REP`
- `status`: `ACTIVE`, `INVITED`, or `DISABLED`
- `createdAt` and `updatedAt`

## Run Locally

```bash
./mvnw spring-boot:run
```

The API starts on `http://localhost:8080`.

Useful endpoints:

- `GET /actuator/health`
- `GET /api/leads`
- `POST /api/leads`
- `GET /api/leads/{id}`
- `PUT /api/leads/{id}`
- `DELETE /api/leads/{id}`
- `GET /api/v1/users`
- `POST /api/v1/users`
- `GET /api/v1/users/{id}`
- `PUT /api/v1/users/{id}`
- `DELETE /api/v1/users/{id}`

Example create request:

```bash
curl -X POST http://localhost:8080/api/leads \
  -H 'Content-Type: application/json' \
  -d '{
    "name": "Avery Stone",
    "email": "avery@example.com",
    "phone": "+15555550123",
    "company": "Acme",
    "status": "NEW",
    "notes": "Interested in enterprise plan"
  }'
```

Example user create request:

```bash
curl -X POST http://localhost:8080/api/v1/users \
  -H 'Content-Type: application/json' \
  -d '{
    "fullName": "Jordan Lee",
    "email": "jordan@example.com",
    "password": "password123",
    "role": "ADMIN",
    "status": "ACTIVE"
  }'
```

## Test

```bash
./mvnw test
```

## Production Configuration

Use the `prod` profile with these environment variables:

- `DATABASE_URL`
- `DATABASE_USERNAME`
- `DATABASE_PASSWORD`
- `PORT`
