# DNA Sequence Matcher and Alignment Tool

Production-style full-stack bioinformatics platform for DNA alignment, learning, and reporting.

## About This Project

This project compares DNA sequences using dynamic programming algorithms and gives both **educational visualization** and **real-world workflow features**.

It supports:
- Global alignment (**Needleman-Wunsch**) for end-to-end comparison.
- Local alignment (**Smith-Waterman**) for high-similarity subsequence discovery.
- Saved analysis history with authentication and database persistence.
- FASTA batch processing for multi-sequence reports.

## Live Demo

- Demo URL: **`<ADD_YOUR_DEPLOYED_LINK_HERE>`**
- Local URL: [http://localhost:8080](http://localhost:8080)

> After deployment, replace the placeholder above with your actual public URL.

## Core Features

### Alignment Engine
- Global and Local alignment with customizable `match`, `mismatch`, `gap` scoring.
- Traceback path reconstruction.
- Identity percentage and score metrics.
- Matrix heatmap with optional traceback highlighting.

### Advanced Frontend UX
- Side-by-side sequence editor with live position rulers.
- Live sequence stats (`length`, `GC%`) and nucleotide sanitization.
- Theme switcher (dark/light), quick presets, and tabbed result views.
- Traceback playback animation (`play`, `pause`, `step`, `reset`).
- Copy alignment and export JSON report.

### Auth + Persistence (DB)
- User registration, login, logout, and profile (`/api/auth/*`).
- Token-based authenticated API access.
- Per-user saved analysis history from database.

### FASTA + Batch Reporting
- Upload `.fasta` / `.fa` / `.txt`.
- Uses first FASTA record as reference sequence.
- Compares reference against all remaining entries.
- Returns score, identity, and aligned output per target sequence.

## Tech Stack Used

- **Backend**: Java 17, Spring Boot 3, Spring Web, Spring Validation
- **Data Layer**: Spring Data JPA, H2 Database (file-based)
- **Security**: Token session model + BCrypt password hashing
- **Frontend**: HTML5, CSS3, Vanilla JavaScript
- **Build Tool**: Maven

## Project Structure

```text
src/main/java/com/bioinformatics/dna
|- controller/
|  |- AuthController.java
|  |- AlignmentController.java
|  |- GlobalExceptionHandler.java
|- dto/
|  |- AlignmentRequest.java
|  |- AlignmentResponse.java
|  |- AuthDtos.java
|  |- HistoryItemDto.java
|  |- BatchReportDto.java
|- model/
|  |- AlgorithmType.java
|  |- UserAccount.java
|  |- AuthToken.java
|  |- AnalysisRecord.java
|- repository/
|  |- UserAccountRepository.java
|  |- AuthTokenRepository.java
|  |- AnalysisRecordRepository.java
|- service/
|  |- AlignmentService.java
|  |- AuthService.java
|  |- AnalysisHistoryService.java
|  |- FastaService.java
|  |- TokenExtractor.java
|- DnaSequenceMatcherApplication.java

src/main/resources/static
|- index.html
|- styles.css
|- app.js
```

## Step-by-Step: Run Locally

1. Install **Java 17+** and **Maven**.
2. Clone/download this project.
3. Open terminal in project root.
4. Start app:
   ```bash
   ./mvnw spring-boot:run
   ```
   On Windows PowerShell you can also run:
   ```powershell
   .\mvnw.cmd spring-boot:run
   ```
5. Open browser: [http://localhost:8080](http://localhost:8080)
6. Register a user, login, then run alignments.

## Step-by-Step: Deploy to Website (Render Example)

1. Push project to GitHub.
2. Create account on [Render](https://render.com/).
3. New -> **Blueprint** -> Connect GitHub repo.
4. Render auto-detects `render.yaml` (one-click deploy config).
5. Click **Apply** to deploy.
6. Copy generated URL and place it in **Live Demo** section.

## Step-by-Step: Deploy to Railway (Alternative)

1. Push code to GitHub.
2. Create project on [Railway](https://railway.app/).
3. Deploy from GitHub repo.
4. Railway auto-detects Maven/Spring Boot.
5. After deployment, use provided public domain as live demo link.

## Run with Docker (One Command)

### Build and run using Docker Compose

```bash
docker compose up --build
```

Open [http://localhost:8080](http://localhost:8080)

### Stop containers

```bash
docker compose down
```

### Run only with Dockerfile

```bash
docker build -t dna-sequence-matcher .
docker run -p 8080:8080 dna-sequence-matcher
```

## API Quick Guide

### Auth
- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`

### Alignment
- `POST /api/alignment/analyze` (auth required)
- `GET /api/alignment/history` (auth required)
- `POST /api/alignment/batch-fasta` (auth required, multipart file)

## Important Features That Make This Project Stand Out

- End-to-end full-stack implementation, not only algorithm demo.
- Bioinformatics algorithm education + practical data workflow in one product.
- User-scoped persistence with authentication.
- Interactive matrix + animated traceback for better learning.
- Batch FASTA reporting to support real sequence sets.
- Ready for cloud deployment with minimal configuration.

## Future Enhancements (Recommended)

- PostgreSQL/MySQL support for production.
- Email verification and password reset.
- Role-based access (admin, analyst, student).
- Export to CSV/PDF reports.
- Comparative charts and mutation hotspot visualization.
- Docker + CI/CD pipeline.

