🎓 Intelligent School Timetable Management Platform (ISTMS)
A multi-tenant, cloud-native microservices platform for intelligent timetable scheduling in educational institutions. Built with Java 21, Spring Boot 3.5.7, and microservices architecture.

🌟 Features
Core Capabilities
✅ Multi-tenant architecture - Serve multiple schools on a single platform

✅ Intelligent scheduling - AI-powered timetable generation with OptaPlanner

✅ Role-based access - Admin, Teacher, Student roles with granular permissions

✅ Real-time updates - Live timetable changes and notifications

✅ Resource optimization - Smart allocation of teachers, rooms, and classes

✅ Extracurricular support - Schedule clubs, sports, and activities

Technical Features
✅ Microservices architecture - Loosely coupled, independently deployable services

✅ API Gateway - Centralized security, rate limiting, and routing

✅ Service Discovery - Dynamic service registration with Eureka

✅ Production-ready - Circuit breakers, retry mechanisms, distributed tracing

✅ Modern stack - Java 21, Spring Boot 3.5.7, PostgreSQL, Redis, Docker

🏗️ Architecture
text
┌─────────────────────────────────────────────────────────┐
│                    Frontend (React.js)                  │
│            (Tailwind CSS, FullCalendar.js)             │
└──────────────────────────┬──────────────────────────────┘
                           │
                    ┌──────▼──────┐
                    │ API Gateway │ ← Centralized Security
                    │  (Port 4001)│
                    └──────┬──────┘
                           │
       ┌───────────────────┼───────────────────┐
       │                   │                   │
┌──────▼──────┐    ┌──────▼──────┐    ┌───────▼──────┐
│ Auth Service│    │ Timetable   │    │ User Service │
│  (Port 4002)│    │   Service   │    │   (Future)   │
└─────────────┘    └─────────────┘    └──────────────┘
       │                   │                   │
       └───────────────────┼───────────────────┘
                           │
                    ┌──────▼──────┐
                    │ PostgreSQL  │
                    │    Redis    │
                    └─────────────┘
🚀 Quick Start
Prerequisites
Java 21 or higher

Maven 3.8+

PostgreSQL 14+

Redis 7+

Docker (optional)

Environment Setup
Clone the repository:

bash
git clone https://github.com/yourusername/intelligent-school-timetable-platform.git
cd intelligent-school-timetable-platform
Set up PostgreSQL databases:

sql
CREATE DATABASE istms_auth_db;
CREATE DATABASE istms_timetable_db;
CREATE USER istms_user WITH PASSWORD 'istms_password';
GRANT ALL PRIVILEGES ON DATABASE istms_auth_db TO istms_user;
GRANT ALL PRIVILEGES ON DATABASE istms_timetable_db TO istms_user;
Configure environment variables:

bash
# Create .env file in root
cp .env.example .env

# Edit .env with your configuration
JWT_SECRET=your-super-secret-jwt-key-here
DB_PASSWORD=istms_password
Running Locally
Option 1: Manual Start (Development)

bash
# 1. Start Redis
redis-server

# 2. Start Service Discovery (Eureka)
cd service-discovery
mvn spring-boot:run

# 3. Start Auth Service
cd auth-service
mvn spring-boot:run

# 4. Start API Gateway
cd api-gateway
mvn spring-boot:run

# Access at:
# - Eureka Dashboard: http://localhost:4000
# - API Gateway: http://localhost:4001
# - Eureka via Gateway: http://localhost:4001/eureka
Option 2: Docker Compose (Recommended)

bash
# Build and start all services
docker-compose up --build

# Access at: http://localhost:4001
Testing the Setup
bash
# Run comprehensive tests
chmod +x test-all.sh
./test-all.sh

# Check service health
curl http://localhost:4001/actuator/health

# View registered services
curl http://localhost:4001/eureka/apps | jq .
📁 Project Structure
text
intelligent-school-timetable-platform/
├── service-discovery/          # Eureka Service Registry (Port 4000)
├── api-gateway/               # Spring Cloud Gateway (Port 4001)
├── auth-service/              # Authentication & Authorization (Port 4002)
├── timetable-service/         # Timetable Core Service (Future)
├── user-service/             # User Management Service (Future)
├── notification-service/     # Notification Service (Future)
├── analytics-service/        # Analytics & Reporting (Future)
├── docker-compose.yml        # Docker orchestration
├── .env.example             # Environment variables template
├── README.md               # This file
└── scripts/                # Utility scripts
    ├── test-all.sh         # Comprehensive test script
    ├── setup-db.sh         # Database setup script
    └── deploy.sh           # Deployment script
🔧 Configuration
Service Ports
Service	Port	Description
Service Discovery	4000	Eureka Server
API Gateway	4001	Entry point for all services
Auth Service	4002	Authentication & JWT
Timetable Service	4003	Core scheduling (Future)
User Service	4004	User management (Future)
Environment Variables
env
# JWT Configuration
JWT_SECRET=your-base64-encoded-secret-key

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=istms_auth_db
DB_USER=istms_user
DB_PASSWORD=istms_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Eureka
EUREKA_URL=http://localhost:4000/eureka/

