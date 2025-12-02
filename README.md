# PTMS — Personal Trainer Management System

A robust, secure, and modular backend built to streamline the operations of personal trainers. PTMS provides a clean REST API for managing clients, workouts, programs, progress tracking, and scheduling — all powered by Spring Boot, JPA/Hibernate, and PostgreSQL.

- Tech Stack: Spring Boot • JPA/Hibernate • PostgreSQL
- Architecture: Clean REST API • Layered Services • DTOs • Validation • Secure Auth
- Status: Actively developed — contributions welcome!

---

## Table of Contents
- Overview
- Key Features
- Who Is It For?
- Architecture & Design
- Getting Started
  - Prerequisites
  - Quick Start
  - Configuration
- API Overview
- Data Model (High-Level)
- Security & Authentication
- Deployment
- Development Workflow
- Testing
- Observability & Logging
- Roadmap
- Contributing
- License
- FAQ

---

## Overview

PTMS is a backend system designed to help personal trainers manage day-to-day operations:
- Create and manage client profiles
- Build structured workout programs
- Track sessions, progress, and metrics
- Manage schedules and availability
- Maintain secure, role-based access to resources

With a clean and opinionated codebase, PTMS is built for scalability, maintainability, and future expansion (e.g., payments, analytics, and client-facing portals).

---

## Key Features

- Client Management: Profiles, goals, health notes, contact info
- Workouts & Programs: Exercises, sets/reps, templates, progressions
- Session Tracking: Completed workouts, performance logs, notes
- Scheduling: Availability windows, session bookings
- Secure Auth: Token-based authentication, role-based access control
- Validation & DTOs: Clear API contracts and input validation
- Persistence: PostgreSQL with JPA/Hibernate
- Extensible: Modular services ready for new features (e.g., nutrition)

---

## Who Is It For?

- Freelance personal trainers managing multiple clients
- Boutique gyms and coaching teams needing backend infrastructure
- Developers building fitness platforms (mobile/web) who need a reliable API
- Educational projects showcasing modern Spring Boot architecture

---

## Architecture & Design

- Layered Architecture:
  - Controllers: REST endpoints and request/response mapping
  - Services: Business logic and orchestration
  - Repositories: Data access via JPA/Hibernate
  - Domain: Entities and value objects
  - DTOs & Mappers: Clean contracts and decoupling
- Error Handling: Global exception handling with consistent error responses
- Validation: Bean Validation (JSR-380)
- Security: Spring Security with token-based auth
- Configuration: Externalized via environment variables

---

## Getting Started

### Prerequisites
- Java 17+
- Maven or Gradle
- PostgreSQL 14+ (local or hosted)
- A terminal and cURL/Postman/HTTP client

### Quick Start

1) Clone:
```
git clone https://github.com/MarekMaro8/PTMS.git
cd PTMS
```

2) Configure your environment:
Create a `.env` or set environment variables:
```
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/ptms
SPRING_DATASOURCE_USERNAME=ptms_user
SPRING_DATASOURCE_PASSWORD=ptms_password
SPRING_JPA_HIBERNATE_DDL_AUTO=update
PTMS_JWT_SECRET=change_me_to_a_strong_secret
PTMS_JWT_EXPIRY_MINUTES=60
```

3) Run:
- With Maven:
```
./mvnw spring-boot:run
```
- Or with Gradle:
```
./gradlew bootRun
```

4) Verify:
```
curl -s http://localhost:8080/actuator/health
```
Expected: `{"status":"UP"}`

### Configuration

Common Spring Boot properties (typically in `application.yml` / `application.properties` or environment variables):
- SPRING_DATASOURCE_URL
- SPRING_DATASOURCE_USERNAME
- SPRING_DATASOURCE_PASSWORD
- SPRING_JPA_HIBERNATE_DDL_AUTO
- SERVER_PORT
- PTMS_JWT_SECRET
- PTMS_JWT_EXPIRY_MINUTES

---

## API Overview

Base URL: `http://localhost:8080/api/v1`

Auth:
- POST `/auth/register` — create trainer account
- POST `/auth/login` — obtain JWT token
- Use `Authorization: Bearer <token>` on subsequent requests

Clients:
- GET `/clients` — list clients
- POST `/clients` — create client
- GET `/clients/{id}` — retrieve client
- PUT `/clients/{id}` — update client
- DELETE `/clients/{id}` — delete client

