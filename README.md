# Placement Readiness & Preparation Platform

A production-grade, full-stack placement readiness platform designed to guide engineering students systematically through Data Structures & Algorithms (DSA), technical interview preparation, spaced repetition revision, and ATS resume optimization. Built with a robust **Spring Boot 3** enterprise backend, modern **React 19 + Vite** frontend, and containerized with **Docker & GitHub Actions CI/CD**.

---

## 🏛️ System Architecture

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

## 🌟 Key Engineering Modules

### 1. Security & Authentication Foundation
* **Dual-Token JWT Architecture**: Stateless JWT access tokens (15-minute expiration) with cryptographically secure, rotatable Refresh Tokens (7-day expiration) stored in MySQL.
* **Role-Based Access Control (RBAC)**: `Role` enum (`ROLE_USER`, `ROLE_ADMIN`) with method-level authorization `@EnableMethodSecurity` and `@PreAuthorize("hasRole('ADMIN')")`.
* **Resource Ownership Isolation**: Strict resource ownership validation preventing IDOR (Insecure Direct Object Reference). Users can only access and review their own submissions and resumes.
* **Centralized Exception Handling**: `@RestControllerAdvice` translating application exceptions (`ResourceNotFoundException`, `ForbiddenException`, `UnauthorizedException`, `DuplicateResourceException`, `FileValidationException`, `BadRequestException`) into consistent, RFC-compliant JSON responses.

### 2. Spaced Repetition (1-4-7 Rule) Revision Engine
* Implements cognitive science-backed spaced repetition scheduling (`Day 1 -> Day 4 -> Day 7 -> Day 14 -> Day 30`) upon solving problems.
* Adaptive recall feedback (`EASY`, `NEEDS_REVIEW`, `HARD`):
  * **EASY**: Advances interval to the next spaced repetition milestone.
  * **NEEDS_REVIEW**: Shortens interval to reinforce understanding within 3 days.
  * **HARD**: Resets interval back to 1 day for urgent recall reinforcement.
* Dedicated **Due Queue** API (`GET /api/revisions/due`) and interactive Dashboard widget.

### 3. Multi-Dimensional Placement Readiness Index (PRI)
* Deterministic scoring engine evaluating candidates across 4 core placement dimensions:
  1. **DSA Solve Ratio (35%)**: Solved count weighted by difficulty (Easy: 1.0, Medium: 2.0, Hard: 3.5).
  2. **Resume ATS Quality (25%)**: Verified resume upload and keyword alignment.
  3. **Consistency & Streaks (20%)**: Active solving streak and 7-day consistency metric.
  4. **Core Topic Breadth (20%)**: Coverage across fundamental placement topics (Arrays, Trees, Graphs, DP, Linked Lists, Stack, Strings).
* Generates transparent strengths, weaknesses, and actionable next steps.

### 4. Transparent, Weighted ATS Resume Evaluator
* **No Artificial Minimums**: Replaced manufactured scoring with a transparent, defensible 5-dimension model:
  * Keyword / Skill Match: 40%
  * Technical Breadth: 25%
  * Experience Keywords: 15%
  * Education Keywords: 10%
  * Project Indicators: 10%
* **Synonym Dictionary**: Normalizes industry synonyms (e.g., `JS` -> `JavaScript`, `K8s` -> `Kubernetes`, `ReactJS` -> `React`).
* **Regex Word Boundary Matching**: Enforces strict word boundaries (`\b`) preventing false-positive matches (e.g., matching letter "C" inside "React").
* **Score Breakdown DTO**: Returns categorized score dimensions, matched skills, missing target keywords, and tailored improvement suggestions.

### 5. Secure Resume Management
* **Magic-Byte & MIME Validation**: Validates actual file headers (PDF `%PDF-` and DOCX `PK\x03\x04`) preventing extension spoofing.
* **UUID Storage & Directory Traversal Protection**: Renames files using `UUID.randomUUID()` and sanitizes path traversal characters.
* **Authenticated Streaming Download**: Public static access to `/uploads/**` is disabled. Resumes are downloaded through `GET /api/resumes/{id}/download` with strict user ownership verification.

### 6. Database Migrations & Performance Indexing
* **Flyway Versioned Migrations**: Automated schema evolution (`V1__create_users.sql` through `V6__add_revision_tracking.sql`).
* **B-Tree Database Indexing**: Optimized query performance on high-frequency columns (`problems(topic, difficulty)`, `submissions(user_id, submitted_at)`, `resumes(user_id, uploaded_at)`).
* **Spring Data Pagination**: Efficient `Pageable` pagination across `/api/problems`, `/api/submissions`, and `/api/resumes`.

---

## 🛠️ Tech Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 17 / 25, Spring Boot 3.5, Spring Security 6, Spring Data JPA, Hibernate ORM |
| **Database & Migrations** | MySQL 8.0, Flyway Migrations, H2 In-Memory (Test Suite) |
| **Security & Tokens** | JJWT (Java JWT) 0.11.5, BCrypt Password Hashing |
| **Documentation** | OpenAPI 3, Swagger UI (`springdoc-openapi-starter-webmvc-ui` 2.8.5) |
| **Frontend** | React 19, Vite, Recharts, Lucide Icons, Axios (with automatic JWT rotation) |
| **Testing** | JUnit 5, Mockito, Spring Security Test, MockMvc (36 unit & integration tests) |
| **DevOps & Containers** | Docker, Docker Compose, Multi-stage builds, Nginx reverse proxy, GitHub Actions CI |

