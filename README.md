# 💳 Banking Core Platform

## 🇬🇧 English

### 📌 Overview

**Banking Core Platform** is a modern **microservices-based backend system** that simulates real-world banking operations.
It demonstrates how to build **scalable, secure, and resilient distributed systems** using industry-standard practices.

The platform includes core banking workflows such as:

* Customer onboarding
* Account management
* Internal money transfers
* Payment processing
* Notifications & audit logging

---

### 🎯 Goals

* Build production-ready microservices architecture
* Simulate real banking workflows
* Apply clean architecture and design patterns
* Implement event-driven communication
* Ensure system reliability and fault tolerance

---

### 🏗 Architecture

The system follows a **microservices architecture** with clearly separated responsibilities.

#### Core Services:

* API Gateway
* Auth Service
* Customer Service
* Account Service
* Transaction Service
* Payment Service
* Notification Service
* Audit Service
* Fraud Service *(planned)*

---

### ⚙️ Tech Stack

* Java 21
* Spring Boot 3
* Spring Cloud Gateway
* Spring Security (**JWT-based authentication**)
* Spring WebClient
* Apache Kafka
* PostgreSQL
* Redis
* **Liquibase (database migrations)**
* Resilience4j
* Docker & Docker Compose

---

### 🔐 Security

This platform uses **JWT-based authentication and authorization**.

* Stateless authentication
* Access & Refresh tokens
* Role-based access control (RBAC)
* Token validation at API Gateway
* Secure inter-service communication

> No external identity provider (e.g., Keycloak) is used. Authentication is handled internally via Auth Service.

---

### 🗄 Database Management

**Liquibase** is used for managing database schema changes.

* Version-controlled migrations
* Consistent schema across environments (dev, staging, production)
* Rollback support for safer deployments
* Centralized schema evolution

---

### 🔄 Communication

* **Synchronous:** REST (WebClient)
* **Asynchronous:** Apache Kafka

The system is designed using an **event-driven architecture**.

---

### 🔑 Key Features

* Secure JWT authentication
* Idempotent transaction processing
* Distributed transaction handling (Saga pattern - simplified)
* Retry & Circuit Breaker (Resilience4j)
* Event-driven notifications
* Full audit logging

---

### ▶️ Getting Started

```bash
git clone https://github.com/your-username/banking-core-platform.git
cd banking-core-platform
docker-compose up -d
```

---

### 📁 Project Structure

```
banking-core-platform/
│
├── docs/
├── services/
├── docker/
├── scripts/
├── docker-compose.yml
└── README.md
```

---

### 📊 Roadmap

* Fraud detection service
* Admin panel
* Reporting & analytics
* Observability (Prometheus + Grafana)
* Distributed tracing

---

## 🇦🇿 Azərbaycan dili

### 📌 Ümumi məlumat

**Banking Core Platform** real bank sistemlərinə yaxın **microservice əsaslı backend platformadır**.

Bu platforma aşağıdakı prosesləri əhatə edir:

* Müştəri qeydiyyatı
* Hesabların idarə olunması
* Daxili pul köçürmələri
* Ödənişlərin icrası
* Bildiriş və audit sistemi

---

### 🎯 Məqsəd

* Production səviyyəli microservice arxitektura qurmaq
* Real bank proseslərini modelləşdirmək
* Design pattern-ləri tətbiq etmək
* Event-driven yanaşmanı istifadə etmək
* Dayanıqlı və scalable sistem yaratmaq

---

### 🏗 Arxitektura

Sistem **microservice arxitekturası** üzərində qurulub.

#### Əsas servislər:

* API Gateway
* Auth Service
* Customer Service
* Account Service
* Transaction Service
* Payment Service
* Notification Service
* Audit Service
* Fraud Service *(planlaşdırılır)*

---

### ⚙️ Texnologiyalar

* Java 21
* Spring Boot 3
* Spring Cloud Gateway
* Spring Security (**JWT əsaslı**)
* WebClient
* Apache Kafka
* PostgreSQL
* Redis
* **Liquibase (database migration idarəetməsi)**
* Resilience4j
* Docker

---

### 🔐 Təhlükəsizlik

Sistem **JWT əsaslı authentication və authorization** istifadə edir.

* Stateless authentication
* Access və Refresh token-lər
* Role-based access control
* Gateway səviyyəsində token yoxlanışı
* Servislərarası təhlükəsiz kommunikasiya

> Keycloak istifadə olunmur — authentication daxili Auth Service ilə idarə olunur.

---

### 🗄 Database idarəetməsi

**Liquibase** istifadə olunur.

* Database dəyişiklikləri versiyalaşdırılır
* Bütün mühitlərdə eyni schema təmin olunur
* Rollback imkanı mövcuddur
* Struktur dəyişikliklər nəzarətdə saxlanılır

---

### 🔄 Kommunikasiya

* **Sync:** REST API
* **Async:** Kafka

---

### 🔑 Əsas xüsusiyyətlər

* JWT ilə təhlükəsiz giriş
* Idempotent transaction sistemi
* Retry və Circuit Breaker
* Event-driven notification
* Audit logging
* Scalable və loose-coupled arxitektura

---

### ▶️ İşə salmaq

```bash
git clone https://github.com/your-username/banking-core-platform.git
cd banking-core-platform
docker-compose up -d
```

---

### 📊 Gələcək inkişaf

* Fraud detection
* Admin panel
* Monitoring (Prometheus, Grafana)
* Reporting sistemi
* Distributed tracing

---
