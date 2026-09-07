# Architecture Documentation

## System Overview

The Contact Management System is a full-stack web application following a modern microservices-ready architecture with clear separation between frontend and backend layers.

```
┌─────────────────────────────────────────────────────────────┐
│                     Client Browser                           │
└────────────────────┬────────────────────────────────────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
   ┌────▼──────┐        ┌────────▼─────┐
   │  HTML/CSS │        │   JavaScript  │
   │   (SPA)   │        │   (React)     │
   └────┬──────┘        └────────┬──────┘
        │                        │
        └────────────┬───────────┘
                     │
                HTTPS/REST API
                     │
        ┌────────────▼───────────┐
        │   Spring Boot API      │
        │   (Port 8080)          │
        └────────────┬───────────┘
                     │
        ┌────────────┴────────────┐
        │                         │
   ┌────▼──────────┐    ┌────────▼─────────┐
   │  SQL Server   │    │  Security Layer   │
   │  Database     │    │  (JWT/Spring      │
   │               │    │   Security)       │
   └───────────────┘    └───────────────────┘
```

---

## Architecture Layers

### 1. Presentation Layer (Frontend)

**Technology Stack:**
- React 18.2.0 with Vite 5.0.8
- Bootstrap 5.3.2 for styling
- Axios for HTTP communication
- React Router for client-side routing

**Key Components:**
- **Pages:** LoginPage, RegisterPage, DashboardPage, ProfilePage, NotFoundPage
- **UI Components:** ProtectedRoute, Navbar, ContactForm, ContactDetails, DeleteConfirmModal
- **Services:** authService, contactService, userService
- **Global State:** localStorage for authentication tokens and user data

**Responsibilities:**
- User interface rendering
- Form validation and user input
- API request orchestration
- Authentication state management
- Responsive design implementation

**Design Patterns:**
- Component composition
- Custom hooks for logic reuse
- Container/Presentational component pattern
- Service layer abstraction

---

### 2. API Layer (Backend REST Controllers)

**Technology Stack:**
- Spring Boot 3.2.0
- Spring MVC for REST endpoints
- Jackson for JSON serialization

**Controllers:**
```
AuthController
├── POST /api/auth/register
└── POST /api/auth/login

UserController
├── GET /api/users/me
└── PUT /api/users/me/password

ContactController
├── GET /api/contacts (paginated with optional search)
├── GET /api/contacts/{id}
├── POST /api/contacts
├── PUT /api/contacts/{id}
└── DELETE /api/contacts/{id}
```

**Request/Response Flow:**
```
HTTP Request
    ↓
JwtAuthenticationFilter (extract token)
    ↓
SecurityContext (set authentication)
    ↓
Controller Method
    ↓
Service Layer
    ↓
Response DTO
    ↓
JSON Response
```

**Error Handling:**
- GlobalExceptionHandler catches all exceptions
- Standardized ApiError response format
- HTTP status codes follow REST conventions
- Validation errors include field-level details

---

### 3. Business Logic Layer (Services)

**Service Classes:**

#### AuthService
```java
public class AuthService {
    - register(RegisterRequest): AuthResponse
    - login(LoginRequest): AuthResponse
    - validatePassword(password): boolean
    - hashPassword(password): String
}
```

Responsibilities:
- User registration with validation
- Password strength verification
- Login with email or phone
- JWT token generation
- Duplicate user detection

#### UserService
```java
public class UserService {
    - getUserProfile(userId): UserProfileResponse
    - changePassword(userId, ChangePasswordRequest): void
    - updateUser(userId, UserRequest): User
}
```

Responsibilities:
- User profile management
- Password change with current password verification
- User data retrieval

#### ContactService
```java
public class ContactService {
    - getContacts(userId, Pageable): Page<ContactResponse>
    - searchContacts(userId, searchTerm, Pageable): Page<ContactResponse>
    - getContact(userId, contactId): ContactResponse
    - createContact(userId, ContactRequest): ContactResponse
    - updateContact(userId, contactId, ContactRequest): ContactResponse
    - deleteContact(userId, contactId): void
}
```

Responsibilities:
- Contact CRUD operations
- Contact search with pagination
- Contact ownership verification
- Email and phone management
- Data mapping (Entity → DTO)

**Design Patterns:**
- Service abstraction (interfaces)
- Dependency injection
- Transaction management (@Transactional)
- Business logic encapsulation

---

### 4. Security Layer

**Components:**

