# Azure Exam Simulator Backend

Spring Boot 3.5 REST API for managing online exam sessions, question delivery, answer evaluation, and result tracking. Supports multiple-choice, ordering, and matching question types with real-time progress and resume capabilities.

## Tech Stack

| Component | Technology |
|-----------|-----------|
| Runtime | Java 21 |
| Framework | Spring Boot 3.5 |
| Database | PostgreSQL |
| Migrations | Flyway 11.7 |
| Storage | Azure Blob Storage |
| Build | Maven 3.9+ |
| Serialization | Jackson YAML |
| Data Access | Spring Data JPA + Lombok |

## Core Features

- **Session Management**: Create and retrieve exam sessions with configurable duration
- **Multi-Type Questions**: SINGLE_CHOICE, MULTIPLE_CHOICE, ORDERING, MATCHING
- **YAML-Based Question Bank**: Load exams from Azure Blob Storage YAML (e.g., `az-900.yml`)
- **Answer Persistence**: Save answers in real-time (stored as JSONB in PostgreSQL)
- **Auto-Scoring**: Calculate results on exam submission with pass/fail determination
- **Resume Support**: Track visited and flagged questions; resume incomplete exams
- **Progress Tracking**: Per-session progress percentage and answered count
- **Timer Service**: Automatic submission after exam duration expires
- **Exam History**: Query user exam attempts with scores and timestamps

## API Endpoints

### Exam Session Management (`/api/exams`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/start` | Create new exam session |
| GET | `/{sessionId}` | Get session details |
| POST | `/{sessionId}/submit` | Submit exam and get results |
| GET | `/{sessionId}/result` | Retrieve exam results and score |
| GET | `/{sessionId}/resume` | Resume incomplete exam with saved answers |
| GET | `/{sessionId}/progress` | Get answered/total question count |
| GET | `/{sessionId}/timer` | Get remaining time in seconds |

### Answer Management (`/api/exam-answers`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/` | Save user answer for a question |
| GET | `/session/{sessionId}` | Retrieve all answers for exam |

### Question State (`/api/exam-state`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/{sessionId}/{questionId}/flag` | Flag question for review |
| POST | `/{sessionId}/{questionId}/visit` | Mark question as visited |

### User History (`/api/users`)

| Method | Endpoint | Purpose |
|--------|----------|---------|
| GET | `/{userId}/exam-history` | Get all past exam attempts |

## Database Schema

### Core Tables

**`users`**
- `id` (UUID, PK) — User identifier
- `email` (VARCHAR) — User email address

**`exam_session`**
- `id` (UUID, PK) — Session identifier
- `user_id` (UUID, FK → users) — User taking exam
- `exam_code` (VARCHAR) — Exam type (e.g., "az-900")
- `status` (VARCHAR) — IN_PROGRESS or SUBMITTED
- `duration_minutes` (INT) — Exam time limit
- `start_time` (TIMESTAMP) — When exam started
- `end_time` (TIMESTAMP) — When exam must end

**`exam_answer`**
- `id` (UUID, PK) — Answer record identifier
- `session_id` (UUID, FK → exam_session) — Exam session reference
- `question_id` (VARCHAR) — Question identifier in exam
- `answer` (JSONB) — User's answer (format depends on question type)
- `created_at`, `updated_at` (TIMESTAMP) — Metadata

**`exam_result`**
- `id` (UUID, PK) — Result record identifier
- `session_id` (UUID, FK → exam_session, UNIQUE) — Session reference
- `score` (INT) — Percentage score
- `correct` (INT) — Number of correct answers
- `total` (INT) — Total questions
- `passed` (BOOLEAN) — Pass/fail status
- `submitted_at` (TIMESTAMP) — When exam was submitted

**`exam_question_state`**
- `id` (UUID, PK) — State record identifier
- `session_id` (UUID, FK → exam_session) — Session reference
- `question_id` (VARCHAR) — Question identifier
- `visited` (BOOLEAN) — Whether question has been viewed
- `flagged` (BOOLEAN) — Whether flagged for review

## Question Bank Format (YAML)

