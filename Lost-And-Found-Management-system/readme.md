# 📦 Lost & Found Platform (Spring Boot)

A simple Lost & Found web application built with Spring Boot. Users can post lost or found items, claim items, and manage claim approvals. The system is built using Spring MVC, Spring Data JPA, Thymeleaf, and MySQL.

---

## 🚀 Tech Stack

- Java 17+
- Spring Boot
- Spring Web (MVC)
- Spring Data JPA
- Thymeleaf (Frontend templates)
- MySQL
- Maven

---

## 📁 Project Structure

```
com.dede.lostfound
│
├── controller      → Handles HTTP requests
├── service         → Business logic layer
├── repository      → Database access layer
├── entity          → Database models (JPA entities)
└── LostFoundApplication.java
```

---

## 🧠 Core Features Implemented

### 1. Item Management
- Users can post **Lost** or **Found** items
- Items contain:
  - Name
  - Description
  - Location
  - Type (LOST / FOUND)
  - Posting user (future improvement)

---

### 2. Claim System
- Users can claim an item they believe belongs to them
- Each claim contains:
  - Message (proof or explanation)
  - Status: `PENDING`, `APPROVED`, `REJECTED`
  - Linked **User**
  - Linked **Item**
  - Timestamp of creation

---

### 3. Claim Review System
- Item owners (or admin in current version) can:
  - Approve a claim
  - Reject a claim
- Status updates are persisted in the database
- Claims are displayed under each item detail page

---

### 4. Item Detail View
- Each item has a dedicated page showing:
  - Item details
  - All related claims
  - Claim status updates in real time (after refresh)

---

## 🔁 Current Application Flow

1. User views all items
2. User clicks an item to view details
3. User submits a claim on an item
4. Claim is stored as **PENDING**
5. Claim is reviewed and:
   - Approved OR
   - Rejected
6. Updated status is shown under item details

---

## 🗄️ Database Design (Current State)

Entities:

- **User**
  - id, name, email, password

- **Item**
  - id, name, description, location, type, user

- **Claim**
  - id, message, status, user, item, createdAt

- **Match** (optional / future use)
  - id, lostItem, foundItem, similarityScore

---

## ⚙️ Configuration

MySQL database used:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/lostfound_db
spring.datasource.username=root
spring.datasource.password=yourpassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## 📌 What Has Been Learned So Far

- Spring Boot project structure (MVC architecture)
- Entity relationships (OneToMany, ManyToOne)
- Repository pattern using Spring Data JPA
- Service layer separation for business logic
- Thymeleaf templating for dynamic UI
- Basic CRUD operations with database integration

---

## 🧱 Known Limitations (Current Version)

These are intentionally left for future improvement:

- No authentication system yet (fake user used for now)
- No role-based access (admin vs user)
- No validation on forms
- No pagination or filtering
- No real-time updates
- No matching intelligence logic yet

---

## 🚀 Future Improvements

Planned upgrades:

### 🔐 Authentication & Security
- User login / signup
- Spring Security integration
- Session-based access control

### 🧑‍💼 Roles
- Admin role (approve/reject claims properly)
- Regular users (post items, make claims)

### 🤖 Matching System
- Auto-suggest matches between lost and found items
- Similarity scoring algorithm (based on text/location)

### 📊 UI Improvements
- Better dashboard design
- Item filtering (lost vs found)
- Search functionality

### ⚡ Backend Enhancements
- DTO layer (clean API separation)
- Validation (Spring Validation)
- REST API version of the system

---

## 🧠 Notes for Developers

This project is structured for learning Spring Boot fundamentals:
- Keep business logic inside services
- Keep controllers thin
- Let repositories handle database only
- Avoid putting logic inside entities

---

## 📍 Author

Built as a learning project to understand full-stack Spring Boot development.

