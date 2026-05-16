# Distributed Shopping Cart Service

A production-ready, session-based shopping cart microservice built with Spring Boot and Redis. This service provides distributed session management, ensuring that user shopping carts are persistent across service restarts and multiple service instances.

## Architecture Overview

The application follows a standard layered architecture:
- **Controller**: Exposes RESTful endpoints for cart operations.
- **Service**: Implements business logic and interacts with Redis using `RedisTemplate`.
- **Model**: Defines the data structure for `Cart` and `CartItem`.
- **Redis (Infrastructure)**: Uses Redis Hashes to store cart data efficiently, allowing partial updates.

## Key Features

- **Distributed Caching**: Carts are stored in Redis, making the service stateless and horizontally scalable.
- **Efficient Updates**: Uses Redis Hashes to update individual item quantities without rewriting the entire cart.
- **Sliding Expiration**: Automatically clears abandoned carts after 30 minutes of inactivity (TTL management).
- **Performance Monitoring**: Provides real-time cache statistics, including total active sessions and hit rate.
- **Resiliency**: Handles Redis connectivity issues gracefully with a global exception handler.
- **Containerized**: Fully Dockerized for consistent development and deployment.

## API Documentation

### Shopping Cart Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/cart/{sessionId}/items` | Add or update an item in the cart. |
| GET | `/api/cart/{sessionId}` | Retrieve the current state of the cart. |
| DELETE | `/api/cart/{sessionId}/items/{productId}` | Remove a specific item from the cart. |
| DELETE | `/api/cart/{sessionId}` | Clear the entire cart. |
| GET | `/api/cart/cache-stats` | Get cache statistics (total carts, hit rate). |

### Example Request (Add Item)
```json
POST /api/cart/user-123/items
{
  "productId": "prod-001",
  "productName": "Ergonomic Chair",
  "price": 250.00,
  "quantity": 1
}
```

## Setup & Running

### Prerequisites
- Docker and Docker Compose

### Running with Docker Compose
1. Clone the repository.
2. Build and start the services:
   ```bash
   docker-compose up --build
   ```
3. The application will be available at `http://localhost:8080`.

### Environment Variables
The application uses the following environment variables (defined in `.env.example`):
- `SPRING_REDIS_HOST`: Redis server hostname (default: `redis`).
- `SPRING_REDIS_PORT`: Redis server port (default: `6379`).

## Monitoring & Health
- **Health Check**: `http://localhost:8080/actuator/health`
- **Metrics**: `http://localhost:8080/actuator/metrics`
- **Cache Stats**: `http://localhost:8080/api/cart/cache-stats`
