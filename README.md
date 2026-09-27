# Placement Readiness & Preparation Platform

A full-stack web application designed to help engineering students prepare systematically for campus placements and technical interviews targeting Java and Spring Boot Full-Stack Developer roles. The platform brings together curated Data Structures & Algorithms (DSA) practice, an automated 1-4-7 spaced repetition revision schedule, a transparent 5-factor ATS resume analyzer, and a placement readiness assessment into a unified workflow. Built using Spring Boot 3, React 19, MySQL 8, and Flyway.

---

## Quick Links

* **GitHub Repository**: [https://github.com/shyamsunderreddypolu/placement-prep-platform](https://github.com/shyamsunderreddypolu/placement-prep-platform)
* **Interactive Swagger UI**: `http://localhost:8080/swagger-ui/index.html` *(available when running locally)*
* **OpenAPI 3 JSON Spec**: `http://localhost:8080/v3/api-docs` *(available when running locally)*
* **Postman Collection**: [`placement_prep_postman_collection.json`](placement_prep_postman_collection.json)

---

## Application Previews

*(Screenshots can be captured when running the application locally)*

| Screen | Description |
|---|---|
| **Dashboard & Readiness Overview** | Displays the Placement Readiness Index (PRI), active solving streak, difficulty distribution chart, and the 1-4-7 revision queue. |
| **DSA Problem Repository** | Searchable and filterable problem catalogue by topic, difficulty, and algorithmic pattern with step-by-step visualizers. |
| **1-4-7 Revision Queue** | Real-time queue showing problems due for recall review today with adaptive feedback controls (`EASY`, `NEEDS_REVIEW`, `HARD`). |
| **ATS Resume Analyzer** | Side-by-side analysis of uploaded resumes against target job descriptions with transparent scoring breakdown and missing keyword extraction. |
| **Practice Submission History** | Chronological log of past problem attempts, personal solution notes, and completion statuses. |

---

## Problem

During college campus recruitment, students frequently juggle disconnected tools:
* Practicing problems on coding websites without tracking which algorithmic patterns were used.
* Suffering from the **forgetting curve**—solving a complex problem once and forgetting the core approach weeks later when the interview arrives.
* Lacking a measurable metric to know if they are truly ready for campus technical rounds.
* Submitting generic resumes to campus drives without verifying whether their technical skills match specific job descriptions.

---

## Solution

This platform consolidates the entire placement preparation lifecycle into a single workflow:
1. **Curated DSA Practice**: Filter coding problems by topic and algorithmic pattern, log practice attempts, and maintain daily solving streaks.
2. **1-4-7 Spaced Repetition**: Automatically schedules revision reminders (Day 1, 4, 7, 14, 30) for every solved problem so approaches stay fresh in memory.
3. **Placement Readiness Index (PRI)**: A deterministic scoring model that evaluates preparation across DSA volume, resume quality, consistency, and topic breadth.
4. **Transparent ATS Resume Analyzer**: Parses resumes and compares them against job descriptions using weighted scoring and synonym expansion without arbitrary minimum thresholds.

---

## Key Features

### 1. Authentication & Session Security
* **Dual-Token JWT Authentication**: Stateless JWT access tokens (15-minute lifespan) paired with cryptographically secure, rotatable Refresh Tokens (7-day lifespan) stored in MySQL.
* **Automatic Token Rotation**: Frontend Axios interceptor detects expired tokens, transparently requests fresh access tokens from `/api/auth/refresh`, and replays queued requests without interrupting the user.
* **Role-Based Access Control (RBAC)**: Distinct `ROLE_USER` and `ROLE_ADMIN` roles enforced at the controller layer using Spring Security `@PreAuthorize`.
* **Resource Ownership Verification**: Strict ownership checks prevent Insecure Direct Object References (IDOR); students can only view, download, or review their own resumes and submissions.
* **Centralized Error Handling**: `@RestControllerAdvice` translates domain exceptions into standard, predictable JSON error payloads.

### 2. DSA Practice & Pattern Repository
* **Curated Problem Library**: Seeded with core interview problems across Arrays, Strings, Linked Lists, Trees, Graphs, Dynamic Programming, Stack, Binary Search, and Heap.
* **Pattern-Based Tagging**: Problems are tagged with algorithmic patterns (e.g., Two Pointers, Sliding Window, Fast & Slow Pointers, DFS, BFS) to help students recognize underlying patterns rather than memorizing individual solutions.
* **Interactive Stepper Modals**: Visual stepper components for Arrays, Stacks, and Trees to help visualize data structure states.
* **Pagination Support**: Spring Data `Pageable` support (`?page=0&size=20`) on list endpoints to handle growing datasets efficiently.

### 3. Automated 1-4-7 Spaced Repetition Engine
* **Automatic Scheduling**: Upon marking a problem as `SOLVED`, the system automatically schedules the first review for `Day 1`.
* **Adaptive Interval Adjustments**: When reviewing a due problem, the student selects recall difficulty:
  * **EASY**: Promotes the problem to the next interval (Day 1 → Day 4 → Day 7 → Day 14 → Day 30).
  * **NEEDS_REVIEW**: Shortens the interval to 3 days to reinforce weak recall.
  * **HARD**: Resets the revision interval back to 1 day for urgent repetition.
* **Due Queue API**: `GET /api/revisions/due` returns all problems whose scheduled review date is on or before the current timestamp.

### 4. Deterministic Placement Readiness Engine
* Computes an aggregate score (0–100%) based on 4 weighted criteria:
  * **DSA Solve Ratio (35%)**: Problem completion count weighted by difficulty (Easy: 1.0, Medium: 2.0, Hard: 3.5).
  * **Resume Quality (25%)**: Verified presence of uploaded resume and ATS compatibility.
  * **Consistency & Streaks (20%)**: Active daily streak and recent submission consistency.
  * **Topic Breadth (20%)**: Coverage across essential interview topics to discourage over-practicing only one category.
* Outlines specific strengths, weaknesses, and actionable recommendations.

### 5. Transparent ATS Resume Evaluator
* **Text Extraction**: Uses Apache PDFBox to parse uploaded PDF resumes.
* **Transparent 5-Dimension Scoring**:
  * Keyword / Skill Match: 40%
  * Technical Breadth: 25%
  * Experience Keywords: 15%
  * Education Keywords: 10%
  * Project Indicators: 10%
* **Synonym Expansion**: Recognizes tech variations (e.g., `JS` → `JavaScript`, `K8s` → `Kubernetes`, `ReactJS` → `React`).
* **Regex Word Boundary Matching**: Enforces `\b` word boundaries to eliminate false positive matches (such as matching single letter "C" inside "CSS" or "React").
* **No Artificial Minimums**: Scores reflect genuine keyword matching (0–100%) without artificial score padding.

### 6. Secure Resume Storage
* **File Validation**: Validates file headers via magic bytes (`%PDF-` for PDF, `PK\x03\x04` for DOCX) and enforces a 5MB size limit.
* **UUID Sanitization**: Files are saved with random UUID names to prevent filename collisions and path traversal attacks.
* **Protected File Streaming**: Public static directory browsing is disabled; files are downloaded through an authenticated endpoint with ownership verification.

---

## System Architecture

```
+-----------------------------------------------------------------------+
|                          Frontend Layer                               |
|                  React 19 + Vite + React Router                       |
|           Axios HTTP Client with Automatic Token Refresh              |
+-----------------------------------+-----------------------------------+
                                    |
                           HTTPS / REST (JSON)
                           Bearer JWT Access Token
                                    |
                                    v
+-----------------------------------------------------------------------+
|                       Spring Boot 3 Backend                           |
|                                                                       |
|  [ Security Filter Chain ]                                            |
|   ├── JwtAuthenticationFilter (validates Bearer token & claims)       |
|   └── Role-Based Access Control (@PreAuthorize / Method Security)     |
|                                                                       |
|  [ Controller Layer ]                                                 |
|   ├── AuthController        (/api/auth)       -> Register, Login, Refresh|
|   ├── ProblemController     (/api/problems)   -> Catalog & Patterns   |
|   ├── SubmissionController  (/api/submissions)-> Practice Logging    |
|   ├── RevisionController    (/api/revisions)  -> 1-4-7 Due Queue      |
|   ├── ReadinessController   (/api/readiness)  -> Readiness Engine     |
|   ├── ResumeController      (/api/resumes)    -> Upload & Download    |
|   ├── AtsController         (/api/ats)        -> ATS Text Evaluator   |
|   └── DashboardController   (/api/dashboard)  -> User Analytics       |
|                                                                       |
|  [ Service Layer ]                                                    |
|   ├── AuthService           ├── ProblemService    ├── AtsScorerService|
|   ├── RevisionService       ├── ReadinessService  ├── ResumeService   |
|   ├── StreakService         ├── SubmissionService └── StorageService  |
|                                                                       |
|  [ Persistence Layer ]                                                |
|   ├── Spring Data JPA Repositories (JpaRepository + Pageable)         |
|   └── Hibernate ORM (Entity mapping, validation, B-Tree indexes)      |
+-----------------------------------+-----------------------------------+
                                    |
                           JDBC Connection Pool
                           Flyway Schema Migrations
                                    |
                                    v
+-----------------------------------------------------------------------+
|                        MySQL 8.0 Database                             |
|  Tables: users, problems, submissions, resumes, streaks, tokens       |
|  Indexes on: email, topic, difficulty, user_id, submitted_at          |
+-----------------------------------------------------------------------+
```

---

## Key Engineering Decisions

### Why Spring Boot 3?
Provides a robust, mature backend ecosystem with native dependency injection, declarative transactions (`@Transactional`), and tightly integrated Spring Security 6 for building maintainable REST APIs.

### Why Spring Data JPA / Hibernate?
Reduces boilerplate data access code through repository interfaces (`JpaRepository`), while enabling explicit query methods, pagination (`Pageable`), and index mapping via JPA annotations (`@Index`).

### Why Dual-Token JWT Architecture?
Stateless access tokens (15-minute expiration) eliminate the need for server-side HTTP session storage on every request. Storing rotatable refresh tokens in the database allows revoking access immediately upon logout or compromised credentials without sacrificing stateless API performance.

### Why Flyway Database Migrations?
Replaces brittle `hibernate.ddl-auto=update` with version-controlled SQL scripts (`V1` to `V6`). This guarantees that schema changes (tables, indexes, foreign keys) are applied deterministically and reproducibly across all environments.

### Why React 19 + Vite?
Vite provides sub-second development server startup and optimized bundle production using modern Rollup/ES module packaging. React's component model allows modular state handling for interactive visualizers, revision queues, and charts.

---

## Security Architecture

| Security Mechanism | Implementation Details |
|---|---|
| **Password Storage** | Passwords hashed using BCrypt before database persistence. |
| **Access Token Security** | Signed with HMAC-SHA256 (JJWT 0.11.5) with a 15-minute expiration window. |
| **Refresh Token Rotation** | Stored in MySQL with unique tokens; expired or revoked tokens are rejected. |
| **Role-Based Authorization** | Endpoints like problem creation (`POST /api/problems`) require `ROLE_ADMIN`. |
| **Ownership Isolation** | Enforced at the service layer to prevent IDOR on resumes and revision submissions. |
| **File Upload Validation** | Inspects magic bytes (`%PDF-`, `PK\x03\x04`), MIME types, and caps file size at 5MB. |
| **Path Traversal Defense** | Storage files use generated UUIDs; direct access to `/uploads/**` is blocked. |
| **Centralized Exceptions** | Handled via `@RestControllerAdvice` to avoid leaking stack traces in API responses. |

---

## Database Design

The relational schema is managed through versioned Flyway migrations (`src/main/resources/db/migration/`):

```
+---------------+        1:N        +-------------------+        N:1        +---------------+
|     users     |------------------<|    submissions    |>------------------|   problems    |
+---------------+                   +-------------------+                   +---------------+
| id (PK)       |                   | id (PK)           |                   | id (PK)       |
| name          |                   | user_id (FK)      |                   | title         |
| email (UQ)    |                   | problem_id (FK)   |                   | difficulty    |
| password_hash |                   | status            |                   | topic         |
| role          |                   | notes             |                   | pattern       |
| branch        |                   | submitted_at      |                   | link          |
| graduation_yr |                   | solved_at         |                   | created_at    |
| created_at    |                   | next_review_at    |                   +---------------+
+---------------+                   | review_count      |
        |                           | last_feedback     |
        | 1:N                       +-------------------+
        v
+-------------------+       +-------------------+       +-------------------+
|      resumes      |       |      streaks      |       |  refresh_tokens   |
+-------------------+       +-------------------+       +-------------------+
| id (PK)           |       | id (PK)           |       | id (PK)           |
| user_id (FK)      |       | user_id (FK, UQ)  |       | user_id (FK)      |
| file_name         |       | current_streak    |       | token (UQ)        |
| file_url          |       | longest_streak    |       | expiry_date       |
| uploaded_at       |       | last_active_date  |       | revoked           |
+-------------------+       +-------------------+       +-------------------+
```

### Applied Database Indexes
* `users`: `idx_user_email` on `email`
* `problems`: `idx_problem_topic` on `topic`, `idx_problem_difficulty` on `difficulty`
* `submissions`: `idx_submission_user` on `user_id`, `idx_submission_submitted_at` on `submitted_at`, `idx_submission_next_review` on `next_review_at`
* `resumes`: `idx_resume_user` on `user_id`, `idx_resume_uploaded_at` on `uploaded_at`
* `refresh_tokens`: `idx_refresh_token` on `token`

---

## Tech Stack

| Layer | Technology | Version | Purpose |
|---|---|---|---|
| **Backend Framework** | Spring Boot | `3.5.16` | Core enterprise REST API framework |
| **Language** | Java | `17` / `25` | Backend programming language |
| **Security** | Spring Security | `6.x` | Stateless authentication, authorization & filter chain |
| **Tokens** | JJWT | `0.11.5` | JWT generation, claim parsing & HMAC validation |
| **ORM & Persistence** | Spring Data JPA / Hibernate | `6.6.x` | Object-relational mapping & database repositories |
| **Database Migrations** | Flyway | `11.x` | Automated versioned SQL schema migrations |
| **Database** | MySQL | `8.0` | Primary relational database storage |
| **In-Memory Test DB** | H2 Database | `2.3.232` | Isolated test execution database |
| **Document Parsing** | Apache PDFBox | `2.0.30` | Text extraction from PDF resume documents |
| **API Documentation** | Springdoc OpenAPI UI | `2.8.5` | Interactive Swagger 3 documentation and console |
| **Frontend Framework** | React | `19.2.7` | UI component library |
| **Build Tool** | Vite | `8.1.1` | Frontend build tooling & dev server |
| **Routing** | React Router | `7.18.1` | Client-side routing and protected routes |
| **HTTP Client** | Axios | `1.18.1` | HTTP requests with automatic token refresh interceptor |
| **Charts** | Recharts | `3.9.2` | Data visualization for placement analytics |
| **Icons** | Lucide React | `1.25.0` | Clean UI icons |
| **Testing** | JUnit 5 & Mockito | `5.x` / `5.x` | Unit and mock-driven testing |
| **Integration Testing**| MockMvc & Spring Security Test | `6.x` | HTTP endpoint and authorization testing |
| **Containerization** | Docker & Docker Compose | `3.8 spec` | Multi-container packaging (Backend, Frontend, MySQL) |
| **Web Server** | Nginx | Alpine | Frontend production static file serving and reverse proxy |
| **CI/CD** | GitHub Actions | `v4` | Automated testing and build pipeline |

---

## Project Structure

```text
placement-prep-platform/
├── .github/
│   └── workflows/
│       └── ci.yml                     # GitHub Actions CI for Maven tests & React build
├── frontend/                          # React + Vite frontend application
│   ├── src/
│   │   ├── assets/                    # Static image assets
│   │   ├── components/                # Reusable components (Navbar, Modals, Visualizers)
│   │   ├── context/                   # AuthContext session state
│   │   ├── pages/                     # Dashboard, Problems, Submissions, Resumes, Login, Register
│   │   ├── services/                  # Axios instance (api.js), auth, problems, resumes
│   │   ├── App.jsx                    # Route configuration
│   │   └── index.css                  # Design system styles
│   ├── Dockerfile                     # Multi-stage Node/Nginx frontend container
│   ├── nginx.conf                     # Nginx SPA fallback and proxy routing
│   └── package.json
├── src/                               # Spring Boot backend application
│   ├── main/
│   │   ├── java/com/shyamsunder/placement_prep_platform/
│   │   │   ├── config/                # SecurityConfig, JwtService, OpenApiConfig, ExceptionHandler
│   │   │   ├── controller/            # REST Controllers (Auth, Problems, Submissions, etc.)
│   │   │   ├── dto/                   # Request/Response data transfer objects
│   │   │   ├── entity/                # JPA Entities (User, Problem, Submission, Resume, Streak)
│   │   │   ├── exception/             # Custom domain exceptions
│   │   │   ├── repository/            # Spring Data JPA repositories
│   │   │   └── service/               # Core business services (Auth, Revision, Readiness, ATS)
│   │   └── resources/
│   │       ├── db/migration/          # Flyway SQL migrations (V1__... to V6__...)
│   │       └── application.properties # Spring configuration with environment overrides
│   └── test/
│       ├── java/.../security/         # Ownership and RBAC MockMvc tests
│       ├── java/.../service/          # Service layer unit tests (Ats, Auth, Revision, Streak, etc.)
│       └── resources/                 # Test application.properties (H2 in-memory DB config)
├── docker-compose.yml                 # Multi-container orchestration (MySQL, Backend, Frontend)
├── Dockerfile                         # Multi-stage Eclipse Temurin Java build
├── placement_prep_postman_collection.json # Exported API test collection
└── pom.xml                            # Maven dependencies and build plugins
```

---

## REST API Reference

All secured endpoints require the `Authorization: Bearer <access_token>` header.

### Authentication Endpoints (`/api/auth`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/auth/register` | Public | Register new user profile; returns access token and refresh token |
| `POST` | `/api/auth/login` | Public | Authenticate user credentials; returns tokens and user role |
| `POST` | `/api/auth/refresh` | Public | Exchange valid refresh token for a new access token |
| `POST` | `/api/auth/logout` | Public | Revoke active refresh token in database |

### DSA Problem Endpoints (`/api/problems`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/problems` | Authenticated | Query problem repository; supports `topic`, `difficulty`, `pattern`, `page`, `size` |
| `POST` | `/api/problems` | Admin Only | Add a new coding challenge (`ROLE_ADMIN` required) |
| `GET` | `/api/problems/patterns` | Authenticated | Retrieve unique list of algorithmic patterns |

### Practice & Revision Endpoints (`/api/submissions` & `/api/revisions`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/submissions` | Authenticated | Log a problem attempt; updates streak and schedules 1-4-7 revision if solved |
| `GET` | `/api/submissions` | Authenticated | Fetch authenticated user's submission history (supports `page`, `size`) |
| `GET` | `/api/revisions/due` | Authenticated | Retrieve all problems scheduled for revision on or before today |
| `POST` | `/api/revisions/{id}/review` | Authenticated | Submit recall difficulty (`EASY`, `NEEDS_REVIEW`, `HARD`) with ownership check |

### Placement Readiness (`/api/readiness`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/readiness` | Authenticated | Calculates Placement Readiness Index (PRI) and 4-factor category breakdown |

### Resume & ATS Scoring (`/api/resumes` & `/api/ats`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/api/resumes/upload` | Authenticated | Upload PDF/DOCX resume with magic-byte validation (max 5MB) |
| `GET` | `/api/resumes` | Authenticated | List resumes uploaded by authenticated user (supports `page`, `size`) |
| `GET` | `/api/resumes/{id}/download` | Authenticated | Stream resume file with strict ownership validation |
| `POST` | `/api/ats/analyze` | Authenticated | Evaluate resume text against job description using 5-dimension model |

### Dashboard Analytics & Utility (`/api/dashboard` & `/api/hello`)
| Method | Endpoint | Access | Description |
|---|---|---|---|
| `GET` | `/api/dashboard/difficulty` | Authenticated | Count of solved problems grouped by EASY, MEDIUM, HARD |
| `GET` | `/api/dashboard/topic` | Authenticated | Count of solved problems grouped by DSA topic |
| `GET` | `/api/dashboard/streak` | Authenticated | Current streak, longest streak, and last active date |
| `GET` | `/api/hello` | Public | Health sanity check endpoint |

---

## Testing Suite

The repository includes **36 automated unit and integration tests** executing via JUnit 5, Mockito, and Spring MockMvc against an isolated in-memory H2 database:

```bash
mvn test
```

### Verified Test Breakdown (36 Tests Total)
* **`OwnershipSecurityTest` (6 tests)**:
  * Verifies non-owner gets `403 Forbidden` on `GET /api/resumes/{id}/download`.
  * Verifies `ROLE_USER` gets `403 Forbidden` on problem creation (`POST /api/problems`).
  * Verifies `ROLE_ADMIN` gets `201 Created` on problem creation.
  * Verifies non-owner gets `403 Forbidden` when attempting review submission.
  * Verifies non-existent refresh token returns `400 Bad Request`.
  * Verifies revoked refresh token returns `401 Unauthorized`.
* **`AtsScorerServiceTest` (4 tests)**: Verifies 5-dimension scoring weights, synonym normalization, zero-match behavior without artificial floors, and unauthorized resume access checks.
* **`AuthServiceTest` (4 tests)**: Verifies successful registration, duplicate email handling, credential authentication, and refresh token rotation.
* **`ProblemServiceTest` (4 tests)**: Verifies problem creation, pattern filtering, distinct pattern extraction, and `Pageable` pagination mapping.
* **`ReadinessServiceTest` (3 tests)**: Verifies PRI score calculations for new users, active users, and missing user validation.
* **`ResumeServiceTest` (4 tests)**: Verifies resume upload, empty file rejection, owner download access, and non-owner download rejection.
* **`RevisionServiceTest` (3 tests)**: Verifies due revision retrieval, 1-4-7 interval rescheduling on `EASY` feedback, and ownership isolation.
* **`StreakServiceTest` (4 tests)**: Verifies streak initialization, same-day duplicate handling, consecutive day increments, and streak resets when gaps exceed 1 day.
* **`SubmissionServiceTest` (3 tests)**: Verifies solved submission streak updates, invalid problem ID handling, and non-solved submissions ignoring streak increments.
* **`PlacementPrepPlatformApplicationTests` (1 test)**: Verifies Spring Boot ApplicationContext loads successfully.

---

## Getting Started

### Prerequisites
* **Java Development Kit (JDK)**: Version 17 or higher
* **Apache Maven**: Version 3.8+
* **Node.js**: Version 20+ with npm
* **MySQL**: Version 8.0+ *(or Docker)*

---

### Option 1: Docker Compose (Quickest Full-Stack Setup)
Run the complete multi-container setup (MySQL 8.0 + Spring Boot Backend + React/Nginx Frontend):

```bash
docker compose up --build
```

* **Frontend Web App**: `http://localhost`
* **Backend API**: `http://localhost:8080/api`
* **Swagger Documentation**: `http://localhost:8080/swagger-ui/index.html`

---

### Option 2: Local Manual Setup

#### 1. Database Configuration
Create the MySQL database schema:
```sql
CREATE DATABASE placement_prep CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

#### 2. Backend Setup
Navigate to the root directory and start the Spring Boot application:
```bash
# Run unit and integration tests
mvn test

# Start the Spring Boot backend (Flyway will automatically create and migrate tables)
mvn spring-boot:run
```
The backend starts on `http://localhost:8080`.

#### 3. Frontend Setup
In a separate terminal, navigate to the `frontend` directory:
```bash
cd frontend

# Install dependencies
npm install

# Start Vite development server
npm run dev
```
The frontend starts on `http://localhost:5173`.

---

## Environment Variables

The backend uses standard environment variables with safe development fallbacks defined in `application.properties`:

| Variable | Development Default | Description |
|---|---|---|
| `DB_HOST` | `localhost` | MySQL host address |
| `DB_PORT` | `3306` | MySQL port |
| `DB_NAME` | `placement_prep` | MySQL database name |
| `DB_USERNAME` | `root` | Database username |
| `DB_PASSWORD` | `root` | Database password *(override in production)* |
| `JWT_SECRET` | *(local 256-bit fallback key)* | Cryptographic secret for signing JWTs *(override with secure random secret)* |
| `JWT_EXPIRATION` | `900000` *(15 min)* | Access token validity in milliseconds |
| `JWT_REFRESH_EXPIRATION` | `604800000` *(7 days)* | Refresh token validity in milliseconds |
| `FILE_UPLOAD_DIR` | `uploads` | Local filesystem directory for uploaded resumes |
| `AWS_S3_ENABLED` | `false` | Set `true` if routing file uploads to AWS S3 |

> **Security Note**: Never commit real database passwords or production JWT secrets to version control. Set `JWT_SECRET`, `DB_USERNAME`, and `DB_PASSWORD` as system environment variables in production environments.

---

## Application User Flow

```
[ New Student ] ──> Register Account (/api/auth/register)
                         │
                         v
[ Login Screen ] ──> Authenticate (/api/auth/login) ──> Receives Access Token + Refresh Token
                         │
                         v
[ Dashboard View ] <─────────────────────────────────────────────────┐
   ├── View Placement Readiness Index (PRI)                          │
   ├── View Current & Longest Solving Streak                         │
   └── Inspect 1-4-7 Revision Queue                                  │
         │                                                           │
         ├──> [ Solve DSA Problem ] (/api/problems)                  │
         │       └── Log Submission (/api/submissions) ──────────────┤ (Updates streak & schedules Day 1 review)
         │                                                           │
         ├──> [ Review Due Problem ] (/api/revisions/due)            │
         │       └── Submit Recall Feedback (EASY / NEEDS_REVIEW / HARD) (Calculates next review interval)
         │                                                           │
         └──> [ Upload Resume ] (/api/resumes/upload)                │
                 └── Run ATS Analysis against Job Description ───────┘ (Updates ATS match score)
```

---

## Future Improvements

1. **Company-Specific Practice Tracks**: Categorize problems by recruitment drive patterns (e.g., Service-based vs. Product-based hiring tracks).
2. **Automated Email Revision Digests**: Scheduled daily email summaries of problems due for review using Spring Mail and Quartz scheduler.
3. **Redis Caching**: Cache distinct algorithm pattern lists and problem queries to minimize database read traffic.
4. **WebSocket Submission Updates**: Live updates for solving streaks and leaderboard rankings across student cohorts.
5. **Direct S3 Cloud Storage Profile**: Active AWS S3 storage provider implementation for scalable cloud resume hosting.

---

## Author

* **Shyam Sunder Reddy Polu**
* GitHub: [@shyamsunderreddypolu](https://github.com/shyamsunderreddypolu)
* Repository: [placement-prep-platform](https://github.com/shyamsunderreddypolu/placement-prep-platform)

---

## License

This project is open-source and available under the [MIT License](LICENSE).