#### JwtTokenProvider
```
Responsibilities:
- Generate JWT tokens from Authentication
- Extract user ID from token
- Validate token signature and expiration
- Handle token claims
```

**Token Structure:**
```
Header: {
  "alg": "HS512",
  "typ": "JWT"
}

Payload: {
  "sub": "1",  // User ID
  "iat": 1234567890,
  "exp": 1234654290
}

Signature: HMAC-SHA512(secret)
```

#### JwtAuthenticationFilter
```
Responsibilities:
- Extract token from Authorization header
- Validate token using JwtTokenProvider
- Set SecurityContext with authentication
- Continue filter chain on success/failure
```

#### SecurityConfig
```
Responsibilities:
- Enable CSRF protection (disabled for stateless API)
- Configure CORS for allowed origins
- Set session creation policy to STATELESS
- Register JwtAuthenticationFilter
- Permit public endpoints (/auth/register, /auth/login)
- Require authentication for /api/** endpoints
- Configure PasswordEncoder (BCrypt)
```

**Authentication Flow:**
```
User Credentials
    ↓
Login Endpoint
    ↓
AuthService.login() - verify password
    ↓
JwtTokenProvider.generateToken()
    ↓
Return AuthResponse with JWT
    ↓
Client stores token in localStorage
    ↓
Client includes token in Authorization header
    ↓
JwtAuthenticationFilter validates token
    ↓
Request proceeds with authentication context
```

---

### 5. Data Access Layer (Repository & JPA)

**Repository Interfaces:**

#### UserRepository (Spring Data JPA)
```java
interface UserRepository {
    - findById(id): Optional<User>
    - findByEmail(email): Optional<User>
    - findByPhoneNumber(phone): Optional<User>
    - existsByEmail(email): boolean
    - existsByPhoneNumber(phone): boolean
}
```

#### ContactRepository
```java
interface ContactRepository {
    - findByUser(user): List<Contact>
    - findByUser(user, Pageable): Page<Contact>
    - findByIdAndUser(id, user): Optional<Contact>
    - searchContacts(user, searchTerm, Pageable): Page<Contact>
}
```

**JPA Entity Relationships:**

```
User (1) ──── (M) Contact
         └─────────────┬
                       │
         ┌─────────────┴──────────────┐
         │                            │
    ContactEmail (M)            ContactPhone (M)
```

**Key Features:**
- Lazy loading for collections (prevents N+1 queries)
- Cascade delete from Contact to Emails/Phones
- Indexes on frequently searched columns
- Pagination support for large datasets

**JPQL Query Example:**
```sql
SELECT c FROM Contact c 
WHERE c.user = :user 
AND (LOWER(c.firstName) LIKE LOWER(:search)
  OR LOWER(c.lastName) LIKE LOWER(:search)
  OR LOWER(c.company) LIKE LOWER(:search))
ORDER BY c.createdAt DESC
```

---

### 6. Database Layer (SQL Server)

**Schema:**

```sql
-- Users table
users (
  id BIGINT PRIMARY KEY,
  first_name NVARCHAR(255) NOT NULL,
  last_name NVARCHAR(255) NOT NULL,
  email NVARCHAR(255) UNIQUE NOT NULL,
  phone_number NVARCHAR(20) UNIQUE,
  password NVARCHAR(255) NOT NULL,
  created_at DATETIME2 DEFAULT GETDATE(),
  updated_at DATETIME2 DEFAULT GETDATE()
)

-- Contacts table
contacts (
  id BIGINT PRIMARY KEY,
  user_id BIGINT FOREIGN KEY (users.id),
  first_name NVARCHAR(255) NOT NULL,
  last_name NVARCHAR(255) NOT NULL,
  title NVARCHAR(255),
  company NVARCHAR(255),
  notes NVARCHAR(MAX),
  created_at DATETIME2 DEFAULT GETDATE(),
  updated_at DATETIME2 DEFAULT GETDATE()
)

-- Contact emails
contact_emails (
  id BIGINT PRIMARY KEY,
  contact_id BIGINT FOREIGN KEY (contacts.id) CASCADE,
  email NVARCHAR(255) NOT NULL,
  label NVARCHAR(50) DEFAULT 'Personal'
)

-- Contact phones
contact_phones (
  id BIGINT PRIMARY KEY,
  contact_id BIGINT FOREIGN KEY (contacts.id) CASCADE,
  phone_number NVARCHAR(20) NOT NULL,
  label NVARCHAR(50) DEFAULT 'Personal'
)
```

