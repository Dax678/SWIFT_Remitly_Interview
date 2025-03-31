# Remitly Internship 2025

## 📌 About the Project
The project was created for the interview process for an internship at Remitly in 2025.

## 🏗️ Tech Stack
- **Backend**: Java, Spring Boot, Spring Data JPA, Hibernate
- **Database**: PostgreSQL
- **Testing**: JUnit, Testcontainers, MockMvc

## 🔧 Installation & Setup  
### Prerequisites  

Make sure you have downloaded xlsx file at "src/main/resources/Interns_2025_SWIFT_CODES.xlsx" path.

Ensure you have the following installed:
- Java 17+  
- Docker (for Testcontainers)  
- PostgreSQL

## 📖 API Endpoints
- **GET "/v1/swift-codes/{swift_code}"**

Retrieve details of a single SWIFT code whether for a headquarters or branches.

- **GET "/v1/swift-codes/country/{countryISO2code}"**

Return all SWIFT codes with details for a specific country (both headquarters and branches).

- **POST "/v1/swift-codes"**

Adds new SWIFT code entries to the database for a specific country.

- **DELETE "/v1/swift-codes/{swift_code}"**

Deletes swift-code data if swiftCode matches the one in the database.