---

## 📋 REST API Reference

All secured endpoints require the `Authorization: Bearer <access_token>` header. Interactive documentation and live test console are available at `/swagger-ui/index.html`.

### Authentication & Tokens (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new student profile; returns JWT + Refresh Token |
| `POST` | `/api/auth/login` | Public | Authenticate user; returns JWT + Refresh Token |
| `POST` | `/api/auth/refresh` | Public | Rotate refresh token for a fresh access token |
| `POST` | `/api/auth/logout` | Public | Revoke active refresh token |

### DSA Problems (`/api/problems`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/problems` | Authenticated | Query problem repository; supports `topic`, `difficulty`, `pattern`, `page`, `size` |
| `POST` | `/api/problems` | Admin Only | Add new practice problem (`ROLE_ADMIN` required) |
| `GET` | `/api/problems/patterns` | Authenticated | List unique algorithm patterns |

### Submissions & Revision (`/api/submissions` & `/api/revisions`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/submissions` | Authenticated | Log practice attempt; triggers streak update and 1-4-7 schedule |
| `GET` | `/api/submissions` | Authenticated | Retrieve user submission history (paginated) |
| `GET` | `/api/revisions/due` | Authenticated | Fetch problems currently due for 1-4-7 spaced revision |
| `POST` | `/api/revisions/{id}/review` | Authenticated | Submit recall difficulty (`EASY`, `NEEDS_REVIEW`, `HARD`) |

### Placement Readiness (`/api/readiness`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/readiness` | Authenticated | Calculate deterministic Placement Readiness Index (PRI) and breakdown |

### Resume & ATS Scoring (`/api/resumes` & `/api/ats`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/resumes/upload` | Authenticated | Upload PDF/DOCX resume with magic-byte validation (max 5MB) |
| `GET` | `/api/resumes` | Authenticated | List uploaded resumes for authenticated user |
| `GET` | `/api/resumes/{id}/download` | Authenticated | Stream resume document with ownership verification |
| `POST` | `/api/ats/analyze` | Authenticated | Run 5-dimension weighted ATS analysis against job description |

### Dashboard Analytics (`/api/dashboard`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/dashboard/difficulty` | Authenticated | Solved problem counts grouped by EASY, MEDIUM, HARD |
| `GET` | `/api/dashboard/topic` | Authenticated | Solved problem counts grouped by DSA topic |
| `GET` | `/api/dashboard/streak` | Authenticated | Current streak, longest streak, and last active date |

---

## 🚀 Getting Started

### Prerequisites
* **Java**: JDK 17 or JDK 21+
* **Build Tool**: Maven 3.8+
* **Node.js**: Node 20+ and npm
* **Database**: MySQL 8.0+ (or Docker)

### Option 1: Single-Command Docker Deployment (Recommended)
Deploy the full-stack system (MySQL + Spring Boot + React/Nginx) with Docker Compose:
```bash
docker compose up --build
```
* **Frontend Web Application**: [http://localhost](http://localhost)
* **Backend REST API**: [http://localhost:8080/api](http://localhost:8080/api)
* **Interactive Swagger UI**: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

### Option 2: Local Development Setup

#### 1. Database Setup
```sql
CREATE DATABASE placement_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. Backend Setup
```bash
# Build and execute all 36 tests
mvn clean test

# Run Spring Boot backend locally (Flyway will automatically execute migrations V1-V6)
mvn spring-boot:run
```
Backend starts on `http://localhost:8080`.

#### 3. Frontend Setup
```bash
cd frontend
npm install
npm run dev
```
Frontend development server starts on `http://localhost:5173`.

---

## 🧪 Automated Testing Suite

The project features a comprehensive test suite with **36 automated unit and integration tests** ensuring reliability across security, business logic, and database operations:

```bash
mvn test
```

### Key Test Coverage
* `OwnershipSecurityTest`: MockMvc integration tests verifying user isolation, role-based access control (`ROLE_ADMIN` vs `ROLE_USER`), resume download protection, and refresh token security.
* `AtsScorerServiceTest`: Verifies weighted 5-dimension ATS scoring, synonym matching, and boundary word detection.
* `RevisionServiceTest`: Verifies 1-4-7 spaced repetition interval updates and ownership validation.
* `ReadinessServiceTest`: Verifies Placement Readiness Index calculations and category weight distribution.
* `AuthServiceTest`: Verifies registration, login, JWT generation, and token rotation.
* `ResumeServiceTest`: Verifies magic-byte validation and storage behavior.
* `StreakServiceTest`: Verifies streak continuity, incrementation, and gap detection.

---

## ⚙️ Environment Configuration Reference

| Environment Variable | Default Value | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL database host |
| `DB_PORT` | `3306` | MySQL database port |
| `DB_NAME` | `placement_prep` | MySQL database schema name |
| `DB_USERNAME` | `root` | MySQL database username |
| `DB_PASSWORD` | `root` | MySQL database password |
| `JWT_SECRET` | `404E63...` (256-bit key) | Cryptographic secret for signing JWTs |
| `JWT_EXPIRATION` | `900000` (15 minutes) | Access token lifetime in milliseconds |
| `JWT_REFRESH_EXPIRATION` | `604800000` (7 days) | Refresh token lifetime in milliseconds |
| `FILE_UPLOAD_DIR` | `uploads` | Local disk directory for resume storage |
| `AWS_S3_ENABLED` | `false` | Enable cloud storage via AWS S3 |

---

## 📄 License
This project is licensed under the MIT License.