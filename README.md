# AI-Powered Placement Prep Platform

A full-stack web application designed to help college students prepare for campus placement interviews (specifically targeting Java Full Stack and Spring Boot Developer roles). This platform provides a unified workspace with two core modules: a **DSA Practice Tracker** and an **AI-Powered Resume ATS Scorer**.

---

## 🚀 Key Modules

### 1. DSA Practice Tracker
* Log daily DSA problem submissions with status, difficulty, and personalized notes.
* Automatically compute and maintain solving streaks (current streak, longest streak).
* Dashboard statistics showing solved problems classified by topic and difficulty level using visual charts.

### 2. Resume ATS Scorer
* Upload resumes securely to AWS S3.
* Paste target job descriptions to analyze match levels.
* Compute keyword match scores and display dynamic lists of matched and missing keywords.

---

## 🛠️ Planned Tech Stack

### Backend
* **Language & Framework**: Java 17, Spring Boot 3
* **Security**: Spring Security 6 with stateless JWT authentication
* **ORM & Database**: Spring Data JPA, Hibernate, MySQL (Local development, AWS RDS for production)
* **Testing**: JUnit 5, Mockito
* **Build System**: Maven

### Frontend
* **Core**: React 18 (bootstrapped with Vite), React Router
* **Visualization**: Recharts (for dashboard graphs)
* **API Client**: Axios

### Cloud & DevOps
* **Storage**: AWS S3 (for resume documents)
* **Hosting**: AWS Elastic Beanstalk (backend API), Vercel or Netlify (frontend React)
* **CI/CD Pipeline**: GitHub Actions (automating `mvn test` builds on code changes)

---

## 📂 Project Structure

```text
placement-prep-platform/
│
├── frontend/                   # React Vite frontend application
│   ├── src/
│   │   ├── components/         # Reusable UI (Navbar, ProtectedRoute, LogSubmissionModal)
│   │   ├── context/            # AuthContext global session state
│   │   ├── pages/              # Login, Register, Problems, Dashboard, Submissions
│   │   ├── services/           # API handlers (api.js, authService, problemService, submissionService)
│   │   ├── App.jsx             # Routes definition
│   │   └── index.css           # Custom CSS and design tokens
│   └── package.json
│
└── src/                        # Spring Boot backend application
    ├── main/
    │   ├── java/com/shyamsunder/placement_prep_platform/
    │   │   ├── config/         # Security configs, JWT, Global Exceptions
    │   │   ├── controller/     # Controllers (Auth, Problems, Submissions, Dashboard)
    │   │   ├── dto/            # Payloads & response objects
    │   │   ├── entity/         # Database tables entities (User, Problem, Submission, Streak)
    │   │   ├── repository/     # Data JPA Repositories
    │   │   └── service/        # Core business service logic
    │   └── resources/
    │       └── application.properties
    └── test/                   # JUnit 5 & Mockito test suites
```

---

## 🔌 REST API Endpoints

All secured endpoints require the `Authorization: Bearer <token>` header.

### 🔑 Authentication
* `POST /api/auth/register` (Public): Onboards a new user.
* `POST /api/auth/login` (Public): Validates credentials and returns a JWT token.

### 🧪 Sanity Check
* `GET /api/hello` (Public): Verifies the Spring Boot server is alive and responding.

### 📚 DSA Problems Management
* `GET /api/problems` (Secured): Retrieves the list of problems. Supports filtering via optional query parameters:
  * `?topic=Arrays` (e.g., Arrays, Trees, Dynamic Programming)
  * `?difficulty=EASY` (values: `EASY`, `MEDIUM`, `HARD`)
* `POST /api/problems` (Secured - Admin Only): Adds a new DSA problem. Access restricted to emails ending in `@placementprep.com` or `admin@gmail.com`.

### 📝 Practice Submissions
* `POST /api/submissions` (Secured): Logs a submission (status: `SOLVED` or `ATTEMPTED`) with optional notes. Solved submissions automatically update and persist solving streaks.
* `GET /api/submissions` (Secured): Fetches the chronological submission history of the authenticated user.

### 📊 Analytics Dashboard
* `GET /api/dashboard/difficulty` (Secured): Retrieves solved problem counts grouped by difficulty level (EASY, MEDIUM, HARD).
* `GET /api/dashboard/topic` (Secured): Retrieves solved problem counts grouped by topic categories.

### 📄 Resume Management & Storage
* `POST /api/resumes/upload` (Secured): Uploads a student's resume file (`MultipartFile`). Uploads directly to AWS S3 if `aws.s3.enabled=true`, or falls back to local storage directory (`/uploads/`).
* `GET /api/resumes` (Secured): Retrieves all resumes uploaded by the authenticated student in descending order.
* `GET /uploads/{fileName}` (Public): Serves locally stored static resume documents when running in developer mode.

### 🤖 AI ATS Resume Scorer
* `POST /api/ats/analyze` (Secured): Evaluates a student's uploaded PDF resume against target job description keywords using Apache PDFBox text extraction. Returns an ATS match score percentage ($0-100\%$), matched skills list, missing keywords, and actionable recommendations.

---

## 💻 Setup & Installation (Local Development)

### Prerequisites
* JDK 17 or higher
* Maven 3.8+
* MySQL 5.7+ / 8.0+

### Database Setup
1. Log into your MySQL console:
   ```sql
   CREATE DATABASE placement_prep;
   ```

### Running the Backend
1. Clone the repository and navigate to the project directory.
2. Build the Maven project:
   ```bash
   mvn clean install
   ```
3. Run the Spring Boot application:
   ```bash
   mvn spring-boot:run
   ```

### Running the Frontend
1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Install npm dependencies:
   ```bash
   npm install
   ```
3. Run the Vite development server:
   ```bash
   npm run dev
   ```