**Indexes:**
- users(email) - Fast email lookups
- users(phone_number) - Fast phone lookups
- contacts(user_id) - Fast user contact filtering
- contacts(first_name, last_name) - Fast searching
- contact_emails(contact_id) - Fast email retrieval
- contact_phones(contact_id) - Fast phone retrieval

---

## Data Flow Diagrams

### User Registration Flow
```
1. User submits registration form
   ↓
2. Frontend validates form (password regex)
   ↓
3. POST /api/auth/register with RegisterRequest
   ↓
4. AuthController.register()
   ↓
5. AuthService validates:
   - Password confirmation match
   - Password strength
   - Email not in use
   - Phone not in use
   ↓
6. Encode password with BCrypt
   ↓
7. Save User to database
   ↓
8. Generate JWT token via JwtTokenProvider
   ↓
9. Return AuthResponse with token
   ↓
10. Frontend stores token in localStorage
    ↓
11. Redirect to Dashboard
```

### Contact Creation Flow
```
1. User opens ContactForm modal
   ↓
2. User fills form and clicks Save
   ↓
3. Frontend validates form
   ↓
4. POST /api/contacts with ContactRequest
   ↓
5. JwtAuthenticationFilter:
   - Extract token from Authorization header
   - Validate token
   - Set userId in SecurityContext
   ↓
6. ContactController.createContact(userId, request)
   ↓
7. ContactService.createContact():
   - Find User by userId
   - Create Contact entity
   - Create ContactEmail entities
   - Create ContactPhone entities
   - Save all in transaction
   ↓
8. Convert Entity to ContactResponse DTO
   ↓
9. Return ContactResponse
   ↓
10. Frontend updates contacts list
    ↓
11. Close modal and show success message
```

### Contact Search Flow
```
1. User types in search box
   ↓
2. Frontend waits 300ms (debounce)
   ↓
3. GET /api/contacts?search=term&page=0&size=10
   ↓
4. ContactController.getContacts(search, pageable)
   ↓
5. ContactService.searchContacts():
   - Execute JPQL query:
     SELECT c FROM Contact c 
     WHERE c.user = :user 
     AND (name LIKE :search OR company LIKE :search)
   ↓
6. Return Page<ContactResponse>
   ↓
7. Frontend renders results with pagination
```

---

## API Design

### RESTful Principles
- **Resource-based URLs:** /api/contacts (not /api/getContacts)
- **HTTP Methods:** GET (retrieve), POST (create), PUT (update), DELETE (remove)
- **Status Codes:**
  - 200: OK (success)
  - 201: Created (resource created)
  - 204: No Content (deletion success)
  - 400: Bad Request (validation error)
  - 401: Unauthorized (not authenticated)
  - 403: Forbidden (insufficient permissions)
  - 404: Not Found (resource missing)
  - 409: Conflict (duplicate resource)
  - 500: Internal Server Error

### Request/Response Format

**Example: Create Contact**
```json
POST /api/contacts
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...

Request:
{
  "firstName": "Jane",
  "lastName": "Smith",
  "title": "Manager",
  "company": "Tech Corp",
  "notes": "Important client",
  "emails": [
    {
      "email": "jane@work.com",
      "label": "Work"
    }
  ],
  "phones": [
    {
      "phoneNumber": "9876543210",
      "label": "Mobile"
    }
  ]
}

Response (201 Created):
{
  "id": 1,
  "firstName": "Jane",
  "lastName": "Smith",
  "title": "Manager",
  "company": "Tech Corp",
  "notes": "Important client",
  "emails": [...],
  "phones": [...],
  "createdAt": "2026-08-15T10:30:00",
  "updatedAt": "2026-08-15T10:30:00"
}
```

---

## Deployment Architecture

### Development Environment
```
Developer Machine
├── Backend: Spring Boot (localhost:8080)
├── Frontend: Vite Dev Server (localhost:5173)
└── Database: SQL Server (localhost:1433)
```

### Production Environment
```
┌─────────────────────────────────────────────────────┐
│                    Client Browser                    │
└────────────────────┬────────────────────────────────┘
                     │
              HTTPS / DNS
                     │
        ┌────────────▼───────────┐
        │   CDN / Reverse Proxy   │
        │   (Nginx / CloudFlare)  │
        └────────────┬────────────┘
                     │
        ┌────────────┴─────────────┐
        │                          │
   ┌────▼──────────┐    ┌─────────▼─────┐
   │  Frontend     │    │   Backend API  │
   │  Container    │    │   Container    │
   │  (Nginx)      │    │   (Spring)     │
   └────┬──────────┘    └─────────┬──────┘
        │                         │
        │              ┌──────────▼──────────┐
        │              │  Connection Pool    │
        │              │  (HikariCP)         │
        │              └──────────┬──────────┘
        │                         │
        │              ┌──────────▼──────────┐
        │              │  SQL Server         │
        │              │  Database           │
        │              └─────────────────────┘
        │
        └──────────────────────────────────────┐
                                               │
                              ┌────────────────▼─────┐
                              │  Load Balancer       │
                              │  (Multiple Instances)│
                              └──────────────────────┘
```

