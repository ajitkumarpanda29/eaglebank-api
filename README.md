
# 🏦 EagleBank API

A secure and modular Spring Boot REST API for managing users, bank accounts, and transactions in a simple banking system.

---

## 🚀 Features

- User registration, retrieval, update, and deletion
- Bank account creation, listing, update, and deletion
- Deposit and withdrawal transactions with balance checks
- Ownership and authorization checks using Spring Security
- Custom exception handling with informative error responses
- Logging with SLF4J for traceability

---

## 🛠️ Technologies Used

- Java 17+
- Spring Boot 3.x
- Spring Data JPA
- Spring Security
- H2 (in-memory DB for testing) / PostgreSQL (production-ready)
- Maven
- Lombok
- JUnit 5 & Mockito

---

## 📦 Project Structure

src/
├── main/
│ ├── java/
│ │ └── com.bclay.eaglebank_api/
│ │ ├── controller/ # REST controllers
│ │ ├── service/ # Business logic
│ │ ├── model/ # Entity classes
│ │ ├── repository/ # JPA repositories
│ │ ├── exception/ # Custom exceptions
│ │ └── EaglebankApiApplication.java
│ └── resources/
│ ├── application.yml
│ └── schema.sql / data.sql (optional)
└── test/
└── java/ # Unit and integration tests


---

## 📚 API Endpoints

### 🔐 Users
| Method | Endpoint             | Description         |
|--------|----------------------|---------------------|
| POST   | `/v1/users`          | Create a new user   |
| GET    | `/v1/users/{id}`     | Get user by ID      |
| PUT    | `/v1/users`          | Update user         |
| DELETE | `/v1/users/{id}`     | Delete user         |

### 💳 Bank Accounts
| Method | Endpoint                         | Description                   |
|--------|----------------------------------|-------------------------------|
| POST   | `/v1/accounts`                   | Create a new account          |
| GET    | `/v1/accounts/{id}`              | Get account (authorized)      |
| PUT    | `/v1/accounts/{id}`              | Update account (authorized)   |
| DELETE | `/v1/accounts/{id}`              | Delete account (authorized)   |
| GET    | `/v1/users/{userId}/accounts`    | List accounts by user ID      |

### 💸 Transactions
| Method | Endpoint                                                       | Description                        |
|--------|----------------------------------------------------------------|------------------------------------|
| POST   | `/v1/accounts/{accountId}/transactions`                        | Create deposit or withdrawal       |
| GET    | `/v1/accounts/{accountId}/transactions`                        | List transactions for an account   |
| GET    | `/v1/accounts/{accountId}/transactions/{transactionId}`        | Get transaction details            |

---

## ✅ Security Notes
Uses Spring Security for authentication/authorization.

API is structured to enforce user ownership of bank accounts and transactions.

Passwords are hashed using PasswordEncoder.