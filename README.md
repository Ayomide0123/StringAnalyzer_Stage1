# Stage 1 Task: Build a String Analyzer Service

## String Analyzer REST API

A **Spring Boot** RESTful service that analyzes strings and stores their computed properties.
It provides endpoints for creating, retrieving, filtering, and deleting strings, including **natural language filtering** like “all single word palindromic strings.”

---

## Features

* Analyze and store strings with computed properties (e.g., palindrome, length, word count).
* Retrieve all stored strings with advanced filtering.
* Filter strings using **natural language queries**.
* Delete strings by value.
* Input validation and structured error handling.

---

## 🧩 Tech Stack

* **Java 17+**
* **Spring Boot 3+**
* **Maven** (for dependency management)
* **Spring Web** – for RESTful endpoints
* **Spring Validation** – for request validation
* **Lombok** – to reduce boilerplate code
* **Jackson** – for JSON serialization
* **H2 (in-memory)** or **HashMap** (depending on implementation) – as the data store

---

## Setup Instructions (Local)

### 1️. Clone the Repository

```bash
git clone https://github.com/Ayomide0123/StringAnalyzer_Stage1.git
cd StringAnalyzer_Stage1
```

### 2️. Open in your IDE

Use **IntelliJ IDEA**, **VS Code**, or **Eclipse**.
Make sure you have the **Java SDK (21 or later)** installed.

Check your Java version:

```bash
java -version
```

### 3️. Build the Project

```bash
mvn clean install
```

### 4️. Run the Application

Run using Maven:

```bash
mvn spring-boot:run
```

or directly from your IDE, run the `StringAnalyzerApplication.java` class.

The app starts at: **[http://localhost:8080](http://localhost:8080)**

---

## Environment Variables

Create a `.env` file in the project root:

| Variable           | Description                            | Default                                     |
| ------------------ | -------------------------------------- |---------------------------------------------|
| `DB_URL`      | JDBC connection string for your database                    | `jdbc:postgresql://localhost:5432/stringdb` |
| `DB_USERNAME`          | Database username | `mysql`                                     |
| `DB_PASSWORD` | Database password   | `password123`                                    |

---

## API Endpoints Overview

### 1. **POST /strings**

Analyze and store a new string.
**Body:**

```json
{
  "value": "racecar"
}
```

**Response:**

```json
{
  "id": "e00f9ef51a95f6e854862eed28dc0f1a68f154d9f75ddd841ab00de6ede9209b",
  "value": "racecar",
  "properties": {
    "length": 7,
    "is_palindrome": true,
    "unique_characters": 4,
    "word_count": 1,
    "sha256_hash": "e00f9ef51a95f6e854862eed28dc0f1a68f154d9f75ddd841ab00de6ede9209b",
    "character_frequency_map": {
      "r": 2,
      "a": 2,
      "c": 2,
      "e": 1
    }
  },
  "created_at": "2025-10-21T13:47:53.6876575"
}
```

---

### 2. **GET /strings**

Retrieve all strings with filters.
Supports:

```
is_palindrome, min_length, max_length, word_count, contains_character
```

Example:
`GET /strings?is_palindrome=true&min_length=5&contains_character=a`

---

### 3. **GET /strings/filter-by-natural-language**

Query strings using plain English.

Example:
`GET /strings/filter-by-natural-language?query=all single word palindromic strings`

---

### 4. **DELETE /strings/{value}**

Delete a string from the system.
Returns `204 No Content` if successful.

## Author

**Oyetimehin Ayomide**
* 📧 [oyetimehin31@gmail.com](mailto:oyetimehin31@gmail.com)
* 💻 Backend Stack: Java / Spring Boot