Programs & Workouts:
- GET `/programs` — list programs
- POST `/programs` — create program
- GET `/programs/{id}` — retrieve program
- POST `/workouts` — create workout
- GET `/workouts/{id}` — get workout

Sessions & Tracking:
- POST `/sessions` — schedule or log a session
- GET `/sessions?clientId={id}` — client sessions
- PATCH `/sessions/{id}/complete` — mark as completed with performance data

Scheduling:
- GET `/availability` — trainer availability
- POST `/availability` — set availability windows

Error Model:
```
{
  "timestamp": "2025-12-02T12:34:56Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed for field 'email'",
  "path": "/api/v1/clients"
}
```

---

## Data Model (High-Level)

- Trainer: id, email, passwordHash, name, roles
- Client: id, trainerId, name, contact, goals, notes
- Program: id, trainerId, clientId, title, phases
- Workout: id, programId, title, exercises[]
- Exercise: id, workoutId, name, sets, reps, load, tempo, notes
- Session: id, clientId, scheduledAt, completedAt, notes, metrics
- Availability: id, trainerId, dayOfWeek, startTime, endTime

Note: Actual entity fields may vary; see codebase for canonical definitions.

---

## Security & Authentication

- Spring Security with JWT bearer tokens
- Role-based access control (e.g., TRAINER, ADMIN)
- Passwords stored using secure hashing
- Tokens signed with `PTMS_JWT_SECRET`
- HTTP-only behavior enforced at client integration level
- CORS configured for client applications (adjust as needed)

Best Practices:
- Use strong secrets in production
- Keep token lifetimes reasonable; rotate if needed
- Restrict sensitive endpoints by role

---

## Deployment

Options:
- Dockerize the application and database
- Deploy to cloud providers (Render, Fly.io, Azure, AWS, GCP)
- Use managed PostgreSQL (e.g., Supabase, RDS)

Environment:
- Set appropriate `SPRING_PROFILES_ACTIVE` (e.g., `prod`)
- Externalize secrets via environment variables or vaults
- Enable database migrations (Flyway/Liquibase) for production

Health & Ops:
- Expose `/actuator/*` endpoints for health, metrics, info
- Integrate logging with your platform (e.g., CloudWatch, ELK, Loki)

---

## Development Workflow

- Branching: feature branches per change; PRs for review
- Code Style: follow standard Java conventions and Spring guidelines
- DTOs & Mappers: ensure controller inputs/outputs remain stable
- Testing: unit tests for services, integration tests for persistence and controllers
- Documentation: keep README and API docs up to date

Recommended Tools:
- Lombok (if used), MapStruct (optional), Testcontainers (for DB tests)
- Postman collections or REST Client files for endpoint testing

---

## Testing

- Unit Tests: Service layer logic
- Integration Tests: Repository + controller with embedded or Testcontainers PostgreSQL
- Security Tests: Auth flows and RBAC constraints
- CI: Add a GitHub Actions workflow to run tests on push/PR

Example Maven:
```
./mvnw test
```

---

## Observability & Logging

- Structured logs (JSON-friendly) with contextual information
- Actuator: health, metrics, info
- Optional integrations: OpenTelemetry, Prometheus, Grafana

---

## Roadmap

- Multi-trainer teams and permissions
- Client-facing portal / mobile integration
- Payment integration and invoices
- Advanced analytics (progress trends, adherence)
- Nutrition module and habit tracking
- Internationalization (i18n)
- Flyway/Liquibase migrations
- API versioning strategy and OpenAPI documentation

---

## Contributing

Contributions are welcome! To propose changes:
1) Open an issue describing your enhancement or bug
2) Fork and create a feature branch
3) Add tests and update documentation
4) Submit a pull request

Please follow code style and keep controllers thin with business logic in services.

---

## License

Specify your preferred license (e.g., MIT, Apache-2.0). If none is set yet, all rights reserved by default. For open collaboration, MIT is a good choice.

---

## FAQ

Q: Can I use PTMS with a mobile app?
A: Yes — the REST API is designed for mobile and web clients.

Q: Does PTMS support multi-tenant setups?
A: The current design is single-trainer-centric. Multi-tenant support is on the roadmap.

Q: How do I add new endpoints?
A: Create a controller, service, DTOs, and repository where needed. Add validation, tests, and update the README/API docs.

Q: What database migrations are used?
A: For now, schema creation may rely on `hibernate.ddl-auto`. Migration tooling (Flyway/Liquibase) is planned.

---

If you have questions, suggestions, or need help integrating PTMS, open an issue on the repository. Happy training and building!