# Profiles
SPRING_PROFILES_ACTIVE=dev  # dev, prod, or test
📚 API Documentation
Authentication Endpoints
text
POST /auth/api/v1/auth/login     # User login
POST /auth/api/v1/auth/register  # User registration
POST /auth/api/v1/auth/refresh   # Refresh JWT token
GET  /auth/api/v1/auth/me        # Get current user info
Swagger Documentation
Development: http://localhost:4001/swagger-ui.html

API Docs: http://localhost:4001/v3/api-docs

Actuator Endpoints
text
GET /actuator/health            # Service health
GET /actuator/metrics           # Application metrics
GET /actuator/gateway/routes    # Gateway routes
GET /actuator/prometheus        # Prometheus metrics
🛡️ Security Features
Centralized Security at Gateway
✅ JWT Authentication - All tokens validated at gateway

✅ Rate Limiting - Redis-based rate limiting per IP/user

✅ CORS Management - Centralized cross-origin configuration

✅ Circuit Breakers - Resilience4j for fault tolerance

✅ Request Tracing - UUID-based request tracking

✅ Security Headers - XSS protection, content security policies

Multi-tenancy
Each school operates in isolated tenant space

Data segregation by school_id in all queries

Tenant-specific configuration and constraints

🧪 Testing
Run Tests
bash
# Unit tests
mvn test

# Integration tests
mvn verify

# Specific service tests
cd auth-service && mvn test
Test Coverage
bash
# Generate coverage reports
mvn jacoco:report

# View reports in browser
open target/site/jacoco/index.html
🚢 Deployment
Production Deployment
bash
# 1. Set production environment
export SPRING_PROFILES_ACTIVE=prod
export JWT_SECRET=$(openssl rand -base64 32)

# 2. Build with production profile
mvn clean package -Pprod

# 3. Deploy with Docker
docker-compose -f docker-compose.prod.yml up -d

# 4. Verify deployment
curl https://your-domain.com/actuator/health
Docker Deployment
dockerfile
# Build individual services
docker build -t istms-api-gateway:latest ./api-gateway
docker build -t istms-auth-service:latest ./auth-service

# Or use docker-compose
docker-compose up --build -d
📊 Monitoring & Observability
Built-in Monitoring
Spring Boot Actuator - Health checks, metrics, environment info

Prometheus - Metrics collection and scraping

Grafana - Dashboard for visualization (Future)

Distributed Tracing - Request tracing across services

Logging
Structured JSON logging in production

Centralized log aggregation (Future)

Log levels configurable per environment

Health Checks
bash
# Check gateway health
curl http://localhost:4001/actuator/health

# Check service discovery
curl http://localhost:4000/actuator/health

# Check database connectivity
curl http://localhost:4001/actuator/health | jq '.components.db'
🔄 Development Workflow
Branch Strategy
text
main
├── develop
│   ├── feature/auth-service
│   ├── feature/timetable-service
│   └── hotfix/security-patch
└── release/v1.0.0
Code Quality
bash
# Run code quality checks
mvn clean verify

# Check dependencies
mvn dependency:analyze

# Format code
mvn spotless:apply
📈 Roadmap
Phase 1: Foundation (Current)
✅ Service Discovery with Eureka

✅ API Gateway with centralized security

✅ Authentication Service with JWT

✅ Multi-tenancy architecture

Phase 2: Core Services (In Progress)
Timetable Service with OptaPlanner

User Management Service

Notification Service

Analytics & Reporting Service

Phase 3: Enhanced Features
WebSocket for real-time updates

Mobile applications

Advanced reporting dashboards

Integration with external systems

Phase 4: Scaling & Optimization
Kubernetes deployment

Advanced caching strategies

Machine learning for optimization

Global deployment support

🤝 Contributing
We welcome contributions! Please see our Contributing Guidelines.

Fork the repository

Create a feature branch

bash
git checkout -b feature/amazing-feature
Commit your changes

bash
git commit -m 'Add amazing feature'
Push to the branch

bash
git push origin feature/amazing-feature
Open a Pull Request

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.

🆘 Support
Documentation: Wiki

Issues: GitHub Issues

Discussions: GitHub Discussions

🙏 Acknowledgments
Spring Boot

OptaPlanner

Netflix Eureka

Redis

PostgreSQL

📞 Contact
Project Maintainer: [Your Name]
Email: your.email@example.com
Website: https://upvalley.co.zw

Project Link: https://github.com/yourusername/intelligent-school-timetable-platform

<div align="center">
⭐️ Star us on GitHub!
If you find this project useful, please consider giving it a star on GitHub!

https://api.star-history.com/svg?repos=yourusername/intelligent-school-timetable-platform&type=Date

</div>
Made with ❤️ for educational institutions worldwide