```yaml
examCode: az-900
title: Microsoft Azure Fundamentals
durationMinutes: 60

questions:
  - id: q1
    type: SINGLE_CHOICE
    text: "What is Azure?"
    options:
      - "Cloud platform by Microsoft"
      - "Programming language"
      - "Database system"
    correctAnswer: "Cloud platform by Microsoft"

  - id: q2
    type: MULTIPLE_CHOICE
    text: "Which are Azure services?"
    options:
      - "Virtual Machines"
      - "Azure Functions"
      - "Blob Storage"
    correctAnswers:
      - "Virtual Machines"
      - "Azure Functions"
      - "Blob Storage"

  - id: q3
    type: ORDERING
    text: "Order deployment steps"
    options:
      - "Select subscription"
      - "Choose VM image"
      - "Document review"
    correctOrder:
      - "Select subscription"
      - "Choose VM image"
      - "Document review"

  - id: q4
    type: MATCHING
    text: "Match services to categories"
    options:
      vm: "Virtual Machine"
      blob: "Blob Storage"
      vnet: "Virtual Network"
    correctMap:
      vm: "compute"
      blob: "storage"
      vnet: "networking"
```

## Prerequisites

- Java 21 (JDK)
- Maven 3.9+
- PostgreSQL 13+ server
- Azure Storage Account with container and SAS token

## Quick Start

### 1. Clone and Navigate

```bash
cd az-exam-simulator-backend
```

### 2. Set Environment Variables

#### Windows PowerShell
```powershell
$env:SPRING_DATASOURCE_URL = "jdbc:postgresql://localhost:5432/exams?sslmode=disable"
$env:SPRING_DATASOURCE_USERNAME = "postgres"
$env:SPRING_DATASOURCE_PASSWORD = "your-password"
$env:AZURE_STORAGE_BLOB_ENDPOINT = "https://<account>.blob.core.windows.net/"
$env:AZURE_STORAGE_SAS_TOKEN = "?se=2026-12-31T23:59:00Z&sp=rwdlac&..."
$env:AZURE_STORAGE_CONTAINER_NAME = "question-bank"
$env:SERVER_PORT = "8081"
```

#### Linux/macOS
```bash
export SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/exams"
export SPRING_DATASOURCE_USERNAME="postgres"
export SPRING_DATASOURCE_PASSWORD="your-password"
export AZURE_STORAGE_BLOB_ENDPOINT="https://<account>.blob.core.windows.net/"
export AZURE_STORAGE_SAS_TOKEN="?se=2026-12-31T23:59:00Z&sp=rwdlac&..."
export AZURE_STORAGE_CONTAINER_NAME="question-bank"
export SERVER_PORT="8081"
```

### 3. Build

```bash
mvn clean package
```

### 4. Run

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8081/api/`.

## Configuration Details

### Database Connection

- `SPRING_DATASOURCE_URL` — PostgreSQL JDBC URL (include `?sslmode=require` for Azure)
- `SPRING_DATASOURCE_USERNAME` — Database user
- `SPRING_DATASOURCE_PASSWORD` — Database password

### Azure Blob Storage

Provide **either** option:

**Option A: Connection String**
- `AZURE_STORAGE_CONNECTION_STRING` — Full connection string
- `AZURE_STORAGE_CONTAINER_NAME` — Container name (e.g., "question-bank")

**Option B: Endpoint + SAS Token** (recommended for managed identity scenarios)
- `AZURE_STORAGE_BLOB_ENDPOINT` — Blob endpoint (e.g., `https://account.blob.core.windows.net/`)
- `AZURE_STORAGE_SAS_TOKEN` — SAS token with read permissions
- `AZURE_STORAGE_CONTAINER_NAME` — Container name

### Application Settings

- `SERVER_PORT` — HTTP port (default: 8081)
- `MANAGEMENT_PORT` — Actuator/metrics port (default: 8081)

## Docker Build

```bash
docker build -t az-exam-simulator-backend:latest .
docker run -p 8081:8081 \
  -e SPRING_DATASOURCE_URL="jdbc:postgresql://db:5432/exams?sslmode=disable" \
  -e SPRING_DATASOURCE_USERNAME="postgres" \
  -e SPRING_DATASOURCE_PASSWORD="password" \
  -e AZURE_STORAGE_BLOB_ENDPOINT="https://<account>.blob.core.windows.net/" \
  -e AZURE_STORAGE_SAS_TOKEN="?..." \
  -e AZURE_STORAGE_CONTAINER_NAME="question-bank" \
  az-exam-simulator-backend:latest
```

## Project Structure

