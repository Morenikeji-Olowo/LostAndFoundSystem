# Lost and Found Management System

A centralised web platform for university students and staff to report lost items and found items — making the chaotic group chat approach a thing of the past.

## The Problem

Lost and found on campus has always been disorganised — scattered WhatsApp groups, notice boards, and word of mouth. This system brings everything into one place so students can quickly report what they've lost or found, and make claims on items.

## Features

- Report a lost item with details and images
- Report a found item and submit it to the system
- Browse and search all reported items
- Make a claim on an item you believe is yours
- User registration and login
- Admin management of reports

## Tech Stack

- **Backend** — Java 21, Spring Boot 3
- **Database** — PostgreSQL
- **Frontend** — Thymeleaf, HTML, CSS
- **Deployment** — Render (web service + managed PostgreSQL)

## Getting Started

### Prerequisites

- Java 21
- PostgreSQL
- Maven

### Running Locally

1. Clone the repository:
   ```bash
   git clone https://github.com/Morenikeji-Olowo/LostAndFoundSystem.git
   cd LostAndFoundSystem/Lost-And-Found-Management-system
   ```

2. Create a PostgreSQL database:
   ```sql
   CREATE DATABASE lostandfoundDb;
   ```

3. Update `src/main/resources/application.properties` with your local database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/lostandfoundDb
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```

5. Visit `http://localhost:8080` in your browser.

## Deployment

The application is deployed on [Render](https://render.com) using Docker.

Environment variables required:
| Variable | Description |
|---|---|
| `DATABASE_URL` | JDBC connection URL for PostgreSQL |
| `DB_USER` | Database username |
| `DB_PASSWORD` | Database password |

## Planned Improvements

- Spring Security for proper role-based access control
- In-app messaging so claimants and finders can connect directly
- Email notifications when a match is found
- Image uploads for item reports
- Advanced search and filtering

## Author

Morenikeji Olowo  
Computer Science — University Project (COS 202)
