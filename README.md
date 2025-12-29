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

## License

Specify your preferred license (e.g., MIT, Apache-2.0). If none is set yet, all rights reserved by default. For open collaboration, MIT is a good choice.

---

## FAQ

Q: Can I use PTMS with a mobile app?
A: Yes — the REST API is designed for mobile and web clients.

Q: Does PTMS support multi-tenant setups?
A: The current design is single-trainer-centric. Multi-tenant support is on the roadmap.


Q: What database migrations are used?
A: For now, schema creation may rely on `hibernate.ddl-auto`. Migration tooling (Flyway/Liquibase) is planned.

---

If you have questions, suggestions, or need help integrating PTMS, open an issue on the repository. Happy training and building!