```
src/main/java/com/azexam/simulator/
├── controller/        — REST endpoints (4 controllers)
│   ├── ExamController
│   ├── ExamAnswerController
│   ├── QuestionController
│   └── UserController
├── service/           — Business logic (9 services)
│   ├── ExamSessionService
│   ├── ExamAnswerService
│   ├── ExamResultService
│   ├── ExamQueryService
│   ├── QuestionLoaderService
│   ├── BlobService
│   ├── AutoSubmitService
│   ├── QuestionLoaderService
│   └── scoring/       — Scoring strategies per question type
├── model/             — JPA entities (6 models)
│   ├── ExamSession
│   ├── ExamAnswer
│   ├── ExamResult
│   ├── ExamQuestionState
│   ├── User
│   └── yaml/          — YAML deserialization models
├── repository/        — Spring Data JPA repositories
├── dto/               — Request/response DTOs
└── exception/         — Custom exceptions
```

## Testing

Run tests with:
```bash
mvn test
```

Tests use H2 in-memory database and are configured via `src/test/resources/application-test.yml`.

## Health Check

Actuator endpoints:
```
GET http://localhost:8081/actuator/health
GET http://localhost:8081/actuator/metrics
```

## Troubleshooting

| Issue | Solution |
|-------|----------|
| Flyway migration error | Check `src/main/resources/db/migration/` SQL syntax; ensure database exists |
| Blob access denied | Verify SAS token has `read` (r) and `list` (l) permissions |
| Connection refused | Ensure PostgreSQL is running and `SPRING_DATASOURCE_URL` is correct |
| YAML parsing error | Validate YAML format in question bank; check indentation and quotes |

## License

Proprietary — Azure Exam Simulator Project

Defaults:

- Local development: `8081`
- Azure App Service should set `SERVER_PORT=8080`

## Running Locally

Build the project:

```powershell
mvn clean package
```

Run the application:

```powershell
mvn spring-boot:run
```

If you want to override the port:

```powershell
$env:SERVER_PORT="8080"
mvn spring-boot:run
```

## Testing

Run tests:

```powershell
mvn test
```

The test profile uses:

- H2 in-memory database
- Flyway disabled
- A test blob connection string defined in `src/test/resources/application-test.yml`

## Database

Flyway migrations live in `src/main/resources/db/migration`.

Current schema includes:

- `users`
- `exam_session`
- `exam_answer`
- `exam_result`

Seed data includes one test user:

- `11111111-1111-1111-1111-111111111111`

## Question Bank

Question content is stored as YAML and loaded from Azure Blob Storage. The repository also includes `question-bank/az-900.yml`, which is used as the source content for upload.

Example exam code currently present:

- `AZ-900`

## API

### Exam Sessions

- `POST /api/exam-sessions`
- `GET /api/exam-sessions/{id}`

Create session request body:

```json
{
  "examCode": "AZ-900",
  "userId": "11111111-1111-1111-1111-111111111111"
}
```

### Questions

- `GET /api/questions/session/{sessionId}`

### Answers

- `POST /api/exam-answers`
- `GET /api/exam-answers/session/{sessionId}`

### Exams

- `POST /api/exams/{sessionId}/submit`
- `GET /api/exams/{sessionId}/result`
- `GET /api/exams/{sessionId}/resume`
- `GET /api/exams/{sessionId}/progress`
- `GET /api/exams/{sessionId}/timer`

### Users

- `GET /api/users/{userId}/exam-history`

Supported query parameters for exam history:

- `page`
- `size`
- `passed`
- `examCode`

## Health Check

Actuator endpoints exposed by default:

- `/actuator/health`
- `/actuator/info`

## Docker

Build the jar first, then build the image:

```powershell
mvn clean package -DskipTests
docker build -t azexam-backend:local .
```

The container exposes port `8080`.

## Azure Notes

- App Service should provide datasource and blob storage settings through app settings
- App Service container runtime should use `SERVER_PORT=8080`
- PostgreSQL connections should use SSL with `sslmode=require`
- Question YAML files should exist in the `question-bank` blob container

## Known Dev Notes

- Local startup depends on reachable PostgreSQL unless overridden
- If App Service starts but exam endpoints fail, check blob storage settings first
- If Azure startup fails during Flyway initialization, check PostgreSQL firewall and network access

## TO DO:
- Update YAML model to include explanation `private String explanation`