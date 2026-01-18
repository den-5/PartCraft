# 🖥️ PartCraft

The Ultimate Full-Stack PC Compatibility Engine. PCPartPicker re-imagined with strict architectural patterns.

-----

# 📖 About

This project is built as a production-ready educational platform. It goes beyond simple CRUD operations, implementing complex business logic for hardware compatibility, strict typing via DTOs, and a clean separation of concerns between the Presentation, Service, and Persistence layers.

-----

# 📸 Demo
Smart Builder Validation Logic

#### Smart Builder

| ![Builder Demo](./assets/IMG_1010.gif)

-----

# ✨ Features

🛠️ The Core Engine
- Geometric Validation: Checks if the GPU length exceeds the Case maximum clearance.
- Socket Matching: Enforces hard constraints between CPU and Motherboard sockets (e.g., LGA1700 vs AM5).
- Power Simulation: Aggregates TDP from all selected components (CPU, GPU, Fans) and validates it against the Power Supply (PSU) wattage to prevent instability.

👤 The Platform
- Secure Auth: Stateless JWT authentication with Google OAuth2 integration.
- Build Management: Users can save, edit, and manage their custom PC configurations.
- Strict Validations: The backend acts as the source of truth, rejecting any invalid build configurations submitted by the client.

-----

# 🏗 Architecture
The project follows a Service-Oriented architecture with strict separation of DTOs (Data Transfer Objects) and Entities.

```plaintext
root/
 ├─ back/ (Spring Boot)
 │   ├─ config/              # Security, Redis, OpenAPI
 │   ├─ controller/          # REST Endpoints (Presentation)
 │   ├─ service/             # Business Logic & Orchestration
 │   │   ├─ compatibility/   # Core validation engine
 │   │   └─ component/       # Component logic
 │   ├─ entity/              # Database Schema (ORM)
 │   └─ dto/                 # Strict Data Contracts
 │
 ├─ front/ (Next.js)
 │   ├─ app/                 # App Router (Pages)
 │   ├─ features/            # Redux Slices & API Services
 │   └─ components/          # Reusable UI
 │
 └─ docker-compose.yml       # Infrastructure as Code
```

-----
# 🔁 Data Flow: Server-Side Validation
PartCraft uses a "Secure Submission" model. While the frontend allows flexibility, the backend performs a final, rigorous compatibility check upon submission to ensure database integrity.

**Example: User saves a Build**

**Client (Next.js):**
1. User selects components (CPU, Motherboard, GPU).
2. User clicks "Save Build".
3. Frontend sends `POST /api/pc` with the build payload.

**Controller Layer (PCController):**
- Receives `CreatePCDTO`.
- Validates basic input formatting.

**Service Layer (PCService):**
- **Orchestration:** Assembles the full PC object from component IDs.
- **Logic Check:** Calls `ComponentCompatibilityService` to run all physics and electronics checks.

**Result:**
- ✅ Success: PC is saved to MySQL, returning `200 OK`.
- ❌ Error: If any check fails, the transaction is rolled back, and a specific exception is returned to the UI (e.g., "PSU Wattage Insufficient").

-----

# 🧩 Tech Stack

**Backend**
- Core: Java 21, Spring Boot 3.5.7.
- Data: MySQL 8.4, Redis (Token blacklist/Caching).
- Security: Spring Security, OAuth2 Client, JJWT.
- Docs: Swagger UI / OpenAPI 3.

**Frontend**
- Core: Next.js 16 (App Router), React 19.
- State: Redux Toolkit, RTK Query.
- Styling: Tailwind CSS 4.
- Lang: TypeScript (Strict Mode).

**DevOps**
- Containerization: Docker & Docker Compose.
- CI/CD: GitLab CI (Build -> Test -> Dockerize -> Deploy).

-----

# 🧠 Architectural Decisions

- **Strict DTO Pattern:** No Entities are ever exposed to the client. We use `CreatePCDTO` and `PCDTO` to decouple the internal database schema from the external API.
- **Global Exception Handling:** A `@ControllerAdvice` layer intercepts business logic exceptions (like `ComponentCompatibilityException`) and transforms them into user-friendly JSON error responses.
- **Database Seeding:** A custom `data.sql` script initializes the database with real-world component data (Lian Li, Ryzen, Intel) to ensure the application is usable immediately after deployment.

-----

# 🚀 Local Setup

**Prerequisite:** Docker installed.

**Clone the Repo:**
```bash
git clone https://github.com/your-repo/partcraft.git
```

**Launch (One Command):**
This builds the JAR, compiles the Frontend, and spins up MySQL/Redis.
```bash
docker-compose up -d --build
```

**Explore:**
- App: [http://localhost:3000](http://localhost:3000)
- Swagger API: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
