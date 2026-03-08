# Task Management API

A RESTful API for task management with JWT authentication built using Spring Boot and MySQL.

## Features

- User registration and login with JWT authentication
- Password hashing with BCrypt
- CRUD operations for tasks
- User-specific task isolation
- Pagination support
- Input validation
- Comprehensive error handling

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- MySQL 8.0
- JWT (JSON Web Tokens)
- Maven
- Lombok

## Prerequisites

- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+

## Database Setup

1. Install MySQL and start the service
2. Create database (or let the application create it automatically):
```sql
CREATE DATABASE task_management_db;
```

3. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=your_username
spring.datasource.password=your_password
```

## Installation & Running

1. Clone the repository:
```bash
git clone <repository-url>
cd task-management-api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The API will start on `http://localhost:8080`

## API Endpoints

### Authentication

#### Register User
```
POST /api/auth/register
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "John Doe",
  "email": "john@example.com"
}
```

#### Login
```
POST /api/auth/login
Content-Type: application/json

{
  "email": "john@example.com",
  "password": "password123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "John Doe",
  "email": "john@example.com"
}
```

### Tasks (Requires Authentication)

All task endpoints require the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

#### Create Task
```
POST /api/tasks
Content-Type: application/json

{
  "title": "Complete project",
  "description": "Finish the task management API",
  "dueDate": "2024-12-31",
  "status": "PENDING"
}

Response: 201 Created
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the task management API",
  "dueDate": "2024-12-31",
  "status": "PENDING"
}
```

#### Get All Tasks (with Pagination)
```
GET /api/tasks?page=0&size=10

Response: 200 OK
{
  "content": [
    {
      "id": 1,
      "title": "Complete project",
      "description": "Finish the task management API",
      "dueDate": "2024-12-31",
      "status": "PENDING"
    }
  ],
  "pageable": {...},
  "totalPages": 1,
  "totalElements": 1,
  "size": 10,
  "number": 0
}
```

#### Get Single Task
```
GET /api/tasks/{id}

Response: 200 OK
{
  "id": 1,
  "title": "Complete project",
  "description": "Finish the task management API",
  "dueDate": "2024-12-31",
  "status": "PENDING"
}
```

#### Update Task
```
PUT /api/tasks/{id}
Content-Type: application/json

{
  "title": "Complete project - Updated",
  "description": "Finish the task management API with tests",
  "dueDate": "2024-12-31",
  "status": "COMPLETED"
}

Response: 200 OK
{
  "id": 1,
  "title": "Complete project - Updated",
  "description": "Finish the task management API with tests",
  "dueDate": "2024-12-31",
  "status": "COMPLETED"
}
```

#### Delete Task
```
DELETE /api/tasks/{id}

Response: 204 No Content
```

## HTTP Status Codes

- `200 OK` - Request succeeded
- `201 Created` - Resource created successfully
- `204 No Content` - Resource deleted successfully
- `400 Bad Request` - Invalid input/validation error
- `401 Unauthorized` - Invalid credentials or missing token
- `404 Not Found` - Resource not found
- `409 Conflict` - Resource already exists (e.g., duplicate email)
- `500 Internal Server Error` - Server error

## Error Response Format

```json
{
  "error": "Error message description"
}
```

For validation errors:
```json
{
  "fieldName": "Error message",
  "anotherField": "Another error message"
}
```

## Security

- Passwords are hashed using BCrypt
- JWT tokens expire after 24 hours
- Users can only access their own tasks
- Stateless authentication (no sessions)

## Project Structure

```
src/main/java/com/taskmanager/api/
├── config/          # Security and application configuration
├── controller/      # REST controllers
├── dto/             # Data Transfer Objects
├── entity/          # JPA entities
├── exception/       # Custom exceptions and global handler
├── repository/      # Data access layer
├── security/        # JWT utilities and filters
└── service/         # Business logic
```

## Testing with Postman

1. Register a new user via `/api/auth/register`
2. Copy the JWT token from the response
3. Add the token to Authorization header for all task endpoints:
   - Type: Bearer Token
   - Token: <paste-your-token>
4. Test CRUD operations on tasks

## Notes

- The JWT secret in `application.properties` should be changed in production
- Database is created automatically if it doesn't exist
- Tables are created/updated automatically via Hibernate
- Default pagination size is 10 items per page

## License

MIT License
