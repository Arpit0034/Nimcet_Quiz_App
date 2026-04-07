# Quiz — Backend

A NIMCET exam preparation quiz backend built with Spring Boot 3, JWT authentication, MySQL, and Groq AI integration.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.5.11 |
| Language | Java 21 |
| Security | Spring Security + JWT (jjwt 0.11.5) |
| Database | MySQL |
| ORM | Spring Data JPA / Hibernate |
| AI | Groq API |
| PDF Extraction | Gemini API |
| Build Tool | Maven |
| Utilities | Lombok |

---

## Project Structure

```
src/main/java/com/nimcet/quiz/
├── QuizApplication.java
├── config/
│   ├── JacksonConfig.java
│   ├── JwtConfig.java
│   ├── JwtFilter.java
│   ├── SecurityConfig.java
│   └── TomcatConfig.java
├── controller/
│   ├── AIController.java
│   ├── AdminController.java
│   ├── AuthController.java
│   ├── BookmarkController.java
│   ├── BugReportController.java
│   ├── QuestionController.java
│   ├── ReportController.java
│   └── TestController.java
├── service/
│   ├── AIService.java
│   ├── QuestionService.java
│   └── TestService.java
├── model/
│   ├── Bookmark.java
│   ├── BugReport.java
│   ├── Question.java
│   ├── QuestionReport.java
│   ├── TestAttempt.java
│   ├── User.java
│   └── UserStats.java
├── repository/
│   ├── BookmarkRepository.java
│   ├── BugReportRepository.java
│   ├── QuestionReportRepository.java
│   ├── QuestionRepository.java
│   ├── TestAttemptRepository.java
│   └── UserRepository.java
└── dto/
    ├── AIRequestDTO.java
    ├── QuestionDTO.java
    └── TestResultDTO.java
```

---

## Configuration

All values are read from environment variables.

`src/main/resources/application.properties`

```properties
spring.application.name=quiz
server.port=8080

spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

admin.username=${ADMIN_USERNAME}
admin.password=${ADMIN_PASSWORD}

jwt.secret=${JWT_SECRET_KEY}
jwt.expiration=${JWT_EXPIRATION}

frontend.url=${FRONTEND_URL}

gemini.api.key=${GEMINI_API_KEY}
gemini.api.url=${GEMINI_API_URL}

groq.api.key=${GROQ_API_KEY}
groq.api.url=${GROQ_API_URL}
groq.model=${GROQ_MODEL}

server.max-http-request-header-size=${SERVER_MAX_HEADER}
```

### Required Environment Variables

| Variable | Description |
|---|---|
| `DATABASE_URL` | MySQL JDBC connection URL |
| `DATABASE_USERNAME` | MySQL username |
| `DATABASE_PASSWORD` | MySQL password |
| `ADMIN_USERNAME` | Admin login username |
| `ADMIN_PASSWORD` | Admin login password |
| `JWT_SECRET_KEY` | Secret key for signing JWTs |
| `JWT_EXPIRATION` | JWT expiration time in milliseconds |
| `FRONTEND_URL` | Allowed CORS origin |
| `GEMINI_API_KEY` | Gemini API key (used for PDF extraction) |
| `GEMINI_API_URL` | Gemini API endpoint URL |
| `GROQ_API_KEY` | Groq API key (used for AI features) |
| `GROQ_API_URL` | Groq API endpoint URL |
| `GROQ_MODEL` | Groq model name to use |
| `SERVER_MAX_HEADER` | Max HTTP request header size |

---

## Getting Started

### Prerequisites

- Java 21+
- Maven
- MySQL

### Steps

1. Create a MySQL database.

2. Set all required environment variables listed above.

3. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

Server starts on **http://localhost:8080**.

---

## API Endpoints

### Auth — `/api/auth`

Public.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/register` | Register a new student user |
| POST | `/api/auth/login` | Login and receive a JWT token |

### Questions — `/api/questions`

Public (GET only).

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/questions` | Get questions with optional filters: `subject`, `topic`, `year`, `difficulty`, `expectedSolveTime`, `count`, `mode` |
| GET | `/api/questions/{id}` | Get a question by ID |
| GET | `/api/questions/topics` | Get distinct topics, optionally filtered by `subject` |
| GET | `/api/questions/years` | Get distinct years |

