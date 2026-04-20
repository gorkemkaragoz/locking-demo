# locking-demo

A basic project for Optimistic and Pessimistic Locking with Spring Boot and PostgreSQL.

## Tech Stack
- Java 21, Spring Boot 3.3, Spring Data JPA, PostgreSQL, Lombok

## Setup

1. Create the database:

```sql
CREATE DATABASE locking_demo;
```

2. Create a `.env` file in the project root:

```
DB_URL=jdbc:postgresql://localhost:5432/locking_demo
DB_USERNAME=your_username
DB_PASSWORD=your_password
```

3. Run the application.

## Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/accounts/init` | Create test accounts |
| GET | `/api/accounts` | List all accounts |
| POST | `/api/accounts/transfer/optimistic` | Test optimistic locking |
| POST | `/api/accounts/transfer/pessimistic` | Test pessimistic locking |

## Expected Behavior

**Optimistic:** One thread succeeds, the other gets `ObjectOptimisticLockingFailureException`.

**Pessimistic:** Both threads succeed. The second waits for the first to release the lock.
