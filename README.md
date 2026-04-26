# 🏭 SMO - Smart Manufacturing Operations (Backend)

<div align="center">

![Java](https://img.shields.io/badge/Java-17+-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.0-6DB33F?style=for-the-badge&logo=spring&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Hibernate](https://img.shields.io/badge/Hibernate-JPA-59666C?style=for-the-badge&logo=hibernate&logoColor=white)
![Maven](https://img.shields.io/badge/Maven-C71A36?style=for-the-badge&logo=apache-maven&logoColor=white)

**Enterprise-grade REST API for intelligent garment manufacturing workflow management**

[Features](#-features) • [Architecture](#-architecture) • [Getting Started](#-getting-started) • [API Documentation](#-api-documentation) • [Deployment](#-deployment)

</div>

---

## 📋 Overview

SMO Backend is a robust, scalable Spring Boot application that powers a comprehensive Smart Manufacturing Operations system for the garment industry. It provides real-time workflow orchestration, process planning, quality control, and production tracking with advanced features like parallel workflow execution and intelligent bin merging.

### 🎯 Key Highlights

- **🔄 Dynamic Workflow Engine** - DAG-based process routing with parallel branch execution and merge points
- **📊 Real-time Analytics** - Node metrics, operator performance tracking, and production insights
- **🏷️ QR-based Tracking** - Complete garment lifecycle traceability from cutting to packaging
- **🔐 Role-based Access Control** - Multi-role authentication (HR, GM, Supervisor, Process Planner, Operator)
- **⚡ High Performance** - Optimized JPA queries, connection pooling, and async processing
- **🎨 Clean Architecture** - Layered design with clear separation of concerns

---

## ✨ Features

### 🏗️ Core Modules

#### 1. **Process Planning & Workflow Management**
- Create and manage manufacturing process plans with approval workflows
- Define operations with semantic types: `SEQUENTIAL`, `PARALLEL_BRANCH`, `MERGE`
- Explicit edge-based workflow graph generation
- Clone and version process plans
- Real-time workflow visualization support

#### 2. **Production Tracking & Monitoring**
- QR code-based garment tracking across all operations
- Bin assignment and merging with validation
- WIP (Work in Progress) tracking
- Operation-level progress monitoring
- Automated bin history logging

#### 3. **Quality Control**
- Multi-stage QC checkpoints
- Defect tracking and reporting
- Final inspection workflows
- QC metrics and analytics

#### 4. **HR & Employee Management**
- Employee CRUD operations with role assignment
- Login credential management
- Role-based permissions (HR/Admin, Supervisor, GM, Process Planner, Operator)
- Employee performance tracking

#### 5. **Inventory & Store Management**
- Item and vendor management
- Purchase orders and GRN (Goods Receipt Note)
- Inventory stock tracking with movement history
- BOM (Bill of Materials) management
- Stock level alerts

#### 6. **Supervisor Operations**
- QR assignment to operators
- Work tracking and validation
- Bin merging operations
- Operator performance insights
- Work reassignment capabilities

#### 7. **Insights & Analytics**
- Dashboard metrics for all roles
- Supervisor floor insights
- Node-level performance metrics
- Production bottleneck identification
- Real-time KPI tracking
- GM-specific insights (pending/approved process plans)

#### 8. **Service Discovery & Auto-Configuration**
- mDNS/Bonjour service discovery
- Automatic backend detection on local networks
- Network scanning fallback mechanism
- Health check endpoints
- Service information endpoints
- Support for any local network (192.168.x.x, 10.x.x.x, 172.x.x.x)

---

## 🏛️ Architecture

### Technology Stack

```
┌─────────────────────────────────────────────────────────────┐
│                     Presentation Layer                       │
│  REST Controllers • DTOs • Request/Response Models           │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                      Business Layer                          │
│  Services • Validators • Business Logic • Workflow Engine    │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Data Access Layer                         │
│  JPA Repositories • Entity Models • Database Mappings        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                       Database Layer                         │
│              MySQL 8.0 • Relational Schema                   │
└─────────────────────────────────────────────────────────────┘
```

### Key Design Patterns

- **Repository Pattern** - Clean data access abstraction
- **DTO Pattern** - Decoupled API contracts from domain models
- **Service Layer Pattern** - Centralized business logic
- **Dependency Injection** - Loose coupling via Spring IoC
- **Builder Pattern** - Complex object construction (WorkflowEdge, Responses)

### Database Schema Highlights

- **18 Core Tables** - Normalized relational design
- **Foreign Key Constraints** - Referential integrity
- **Indexed Columns** - Optimized query performance
- **Audit Fields** - Timestamps for tracking
- **Enum Types** - Type-safe operation classifications

---

## 🚀 Getting Started

### Prerequisites

```bash
Java 17+
Maven 3.8+
MySQL 8.0+
```

### Installation

1. **Clone the repository**
```bash
git clone https://github.com/PremSaiBollamoni/SMO.git
cd SMO
```

2. **Configure database**
```bash
# Create MySQL database
mysql -u root -p
CREATE DATABASE smo;
```

3. **Update application properties**
```properties
# src/main/resources/application.properties
spring.datasource.url=jdbc:mysql://localhost:3306/smo
spring.datasource.username=root
spring.datasource.password=your_password
```

4. **Build the project**
```bash
mvn clean install
```

5. **Run the application**
```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`

### Docker Deployment

```bash
# Build Docker image
docker build -t smo-backend .

# Run container
docker run -p 8080:8080 \
  -e DB_URL=jdbc:mysql://host.docker.internal:3306/smo \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  smo-backend
```

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
```http
POST /api/auth/login
Content-Type: application/json

{
  "loginid": "string",
  "password": "string"
}
```

### Key Endpoints

#### Process Planning
```http
GET    /api/processplan/pending              # Get pending approvals
GET    /api/processplan/{routingId}          # Get process plan details
POST   /api/processplan/draft                # Create draft process plan
POST   /api/processplan/{id}/approve         # Approve process plan
POST   /api/processplan/{id}/reject          # Reject process plan
```

#### Production Operations
```http
GET    /api/production/operations             # List all operations
POST   /api/production/operations             # Create operation
GET    /api/production/routings               # List routings
POST   /api/production/routings               # Create routing
GET    /api/production/bundles                # List bundles
POST   /api/production/bundles                # Create bundle
```

#### Supervisor Operations
```http
POST   /api/supervisor/qr-assignment          # Assign QR to operator
POST   /api/supervisor/tracking               # Track work progress
POST   /api/supervisor/merging                # Merge bins
GET    /api/supervisor/process-plans          # Get process plans
```

#### HR Management
```http
GET    /api/hr/employees                      # List employees
POST   /api/hr/employees                      # Create employee
PUT    /api/hr/employees/{id}                 # Update employee
DELETE /api/hr/employees/{id}                 # Delete employee
GET    /api/hr/roles                          # List roles
POST   /api/hr/roles                          # Create role
```

#### Insights & Analytics
```http
GET    /api/insights/dashboard                # Dashboard metrics
GET    /api/insights/supervisor               # Supervisor insights
GET    /api/insights/gm                       # GM insights (pending/approved plans)
```

#### Service Discovery
```http
GET    /api/health                            # Health check endpoint
GET    /api/discovery/info                    # Service discovery info
GET    /api/discovery/ping                    # Service discovery ping
```

### Response Format

**Success Response:**
```json
{
  "status": "success",
  "data": { ... },
  "message": "Operation completed successfully"
}
```

**Error Response:**
```json
{
  "status": "error",
  "message": "Error description",
  "timestamp": "2026-04-25T10:30:00"
}
```

---

## 🔧 Configuration

### Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DB_URL` | MySQL connection URL | `jdbc:mysql://localhost:3306/smo` |
| `DB_USERNAME` | Database username | `root` |
| `DB_PASSWORD` | Database password | - |
| `SMO_DATA_KEY` | AES encryption key (32 chars) | `SMO_DEFAULT_32_CHAR_SECRET_KEY_!` |
| `SERVER_PORT` | Application port | `8080` |

### Application Properties

```properties
# Server Configuration
server.port=8080
server.address=0.0.0.0

# Database Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Security
app.security.aes-key=${SMO_DATA_KEY}

# Service Discovery Configuration
smo.service.name=SMO-Backend
smo.service.version=1.0.0
app.base-url=http://localhost

# Production-Safe Logging
logging.level.com.cutm.smo=INFO
logging.level.org.springframework.web=WARN
logging.level.org.hibernate=WARN
```

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=ProcessPlanServiceTest

# Generate coverage report
mvn jacoco:report
```

---

## 📦 Deployment

### Render.com Deployment

The application includes a `render.yaml` configuration for one-click deployment:

```yaml
services:
  - type: web
    name: smo-backend
    env: java
    buildCommand: mvn clean package -DskipTests
    startCommand: java -jar target/smo-V0.1.war
```

### Production Checklist

- [ ] Set strong `SMO_DATA_KEY` environment variable
- [ ] Configure production database with SSL
- [ ] Enable HTTPS/TLS
- [ ] Set up database backups
- [ ] Configure logging and monitoring
- [ ] Set up CI/CD pipeline
- [ ] Enable rate limiting
- [ ] Configure CORS for frontend domain

---

## 🏗️ Project Structure

```
src/main/java/com/cutm/smo/
├── config/              # Configuration classes
│   ├── CorsConfig.java
│   ├── DataInitializer.java
│   └── WebConfig.java
├── controller/          # REST Controllers
│   ├── AuthController.java
│   ├── ProcessPlanController.java
│   ├── ProductionController.java
│   ├── SupervisorController.java
│   ├── HrController.java
│   └── InsightsController.java
├── dto/                 # Data Transfer Objects
│   ├── ProcessPlanResponse.java
│   ├── WorkflowEdge.java
│   ├── NodeMetricsResponse.java
│   └── ...
├── models/              # JPA Entity Models
│   ├── Operation.java
│   ├── OperationType.java
│   ├── Routing.java
│   ├── RoutingStep.java
│   ├── Bin.java
│   └── ...
├── repositories/        # JPA Repositories
│   ├── OperationRepository.java
│   ├── RoutingRepository.java
│   └── ...
├── services/            # Business Logic Services
│   ├── ProcessPlanService.java
│   ├── SupervisorService.java
│   ├── HrService.java
│   └── ...
└── validation/          # Validators
    └── OperationValidator.java
```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Code Style Guidelines

- Follow Java naming conventions
- Use meaningful variable and method names
- Add JavaDoc comments for public methods
- Write unit tests for new features
- Keep methods focused and concise

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👥 Authors

**Prem Sai Bollamoni**
- GitHub: [@PremSaiBollamoni](https://github.com/PremSaiBollamoni)
- LinkedIn: [Prem Sai Bollamoni](https://linkedin.com/in/premsaibollamoni)

---

## 🙏 Acknowledgments

- Spring Boot team for the excellent framework
- Hibernate for robust ORM capabilities
- MySQL for reliable database management
- The open-source community for inspiration and tools

---

<div align="center">

**⭐ Star this repository if you find it helpful!**

Made with ❤️ for the garment manufacturing industry

</div>
