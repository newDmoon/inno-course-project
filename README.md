# 🏗️ Microservices Architecture

This project implements a **microservices-based system**.  
All services communicate through the **API Gateway**, and some interactions are asynchronous via **Kafka**.
---

## 📌 Architecture Overview

- **Frontend (ReactJS)** – user interface.
- **API Gateway** – single entry point for clients, request routing, and aggregation.
- **Authentication Service** – handles authentication and authorization.
- **User Service** – user management (REST API + Redis caching).
- **Order Service** – order management.
- **Payment Service** – payment handling (integration with external APIs).
- **Kafka** – message broker for asynchronous communication.
- **Databases**:
    - PostgreSQL – authentication, users, and orders.
    - MongoDB – payments.
    - Redis – caching for User Service.

---

## ⚙️ Tech Stack

- **Backend**: Java (Spring Boot)
- **Frontend**: ReactJS
- **Message Broker**: Kafka
- **Databases**:
    - PostgreSQL
    - MongoDB
    - Redis
- **Communication**: REST + Event-driven

---