### Docker Compose Setup
```
docker-compose.yml
├── mssql service (SQL Server 2019)
├── backend service (Spring Boot API)
└── frontend service (Nginx + React)
```

---

## Performance Considerations

### Frontend Optimization
1. **Code Splitting:** Vite auto-splits react-vendor, ui-vendor, http-vendor
2. **Lazy Loading:** Routes loaded on demand
3. **Caching:** Static assets cached with versioned filenames
4. **Compression:** Gzip enabled in Nginx
5. **Minification:** CSS and JavaScript minified in build

### Backend Optimization
1. **Connection Pooling:** HikariCP with configurable pool size
2. **Database Indexes:** On email, phone, user_id, search columns
3. **Pagination:** Default 10 items per page
4. **Lazy Loading:** Related entities loaded on access
5. **Async Logging:** Logback async appenders reduce I/O

### Database Optimization
1. **Query Optimization:** JPQL with WHERE clauses and indexing
2. **Pagination:** OFFSET/FETCH for large result sets
3. **Cascade Delete:** Automatic cleanup of related data
4. **Connection Pooling:** Reuse database connections

---

## Security Architecture

### Authentication
- JWT tokens with 24-hour expiration
- BCrypt password hashing (cost factor: 10)
- Token stored in browser localStorage
- Token sent in Authorization header

### Authorization
- Spring Security enforces endpoint protection
- Ownership verification in services (users can only access own data)
- Role-based access control (extensible for future roles)

### Data Protection
- HTTPS/TLS for all communication
- SQL injection prevention via parameterized queries
- XSS prevention via React's built-in escaping
- CSRF disabled (stateless API)
- Security headers in Nginx (X-Content-Type-Options, X-Frame-Options)

### Network Security
- CORS whitelist (configurable by environment)
- Firewall rules (production only)
- Rate limiting (future enhancement)
- WAF (Web Application Firewall) (future enhancement)

---

## Scalability Strategy

### Horizontal Scaling
```
Load Balancer
├── Backend Instance 1 (8080)
├── Backend Instance 2 (8080)
├── Backend Instance 3 (8080)
└── Shared Database Connection Pool
```

### Database Scaling
1. **Read Replicas:** For reporting and analytics
2. **Connection Pooling:** Increased pool size with multiple instances
3. **Caching Layer:** Redis for frequently accessed data (future)
4. **Database Indexing:** Proper indexes on all query columns

### Frontend Scaling
1. **CDN Distribution:** Static assets served from nearest edge
2. **Multiple Instances:** Behind load balancer
3. **Caching Strategy:** Long expiration on static assets

---

## Future Enhancements

### Architecture Improvements
- Implement CQRS pattern for read/write separation
- Add message queue (RabbitMQ/Kafka) for async operations
- Implement Redis cache layer
- API Gateway pattern with Spring Cloud Gateway
- GraphQL as alternative to REST API
- Event sourcing for audit trail

### Scalability Enhancements
- Kubernetes deployment
- Service mesh (Istio) for microservices
- Database sharding for multi-tenancy
- Document store (MongoDB) for audit logs

### Feature Additions
- Contact groups and categories
- Real-time updates via WebSocket
- File attachments
- Contact photos
- Integration with email/calendar
- Advanced analytics and reporting

---

## Glossary

| Term | Definition |
|------|-----------|
| DTO | Data Transfer Object - class for API request/response |
| JPA | Java Persistence API - ORM standard |
| JWT | JSON Web Token - stateless authentication |
| ORM | Object-Relational Mapping - database abstraction |
| CORS | Cross-Origin Resource Sharing - browser security |
| CSRF | Cross-Site Request Forgery - security attack |
| XSS | Cross-Site Scripting - security attack |
| Pagination | Dividing large result sets into pages |
| Lazy Loading | Delaying entity loading until accessed |
| N+1 Query | Performance problem with multiple queries |

---

**Last Updated:** August 2026
**Version:** 1.0.0
