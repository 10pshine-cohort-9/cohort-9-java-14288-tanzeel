# Contact Management System

Contact Management System is a full-stack application for securely managing personal contacts. It includes a Spring Boot REST API, a React frontend, JWT-based authentication, and SQL Server persistence.

## Features

- User registration and login
- JWT-based authentication and protected routes
- Create, view, update, and delete contacts
- User profile management
- Request validation and centralized error handling
- Docker Compose setup for the database, backend, and frontend
- Automated backend tests and JaCoCo coverage reporting

## Technology Stack

### Backend

- Java 17
- Spring Boot 3.2
- Spring Web, Spring Security, Spring Data JPA, and Bean Validation
- JSON Web Tokens (JWT)
- Microsoft SQL Server, with H2 available at runtime for lightweight use
- Maven

### Frontend

- React 18
- Vite
- React Router
- Axios
- Bootstrap and React Bootstrap

## Project Structure

```text
backend/       Spring Boot REST API and tests
database/      SQL Server database initialization scripts
frontend/      React and Vite client application
docker-compose.yml  Local multi-container development environment
```

## Running with Docker Compose

Docker Desktop is required. From the project root, run:

```bash
docker compose up --build
```

The applications will be available at:

- Frontend: http://localhost
- Backend API: http://localhost:8080
- SQL Server: localhost:1433

To stop the services, run:

```bash
docker compose down
```

## Running Locally

### Backend

Requirements: Java 17 and Maven.

```bash
cd backend
mvn spring-boot:run
```

### Frontend

Requirements: Node.js and npm.

```bash
cd frontend
npm install
npm run dev
```

The Vite development server runs on the port shown in the terminal. Configure the API base URL using `VITE_API_BASE_URL` when required.

## Testing

Run the backend test suite with:

```bash
cd backend
mvn test
```

Build the frontend with:

```bash
cd frontend
npm run build
```

## Configuration

Runtime settings are defined in `backend/src/main/resources/application.properties` and can be overridden with environment variables. Do not commit real credentials, JWT secrets, or production connection strings. Use `.env.example` as a starting point for local configuration.

## Assignment

This project was completed for Cohort 9 JAVA Fullstack (Java and ReactJS).