### Test — `/api/test`

Requires `STUDENT` role.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/test/submit` | Submit a test attempt |
| GET | `/api/test/history?userId=` | Get test history for a user |
| GET | `/api/test/{id}/result` | Get a specific test attempt result |

**Scoring:** `score = (correct × 3) − incorrect`

### Bookmarks — `/api/bookmarks`

Requires `STUDENT` role.

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/bookmarks?userId=` | Get all bookmarks for a user |
| POST | `/api/bookmarks` | Add a bookmark (`userId`, `questionId` in body) |
| DELETE | `/api/bookmarks/{questionId}?userId=` | Remove a bookmark |

### AI — `/api/ai`

Requires `STUDENT` role. Powered by Groq API.

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/ai/explain` | Get a step-by-step explanation of a question |
| POST | `/api/ai/hint` | Get a short hint for a question |
| POST | `/api/ai/similar` | Generate a similar practice question |

All three accept `{ "questionId": <id> }` in the request body.

### Reports — `/api/reports`

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/reports` | `STUDENT` — Submit a question report |

### Bug Reports — `/api/bug-reports`

| Method | Endpoint | Access |
|---|---|---|
| POST | `/api/bug-reports` | Authenticated — Submit a bug report |

### Admin — `/api/admin`

| Method | Endpoint | Access | Description |
|---|---|---|---|
| POST | `/api/admin/login` | Public | Admin login, returns JWT |
| POST | `/api/admin/questions` | `ADMIN` | Add a question |
| PUT | `/api/admin/questions/{id}` | `ADMIN` | Update a question |
| DELETE | `/api/admin/questions/{id}` | `ADMIN` | Delete a question |
| POST | `/api/admin/questions/bulk` | `ADMIN` | Bulk import questions |
| GET | `/api/admin/stats` | `ADMIN` | Get stats: total questions, students, tests today, subject counts |
| GET | `/api/admin/students` | `ADMIN` | Get all users with role `STUDENT` |
| POST | `/api/admin/extract-pdf` | `ADMIN` | Extract text from a PDF via Gemini API (base64 input) |
| GET | `/api/admin/reports` | `ADMIN` | Get all question reports |
| POST | `/api/admin/reports/{id}/resolve` | `ADMIN` | Resolve a question report |
| GET | `/api/admin/bug-reports` | `ADMIN` | Get all bug reports |
| POST | `/api/admin/bug-reports/{id}/resolve` | `ADMIN` | Resolve a bug report |

---

## Security

- Stateless sessions (`SessionCreationPolicy.STATELESS`)
- JWT passed via `Authorization: Bearer <token>` header
- JWT contains `username` and `role` claims, signed with HS256
- BCrypt password encoding
- CORS allowed origins: `${FRONTEND_URL}` and `http://localhost:3000`
- CORS allowed methods: `GET`, `POST`, `PUT`, `DELETE`, `OPTIONS`

---

## Roles

| Role | Description |
|---|---|
| `STUDENT` | Registered user; assigned automatically on registration |
| `ADMIN` | Credentials set via `ADMIN_USERNAME` and `ADMIN_PASSWORD` env vars |

---

## Data Models

### Question
Fields: `id`, `questionText`, `optionA`, `optionB`, `optionC`, `optionD`, `correctAnswer` (A/B/C/D), `manualSolution`, `subject`, `topic`, `year`, `difficulty`, `expectedSolveTime`, `createdAt`

### TestAttempt
Fields: `id`, `userId`, `questionsJson`, `answersJson`, `score`, `correct`, `incorrect`, `skipped`, `timeTaken`, `subjectFilter`, `topicFilter`, `yearFilter`, `createdAt`

### User
Fields: `id`, `username`, `email`, `password`, `role`, `createdAt`

### Bookmark
Fields: `id`, `userId`, `questionId`, `createdAt`

### QuestionReport
Fields: `id`, `questionId`, `userId`, `username`, `reason`, `note`, `resolved`, `resolvedAction`, `createdAt`, `resolvedAt`

### BugReport
Fields: `id`, `userId`, `username`, `description`, `category`, `pageUrl`, `resolved`, `createdAt`, `resolvedAt`

### UserStats
Fields: `id`, `userId`, `streak`, `totalTimeSpent`, `accuracyHistory`, `lastActive`
