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

## 📂 Project Structure (Planned)

```text
placement-prep-platform/
│
├── .github/workflows/          # GitHub Actions workflows
│   └── maven.yml               # Automated test execution pipeline
│
├── frontend/                   # React frontend application
│
└── src/                        # Spring Boot backend application
    ├── main/
    │   ├── java/com/shyamsunder/placement_prep_platform/
    │   │   ├── config/         # Security configs, JWT, app filters
    │   │   ├── controller/     # API Endpoints (Auth, DSA, Resume)
    │   │   ├── dto/            # Data Transfer Objects
    │   │   ├── entity/         # Database JPA Entities
    │   │   ├── repository/     # Database CRUD Repositories
    │   │   └── service/        # Core Business Logic Services
    │   └── resources/
    │       └── application.properties
    └── test/                   # JUnit 5 & Mockito test suites
```

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
