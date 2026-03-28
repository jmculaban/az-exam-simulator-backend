# Azure Exam Simulator Backend

Spring Boot backend for the Azure Exam Simulator. The service manages exam sessions, question delivery, answer persistence, scoring, resume/progress queries, and user exam history.

## Stack

- Java 21
- Spring Boot 3.5
- Spring Web
- Spring Data JPA
- Flyway
- PostgreSQL
- Azure Blob Storage
- Maven

## What It Does

- Creates exam sessions for a user and exam code
- Loads question sets from Azure Blob Storage YAML files
- Saves answers during an exam session
- Calculates and returns exam results
- Supports resume, progress, timer, and user exam history queries

## Project Layout

```text
az-exam-simulator-backend/
├── question-bank/
│   └── az-900.yml
├── src/
│   ├── main/
│   │   ├── java/com/azexam/simulator/
│   │   │   ├── controller/
│   │   │   ├── dto/
│   │   │   ├── model/
│   │   │   ├── repository/
│   │   │   └── service/
│   │   └── resources/
│   │       ├── application.yaml
│   │       └── db/migration/
│   └── test/
│       ├── java/com/azexam/simulator/
│       └── resources/application-test.yml
├── Dockerfile
├── pom.xml
└── README.md
```

## Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL reachable from your runtime environment
- Azure Blob Storage container containing exam YAML files

## Configuration

The application supports Spring property binding from environment variables. For local development and Azure App Service, prefer environment variables instead of hardcoding values.

### Required Database Settings

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`

Example:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://<host>:5432/postgres?sslmode=require"
$env:SPRING_DATASOURCE_USERNAME="adminuser"
$env:SPRING_DATASOURCE_PASSWORD="your-password"
```

### Required Blob Storage Settings

Use one of these options.

Option 1: Connection string

- `AZURE_STORAGE_CONNECTION_STRING`
- `AZURE_STORAGE_CONTAINER_NAME`

Option 2: Blob endpoint plus SAS token

- `AZURE_STORAGE_BLOB_ENDPOINT`
- `AZURE_STORAGE_SAS_TOKEN`
- `AZURE_STORAGE_CONTAINER_NAME`

Example:

```powershell
$env:AZURE_STORAGE_BLOB_ENDPOINT="https://<account>.blob.core.windows.net/"
$env:AZURE_STORAGE_SAS_TOKEN="?<sas-token>"
$env:AZURE_STORAGE_CONTAINER_NAME="question-bank"
```

### Server Port

- `SERVER_PORT`

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