# Scheduler System - User Service

**Identity and Access Management (IAM) microservice for the Scheduler System platform.**

The User Service provides a secure and scalable authentication and authorization layer for the Scheduler System platform, implementing Role-Based Access Control (RBAC) with Spring Security and JWT Authentication.

---

## Summary

- [About the Project](#about-the-project)
- [Platform Architecture](#platform-architecture)
- [User Service Architecture](#user-service-architecture)
- [Features](#features)
- [Security Architecture](#security-architecture)
- [Package Structure](#package-structure)
- [Endpoints](#endpoints)
- [API Request Examples](#api-request-examples)
- [Technologies Used](#technologies-used)
- [How to Run the Project](#how-to-run-the-project)
- [API Documentation](#api-documentation)
- [Tests and Coverage](#tests-and-coverage)
- [CI/CD Pipeline](#cicd-pipeline)
- [Roadmap](#roadmap)
- [License](#license)
- [Author](#author)

---

## About the Project

The **User Service** is the **Identity and Access Management (IAM)** component of the **Scheduler System platform.**

It is responsible for:

- User registration
- Authentication
- Authorization
- User profile management
- Role assignment
- User status control
- Address management
- Phone management
- Administrative user operations

Authentication and authorization are implemented using **Spring Security** and **JWT Authentication**, ensuring secure access to protected resources through **Role-Based Access Control (RBAC)**.

The service was designed following **RESTful API principles** and is part of a microservices architecture where each service owns a specific business domain.

The application follows a layered architecture inspired by **Clean Architecture principles**, separating responsibilities between controllers, application services, domain entities, repositories, and infrastructure components.

---

## Platform Architecture

The Scheduler System follows a **microservices architecture** where each service is responsible for a specific business domain.

Services can be developed, deployed, scaled, and maintained independently, improving flexibility, scalability, and system reliability.

### Services

- **BFF (Backend for Frontend)** – Centralizes requests and orchestrates communication between clients and microservices
- **User Service** – Identity and Access Management (IAM)
- **Task Service** – Task management and scheduling
- **Notification Service** – Email notification delivery

---

## User Service Architecture

<div align="center">

<img src="img/architecture.png" alt="User Service Architecture" style="width: 900px; height: auto;" />

</div>

### Request Flow:

1. Client sends a request
2. BFF forwards the request to the User Service
3. Spring Security validates authentication and authorization
4. Business rules are executed
5. Data is persisted in PostgreSQL
6. Response is returned to the BFF
7. BFF returns the response to the client

---

## Features

### Authentication

- User registration
- User authentication

---

### User Management

Authenticated users can:

- Retrieve their own profile
- Update their own profile
- Delete their own account

---

### Address Management

Users can manage their own addresses:

- Create address
- List addresses
- Update address
- Delete address

---

### Phone Management

Users can manage their own phone numbers:

- Create phone number
- List phone numbers
- Update phone number
- Delete phone number

---

### Administration

Administrators can:

- Create users
- Find users by ID
- Find users by email
- Search users with filters
- Update user information
- Update user status
- Update user roles
- Delete users

---

### Authorization

The system implements **Role-Based Access Control (RBAC)** with the following roles:

**ROLE_USER**

Permissions:

- View own profile
- Update own profile
- Delete own profile
- Manage own addresses
- Manage own phone numbers

---

**ROLE_ADMIN**

Permissions:

- Full user management
- User status management
- User role management
- User search and filtering
- Administrative operations

---

### User Status

Users can have the following states:

| Status   | Description                      |
|----------|----------------------------------|
| ACTIVE   | User can access the system       |
| INACTIVE | User account is inactive         |
| BLOCKED  | User access is blocked           |
| PENDING  | User account awaiting activation |

---

## Security Architecture

The User Service uses **Spring Security** with **JWT Authentication** to protect API resources and enforce authorization rules.

The security implementation follows a **stateless authentication architecture**, meaning the server does not maintain user sessions.

Every protected request must contain a valid JWT token using the Bearer authentication scheme.

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

### Security Features

The security layer provides:

- JWT-based authentication
- Role-Based Access Control (RBAC)
- Stateless session management
- BCrypt password hashing
- Protected API endpoints
- Custom authentication handling
- Custom authorization handling

CSRF protection is disabled because the application follows a stateless REST API architecture using JWT authentication.

---

### Security Components

The security layer is composed of the following components:

| Component                      | Responsibility                                                                    |
|--------------------------------|-----------------------------------------------------------------------------------|
| Spring Security                | Provides authentication and authorization mechanisms for protecting API endpoints |
| JWT Authentication             | Enables stateless authentication using JSON Web Tokens (JWT)                      |
| JwtRequestFilter               | Intercepts incoming requests and validates JWT tokens before granting access      |
| UserDetailsServiceImpl         | Loads user information and assigned roles during authentication                   |
| CustomAuthenticationEntryPoint | Handles unauthenticated requests and returns HTTP 401 Unauthorized                |
| CustomAccessDeniedHandler      | Handles authorization failures and returns HTTP 403 Forbidden                     |

---

### Authentication and Authorization Flow

The authentication process works as follows:

1. User sends login credentials through the authentication endpoint.
2. Application validates credentials against stored user data.
3. Password validation is performed securely using BCrypt.
4. A JWT token is generated after successful authentication.
5. Client stores the JWT token.
6. Client sends the token in subsequent API requests.

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

## Package Structure

The User Service follows a layered architecture inspired by **Clean Architecture principles**, separating responsibilities between API interfaces, application logic, domain models, and infrastructure components.

```text
com.geisivan.userservice/
│
│
├── application/
│   │ 
│   ├── dto/
│   ├── mapper/
│   ├── service/
│   └── validator/
│
├── controller/
│
├── domain/
│
└── infrastructure/
    │ 
    ├── config/
    ├── exception/
    ├── handler/
    ├── repository/
    └── security/
```

### Layer Responsibilities

| Layer          | Responsibility                                                                          |
|----------------|-----------------------------------------------------------------------------------------|
| Controller     | Exposes REST API endpoints and handles HTTP requests                                    |
| Application    | Contains business use cases, DTOs, validators, and service logic                        |
| Domain         | Contains business entities, enums, and core domain rules                                |
| Infrastructure | Contains database access, security configuration, exceptions, and external integrations |

---

## Endpoints

The User Service exposes RESTful API endpoints for:

- Authentication
- User profile management
- Address management
- Phone management
- Administrative user operations

Protected endpoints require a valid JWT token using the Bearer authentication scheme.

Example:

```http
Authorization: Bearer <JWT_TOKEN>
```

---

### Public Endpoints 🔓

These endpoints do not require authentication.

#### Authentication 

| Method | Endpoint                | Authentication | Description                              |
|--------|-------------------------|----------------|------------------------------------------|
| POST   | `/api/v1/auth/register` | ❌ public      | Register a new user                      |
| POST   | `/api/v1/auth/login`    | ❌ public      | Authenticate user and generate JWT token |

---

### Protected Endpoints 🔒

These endpoints require a valid JWT token.

#### Authenticated User Endpoints

**Required Role:** `ROLE_USER`

| Method | Endpoint           | Authentication | Description                           |
|--------|--------------------|----------------|---------------------------------------|
| GET    | `/api/v1/users/me` | ✅ JWT         | Retrieve authenticated user's profile |
| PUT    | `/api/v1/users/me` | ✅ JWT         | Update authenticated user's profile   |
| DELETE | `/api/v1/users/me` | ✅ JWT         | Delete authenticated user's account   |

---

#### Address Management 

These endpoints allow authenticated users to manage their own addresses.

**Required Role:** `ROLE_USER`

| Method | Endpoint                          | Authentication | Description                             |
|--------|-----------------------------------|----------------|-----------------------------------------|
| POST   | `/api/v1/users/me/addresses`      | ✅ JWT         | Create a new address                    |
| GET    | `/api/v1/users/me/addresses`      | ✅ JWT         | Retrieve authenticated user's addresses |
| PUT    | `/api/v1/users/me/addresses/{id}` | ✅ JWT         | Update an existing address              |
| DELETE | `/api/v1/users/me/addresses/{id}` | ✅ JWT         | Delete an address                       |

---

#### Phone Management

These endpoints allow authenticated users to manage their own phone numbers.

**Required Role:** `ROLE_USER`

| Method | Endpoint                       | Authentication | Description                                   |
|--------|--------------------------------|----------------|-----------------------------------------------|
| POST   | `/api/v1/users/me/phones`      | ✅ JWT         | Create a new phone number                     |
| GET    | `/api/v1/users/me/phones`      | ✅ JWT         | Retrieve authenticated user's phone numbers   |
| PUT    | `/api/v1/users/me/phones/{id}` | ✅ JWT         | Update a phone number                         |
| DELETE | `/api/v1/users/me/phones/{id}` | ✅ JWT         | Delete a phone number                         |

---

#### Administrative Endpoints

These endpoints are restricted to administrators.

**Required Role:** `ROLE_ADMIN`

| Method | Endpoint                                           | Authentication | Description                                         |
|--------|----------------------------------------------------|----------------|-----------------------------------------------------|
| POST   | `/api/v1/admin/users`                              | ✅ JWT         | Create a new user                                   |
| GET    | `/api/v1/admin/users`                              | ✅ JWT         | Retrieve users with optional filters and pagination |
| GET    | `/api/v1/admin/users/{id}`                         | ✅ JWT         | Retrieve user by ID                                 |
| GET    | `/api/v1/admin/users/email?email=user@example.com` | ✅ JWT         | Retrieve user by email                              |
| PUT    | `/api/v1/admin/users/{id}`                         | ✅ JWT         | Update user information                             |
| PATCH  | `/api/v1/admin/users/{id}/status`                  | ✅ JWT         | Update user status                                  |
| PATCH  | `/api/v1/admin/users/{id}/roles`                   | ✅ JWT         | Update user roles                                   |
| DELETE | `/api/v1/admin/users/{id}`                         | ✅ JWT         | Delete user                                         |

---

### Authorization Rules

| Resource                | Required Role  | Description                              |
|-------------------------|----------------|------------------------------------------|
| User profile management | `ROLE_USER`    | Users can manage their own profile       |
| Address management      | `ROLE_USER`    | Users can manage their own addresses     |
| Phone management        | `ROLE_USER`    | Users can manage their own phone numbers |
| User administration     | `ROLE_ADMIN`   | Administrators can manage platform users |

All authorization rules are enforced by Spring Security through JWT Authentication and Role-Based Access Control (RBAC).

---

## API Request Examples

### User Registration

Creates a new user account.

**Endpoint:**

```http
POST /api/v1/auth/register
```

Request:

```json
{
    "name": "User",
    "email": "user@example.com",
    "password": "123456"
}
```

Response:

```json
{
    "id": 1,
    "name": "User",
    "email": "user@example.com",
    "status": "ACTIVE",
    "roles": [
      "ROLE_USER"
    ],
    "createdAt": "2026-07-26T18:30:15Z",
    "updatedAt": "2026-07-26T18:30:15Z",
    "addresses": [],
    "phones": []
}
```

---

### User Authentication

Authenticates a user and generates a JWT token.

**Endpoint**

```http
POST /api/v1/auth/login
```

**Request**

```json
{
    "email": "user@example.com",
    "password": "123456"
}
```

**Response**

```json
{ 
   "token": "jwt-token", 
   "type": "Bearer"
}
```

---

## Technologies Used

### Backend

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)

[![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5.16-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)

[![Spring Security](https://img.shields.io/badge/Spring_Security-Authentication-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-security)

[![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-Persistence-6DB33F?logo=spring&logoColor=white)](https://spring.io/projects/spring-data-jpa)

[![JWT](https://img.shields.io/badge/JJWT-0.13.0-000000?logo=jsonwebtokens&logoColor=white)](https://github.com/jwtk/jjwt)

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

[![Gradle](https://img.shields.io/badge/Gradle-Build_Tool-02303A?logo=gradle&logoColor=white)](https://gradle.org/)

---

### API Documentation

[![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?logo=swagger&logoColor=white)](https://swagger.io/)

---

### Testing and Code Quality

[![JUnit 5](https://img.shields.io/badge/JUnit_5-Testing-25A162?logo=junit5&logoColor=white)](https://junit.org/junit5/)

[![Mockito](https://img.shields.io/badge/Mockito-Mocking-78A641?logoColor=white)](https://site.mockito.org/)

[![JaCoCo](https://img.shields.io/badge/JaCoCo-Coverage-orange)](https://www.jacoco.org/jacoco/)

[![SonarQube](https://img.shields.io/badge/SonarQube-Code_Quality-4E9BCD?logo=sonarqube&logoColor=white)](https://www.sonarsource.com/)

---

## How to Run the Project

### Prerequisites

⚠️ **Important**: Before running the application, ensure that the following software is installed on your machine:

[![Java](https://img.shields.io/badge/Java-17-ED8B00?logo=openjdk&logoColor=white)](https://www.oracle.com/java/)

[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)

💡 _Recommended tool for API testing:_

[![Postman](https://img.shields.io/badge/Postman-API%20Collection-FF6C37?logo=postman&logoColor=white)](https://www.postman.com/)

---

### Clone the Repository

```bash
# Clone the repository
git clone https://github.com/geisivanvitena/scheduler-user-service.git

# Navigate to the project directory
cd scheduler-user-service
```

---

### Environments

```bash
# JWT Configuration
JWT_SECRET=secret-key
JWT_EXPIRATION_MS=3600000

# PostgreSQL Configuration
DB_HOST=host
DB_PORT=port
POSTGRES_DB=db
POSTGRES_USER=user
POSTGRES_PASSWORD=password
```

⚠️ **Important**: Never commit .env files containing sensitive information.

---

### Run the Application

⚠️ **Important**: Make sure PostgreSQL is running and accessible on the local host.

#### Build the project

```bash
./gradlew clean build
```

#### Start the application

```bash
./gradlew bootRun
```

#### The application will be available at:

```text
http://localhost:8080
```

---

## API Documentation

The project provides interactive API documentation through Swagger UI.

[![Swagger](https://img.shields.io/badge/Swagger-API_Docs-85EA2D?logo=swagger&logoColor=white)](http://localhost:8080/swagger-ui/index.html)

#### Swagger UI is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

You can use Swagger UI to:

- Explore available endpoints
- Execute requests directly from the browser
- Inspect request and response models
- Test JWT-protected endpoints

---

## Tests and Coverage

The project includes **comprehensive automated tests** covering:

- Controllers
- Services
- Validators
- Security components
- Business rules

### Testing Stack:

- JUnit 5
- Mockito
- JaCoCo
- SonarQube

| Application Class      | What is Tested                        | Test Class                |
|------------------------|---------------------------------------|---------------------------|
| `AuthController`       | Authentication endpoints              | `AuthControllerTest`      |
| `AuthService`          | Authentication business rules         | `AuthServiceTest`         |
| `UserController`       | User endpoints                        | `UserControllerTest`      |
| `UserService`          | User business rules                   | `UserServiceTest`         |
| `AdminUserController`  | Administrative endpoints              | `AdminUserControllerTest` |
| `AdminUserService`     | Administrative business rules         | `AdminUserServiceTest`    |
| `AddressController`    | Address endpoints                     | `AddressControllerTest`   |
| `AddressService`       | Address business rules                | `AddressServiceTest`      |
| `PhoneController`      | Phone endpoints                       | `PhoneControllerTest`     |
| `PhoneService`         | Phone business rules                  | `PhoneServiceTest`        |
| `UserValidator`        | Validation rules                      | `UserValidatorTest`       |

💡 _Detailed information about all test scenarios can be found in the `src/test/java` directory._

---

### Running the Tests

**Execute all automated tests:**

```bash
./gradlew test
```

**Generate JaCoCo coverage report:**

```bash
./gradlew jacocoTestReport
```

**Coverage report:**

```text
build/reports/jacoco/test/html/index.html
```

---

### SonarQube Analysis

The project uses SonarQube for continuous code quality analysis.

Run SonarQube analysis:

```bash
./gradlew sonar
```

**SonarQube validates:**

- Test coverage
- Code reliability
- Code maintainability
- Code duplication
- Security vulnerabilities
- Code smells

The project must pass the SonarQube Quality Gate to ensure code quality before integration.

---

## CI/CD Pipeline

The project uses GitHub Actions to automate build, testing, code coverage analysis, and code quality verification.

The pipeline executes the following steps:

1. Checkout source code
2. Configure JDK environment
3. Build the application
4. Execute automated tests
5. Generate JaCoCo coverage reports
6. Analyze code quality with SonarQube
7. Validate SonarQube Quality Gate

The pipeline ensures:

- Code reliability
- Automated validation
- Continuous quality monitoring
- Safer integration of new features

CI environment uses JDK 21, while the application runtime uses Java 17.

---

## Roadmap

Future improvements planned for the User Service:

### Security

- [ ] Refresh Token implementation
- [ ] Email verification workflow
- [ ] Password recovery workflow

### DevOps

- [ ] Docker containerization
- [ ] Docker Compose
- [ ] Kubernetes deployment
- [ ] AWS deployment

### Platform Evolution

- [ ] Frontend integration
- [ ] Application monitoring
- [ ] Centralized logging
- [ ] Distributed tracing

---

## License

This project is licensed under the MIT License.

See the LICENSE file for more details.

[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](https://github.com/geisivanvitena/scheduler-user-service/blob/main/LICENSE)

---

## Author

### Geisivan Vitena

Full Stack Java Developer

### Contact

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Geisivan%20Vitena-0A66C2?logo=linkedin&logoColor=white)](https://www.linkedin.com/in/geisivan-vitena-a46168246/)

[![GitHub](https://img.shields.io/badge/GitHub-Perfil-181717?logo=github&logoColor=white)](https://github.com/geisivanvitena/)

[![Email](https://img.shields.io/badge/Email-gsv1205%40yahoo.com-D14836?logo=yahoo&logoColor=white)](mailto:gsv1205@yahoo.com)

---
