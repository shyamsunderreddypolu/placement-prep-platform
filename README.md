# Placement Readiness & Preparation Platform

[![Java 17/25](https://img.shields.io/badge/Java-17%20%7C%2025-ED8B00?logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot 3.5](https://img.shields.io/badge/Spring%20Boot-3.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Spring Security 6](https://img.shields.io/badge/Spring%20Security-6-6DB33F?logo=springsecurity&logoColor=white)](https://spring.io/projects/spring-security)
[![React 19](https://img.shields.io/badge/React-19-61DAFB?logo=react&logoColor=black)](https://react.dev/)
[![MySQL 8.0](https://img.shields.io/badge/MySQL-8.0-4479A1?logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Flyway](https://img.shields.io/badge/Flyway-Migrations-CC0200?logo=flyway&logoColor=white)](https://flywaydb.org/)
[![Docker](https://img.shields.io/badge/Docker-Containerized-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![CI Pipeline](https://img.shields.io/badge/GitHub%20Actions-CI%20Passing-2088FF?logo=githubactions&logoColor=white)](https://github.com/shyamsunderreddypolu/placement-prep-platform/actions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

A production-grade, full-stack placement readiness platform designed to guide engineering students systematically through Data Structures & Algorithms (DSA), technical interview preparation, spaced repetition revision, and ATS resume optimization. Built with a robust **Spring Boot 3** enterprise backend, modern **React 19 + Vite** frontend, and containerized with **Docker & GitHub Actions CI/CD**.

---

## Table of Contents
- [System Architecture](#system-architecture)
- [Key Engineering Modules](#key-engineering-modules)
  - [1. Security & Authentication Foundation](#1-security--authentication-foundation)
  - [2. Spaced Repetition (1-4-7 Rule) Revision Engine](#2-spaced-repetition-1-4-7-rule-revision-engine)
  - [3. Multi-Dimensional Placement Readiness Index (PRI)](#3-multi-dimensional-placement-readiness-index-pri)
  - [4. Transparent, Weighted ATS Resume Evaluator](#4-transparent-weighted-ats-resume-evaluator)
  - [5. Interactive Algorithm Steppers & Core CS Flashcards](#5-interactive-algorithm-steppers--core-cs-flashcards)
  - [6. Database Migrations & Performance Indexing](#6-database-migrations--performance-indexing)
- [Tech Stack](#tech-stack)
- [Project Directory Structure](#project-directory-structure)
- [REST API Reference](#rest-api-reference)
- [Getting Started](#getting-started)
  - [Option 1: Single-Command Docker Deployment (Recommended)](#option-1-single-command-docker-deployment-recommended)
  - [Option 2: Bare-Metal Local Development Setup](#option-2-bare-metal-local-development-setup)
- [Automated Testing Suite](#automated-testing-suite)
- [Environment Configuration](#environment-configuration)
- [License](#license)

---

## System Architecture

```
                                      +------------------------------------+
                                      |         Web Browser Client         |
                                      |     (React 19, Vite, Recharts)     |
                                      +-----------------+------------------+
                                                        |
                                            HTTPS / REST (JWT Bearer)
                                                        |
                                                        v
+-------------------------------------------------------+-------------------------------------------------------+
|                                              Spring Boot 3 Backend                                           |
|                                                                                                       |
|   +-------------------+    +--------------------+    +--------------------+    +--------------------+        |
|   |  Security Layer   |    |   DSA & Patterns   |    |  1-4-7 Spaced Rep  |    | Placement Readin.  |        |
|   | (JWT + Refresh,   |    | (Curated practice, |    | (Adaptive recall   |    | (Deterministic     |        |
|   |  RBAC, Ownership) |    |  tagging, Stepper) |    |  due queue engine) |    |  multi-dim index)  |        |
|   +-------------------+    +--------------------+    +--------------------+    +--------------------+        |
|             |                        |                          |                        |                   |
|   +-------------------+    +--------------------+    +--------------------+    +--------------------+        |
|   | Resume Controller |    | ATS Scorer Engine  |    | Global Exceptions  |    | OpenAPI Swagger 3  |        |
|   | (Magic-byte check,|    | (5-dim weighted    |    | (Centralized REST  |    | (Interactive docs, |        |
|   |  private storage) |    |  transparent ATS)  |    |  error responses)  |    |  JWT Auth testing) |        |
|   +-------------------+    +--------------------+    +--------------------+    +--------------------+        |
+-------------------------------------------------------+-------------------------------------------------------+
                                                        |
                                      Spring Data JPA / Flyway Migrations
                                                        |
                                                        v
                                      +------------------------------------+
                                      |         MySQL 8.0 Database         |
                                      |  (Indexed tables, constraints)     |
                                      +------------------------------------+
```

---

## Key Engineering Modules

### 1. Security & Authentication Foundation
* **Dual-Token JWT Architecture**: Stateless JWT access tokens (15-minute expiration) paired with cryptographically secure, rotatable Refresh Tokens (7-day expiration) persisted in MySQL.
* **Role-Based Access Control (RBAC)**: `Role` enum (`ROLE_USER`, `ROLE_ADMIN`) with method-level authorization `@EnableMethodSecurity` and `@PreAuthorize("hasRole('ADMIN')")`.
* **Resource Ownership Isolation**: Strict resource ownership validation preventing IDOR (Insecure Direct Object Reference). Users can only access and review their own submissions and resumes.
* **Centralized Exception Handling**: `@RestControllerAdvice` translating application exceptions (`ResourceNotFoundException`, `ForbiddenException`, `UnauthorizedException`, `DuplicateResourceException`, `FileValidationException`, `BadRequestException`) into consistent, RFC-compliant JSON responses (`ErrorResponse`).
* **Axios Refresh Interceptor**: Frontend automatically catches `401 Unauthorized` responses, queues incoming requests, exchanges the refresh token at `/api/auth/refresh`, and seamlessly retries queued calls without disrupting user experience.

### 2. Spaced Repetition (1-4-7 Rule) Revision Engine
* Overcomes the Ebbinghaus Forgetting Curve by automatically scheduling spaced repetition reviews upon solving coding problems:
  * **Day 1**: Initial recall checkpoint.
  * **Day 4**: Intermediate consolidation checkpoint.
  * **Day 7**: Long-term retention checkpoint.
  * **Day 14 & Day 30**: Mastery maintenance.
* **Adaptive Recall Feedback**:
  * `EASY`: Advances interval to the next spaced repetition milestone (+7 days).
  * `NEEDS_REVIEW`: Shortens interval to reinforce understanding within 3 days.
  * `HARD`: Resets interval back to 1 day for urgent recall reinforcement.
* **Due Queue Engine**: Dedicated endpoint `GET /api/revisions/due` powering the interactive Dashboard Spaced Repetition Widget.

### 3. Multi-Dimensional Placement Readiness Index (PRI)
* Deterministic mathematical model providing real-time evaluation across 4 weighted placement preparation pillars:
  1. **DSA Solve Ratio (35%)**: Solved count weighted by difficulty (`Easy` = 1.0, `Medium` = 2.0, `Hard` = 3.5).
  2. **Resume ATS Quality (25%)**: Presence of verified resume and keyword alignment.
  3. **Consistency & Streaks (20%)**: Active solving streak and 7-day consistency metric.
  4. **Core Topic Breadth (20%)**: Topic coverage across fundamental placement categories (Arrays, Trees, Graphs, DP, Linked Lists, Stack, Strings).
* Generates clear visual feedback: Overall Score (0-100), Status Tier (`Ready for Tier-1`, `Competitive`, `Needs Preparation`), Strengths, Weaknesses, and Actionable Recommendations.

### 4. Transparent, Weighted ATS Resume Evaluator
* **No Fabricated Scores or Artificial Caps**: Replaced black-box minimum scores with a transparent, 5-dimension evaluation model:
  * **Keyword / Skill Match (40%)**: Normalized keyword overlap against job requirements.
  * **Technical Breadth (25%)**: Presence of core programming languages, databases, frameworks, and cloud tooling.
  * **Experience Keywords (15%)**: Action verbs, leadership terminology, and tenure markers.
  * **Education Keywords (10%)**: Degree designations, GPA/percentage mentions, and accredited university coursework.
  * **Project Indicators (10%)**: Production deployments, GitHub links, architecture keywords, and measurable metrics.
* **Synonym Normalization**: Integrated dictionary matching common variations (e.g., `JS` -> `JavaScript`, `K8s` -> `Kubernetes`, `ReactJS` -> `React`, `Postgres` -> `PostgreSQL`).
* **Regex Word Boundary Matching**: Enforces `\b` boundaries preventing false-positive substring matches (e.g., matching letter "C" inside "React").
* **Magic-Byte File Validation**: Inspects actual file signatures (PDF `%PDF-` and DOCX `PK\x03\x04`) preventing MIME spoofing.
* **Authenticated Streaming Download**: Public static `/uploads/**` access is disabled; downloads are mediated via `GET /api/resumes/{id}/download` with strict user ownership validation.

### 5. Interactive Algorithm Steppers & Core CS Flashcards
* **Two-Pointer Array Stepper**: Visualizes left and right pointers moving step-by-step to find pairs with target sum.
* **Stack Operation Stepper**: Interactive visual representation of push, pop, and top operations for bracket matching and monotonic stack problems.
* **Binary Tree Stepper**: Visualizes recursive traversal orders (In-Order, Pre-Order, Post-Order, and BFS Level-Order).
* **Core CS Subject Flashcards**: Curated interview question cards for Operating Systems, DBMS & SQL, Computer Networks, and System Design with topic filtering and flip animations.

### 6. Database Migrations & Performance Indexing
* **Flyway Versioned Migrations**: Automated database evolution via modular migration scripts (`V1__create_users.sql` through `V6__add_revision_tracking.sql`).
* **B-Tree Database Indexing**: Optimized query performance on high-frequency columns:
  * `problems`: Indexed by `topic`, `difficulty`.
  * `submissions`: Indexed by `user_id`, `submitted_at`, `next_review_at`.
  * `resumes`: Indexed by `user_id`, `uploaded_at`.
  * `refresh_tokens`: Indexed by unique `token`.
* **Spring Data Pagination**: Efficient `Pageable` pagination across `/api/problems`, `/api/submissions`, and `/api/resumes` with backward-compatible array fallback.

---

## Tech Stack

| Layer | Technologies |
|---|---|
| **Backend Framework** | Java 17 / 25, Spring Boot 3.5, Spring Security 6, Spring Data JPA, Hibernate ORM |
| **Database & Migrations** | MySQL 8.0, Flyway Migrations, H2 In-Memory (Test Suite) |
| **Security & Tokens** | JJWT (Java JWT) 0.11.5, BCrypt Password Hashing, Refresh Token Rotation |
| **Documentation** | OpenAPI 3, Swagger UI (`springdoc-openapi-starter-webmvc-ui` 2.8.5) |
| **Frontend Framework** | React 19, Vite, Recharts, Lucide React, Axios (with automatic refresh interceptors) |
| **Testing & Quality** | JUnit 5, Mockito, Spring Security Test, MockMvc (36 unit & integration tests) |
| **DevOps & Containers** | Docker, Docker Compose, Multi-stage builds, Nginx reverse proxy, GitHub Actions CI |

---

## Project Directory Structure

```text
placement-prep-platform/
├── .github/
│   └── workflows/
│       └── ci.yml                            # Full-stack GitHub Actions CI pipeline
├── frontend/                                 # React 19 + Vite frontend
│   ├── src/
│   │   ├── components/                       # Visualizer modals, Navbar, ProtectedRoute
│   │   ├── context/                          # AuthContext (session, tokens, roles)
│   │   ├── pages/                            # Dashboard, Problems, Submissions, Resumes, Flashcards
│   │   ├── services/                         # Axios client, Auth, Problems, Submissions, Resumes
│   │   ├── App.jsx                           # Application routing
│   │   └── index.css                         # Dark mode glassmorphism design system
│   ├── Dockerfile                            # Multi-stage frontend Docker build
│   └── nginx.conf                            # Nginx reverse proxy & SPA router
├── src/                                      # Spring Boot 3 backend
│   ├── main/
│   │   ├── java/com/shyamsunder/placement_prep_platform/
│   │   │   ├── config/                       # SecurityConfig, JwtService, OpenApiConfig, ExceptionHandler
│   │   │   ├── controller/                   # REST Controllers (Auth, Problem, Submission, Revision, etc.)
│   │   │   ├── dto/                          # Request payloads & response DTOs
│   │   │   ├── entity/                       # JPA Entities (User, Problem, Submission, Resume, Streak, etc.)
│   │   │   ├── exception/                    # Custom domain exceptions
│   │   │   ├── repository/                   # Spring Data JPA repositories with pagination
│   │   │   └── service/                      # Core business engines (ATS, Spaced Rep, Readiness, Auth)
│   │   └── resources/
│   │       ├── db/migration/                 # Flyway migrations V1 to V6
│   │       └── application.properties        # Externalized config with local fallbacks
│   └── test/                                 # 36 automated unit & MockMvc integration tests
├── Dockerfile                                # Multi-stage backend Docker build
├── docker-compose.yml                        # Single-command production container orchestration
├── pom.xml                                   # Maven dependencies & build plugins
└── README.md                                 # Project documentation
```

---

## REST API Reference

All secured endpoints require the `Authorization: Bearer <access_token>` header. Interactive Swagger UI console is accessible at `http://localhost:8080/swagger-ui/index.html`.

### Authentication & Token Management (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new student profile; returns JWT + Refresh Token |
| `POST` | `/api/auth/login` | Public | Authenticate user; returns JWT + Refresh Token |
| `POST` | `/api/auth/refresh` | Public | Rotate refresh token for a fresh access token |
| `POST` | `/api/auth/logout` | Public | Revoke active refresh token |

### DSA Practice Repository (`/api/problems`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/problems` | Authenticated | Query problems with optional `topic`, `difficulty`, `pattern`, `page`, `size` |
| `POST` | `/api/problems` | Admin Only | Create new problem (`ROLE_ADMIN` required) |
| `GET` | `/api/problems/patterns` | Authenticated | List all distinct algorithmic patterns |

### Submissions & Spaced Repetition (`/api/submissions` & `/api/revisions`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/submissions` | Authenticated | Log practice attempt; triggers streak update and 1-4-7 schedule |
| `GET` | `/api/submissions` | Authenticated | Retrieve user submission history (paginated) |
| `GET` | `/api/revisions/due` | Authenticated | Fetch problems currently due for 1-4-7 spaced revision |
| `POST` | `/api/revisions/{id}/review` | Authenticated | Submit recall difficulty (`EASY`, `NEEDS_REVIEW`, `HARD`) |

### Placement Readiness (`/api/readiness`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/readiness` | Authenticated | Compute deterministic Placement Readiness Index (PRI) and category scores |

### Resume Management & ATS Evaluator (`/api/resumes` & `/api/ats`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/resumes/upload` | Authenticated | Upload PDF/DOCX resume with magic-byte validation (max 5MB) |
| `GET` | `/api/resumes` | Authenticated | List uploaded resumes for authenticated user |
| `GET` | `/api/resumes/{id}/download` | Authenticated | Stream resume document with ownership verification |
| `POST` | `/api/ats/analyze` | Authenticated | Execute 5-dimension weighted ATS analysis against job description |

### Dashboard Analytics (`/api/dashboard`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/dashboard/difficulty` | Authenticated | Solved problem counts grouped by EASY, MEDIUM, HARD |
| `GET` | `/api/dashboard/topic` | Authenticated | Solved problem counts grouped by DSA topic |
| `GET` | `/api/dashboard/streak` | Authenticated | Current streak, longest streak, and last active date |

---

## Getting Started

### Prerequisites
* **Java**: JDK 17 or JDK 21+
* **Build Tool**: Maven 3.8+
* **Node.js**: Node 20+ and npm
* **Database**: MySQL 8.0+ (or Docker)

---

### Option 1: Single-Command Docker Deployment (Recommended)
Launch the entire application stack (MySQL 8.0 + Spring Boot 3 Backend + React 19 Nginx Frontend) with a single command:

```bash
docker compose up --build
```

#### Application Endpoints:
* **Frontend Web Application**: [http://localhost](http://localhost)
* **Backend REST API**: [http://localhost:8080/api](http://localhost:8080/api)
* **Interactive Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
* **MySQL Database**: `localhost:3306` (`placement_prep` schema)

---

### Option 2: Bare-Metal Local Development Setup

#### 1. Database Setup
Log into your local MySQL server and create the database schema:
```sql
CREATE DATABASE placement_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. Backend Setup
```bash
# Build and execute the test suite (36 tests)
mvn clean test

# Run Spring Boot backend locally (Flyway will automatically apply migrations V1-V6)
mvn spring-boot:run
```
The Spring Boot backend will start on `http://localhost:8080`.

#### 3. Frontend Setup
In a separate terminal window:
```bash
cd frontend

# Install dependencies
npm install

# Start Vite development server
npm run dev
```
The React frontend will be accessible at `http://localhost:5173`.

---

## Automated Testing Suite

The repository includes **36 automated unit and integration tests** guaranteeing stability across the security perimeter, domain logic, and persistence layers:

```bash
mvn test
```

### Test Coverage Highlights
* `OwnershipSecurityTest`: Comprehensive MockMvc integration tests verifying user isolation, role-based access control (`ROLE_ADMIN` vs `ROLE_USER`), resume download protection, and refresh token security.
* `AtsScorerServiceTest`: Verifies 5-dimension weighted scoring, synonym normalization, and regex boundary matching.
* `RevisionServiceTest`: Validates 1-4-7 spaced repetition calculation and ownership validation.
* `ReadinessServiceTest`: Tests deterministic PRI score computation and category weight balances.
* `AuthServiceTest`: Verifies user registration, credential authentication, JWT token generation, and token rotation.
* `ResumeServiceTest`: Validates magic-byte MIME checking, size constraints, and file handling.
* `StreakServiceTest`: Verifies active streak increments, streak freezes, and missed-day resets.

---

## Environment Configuration

All environment variables feature sensible defaults for local development:

| Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host address |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `placement_prep` | MySQL database schema name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password |
| `JWT_SECRET` | `404E63...` (256-bit key) | HMAC-SHA256 secret key for signing JWTs |
| `JWT_EXPIRATION` | `900000` (15 minutes) | Access token validity in milliseconds |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7 days) | Refresh token validity in milliseconds |
| `FILE_UPLOAD_DIR` | `uploads` | Local directory path for resume storage |
| `AWS_S3_ENABLED` | `false` | Enable Amazon S3 cloud storage integration |

---

## License
This project is licensed under the [MIT License](LICENSE).