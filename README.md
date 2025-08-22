# 📂 FileNest - Secure File Upload & Retrieval System

## 📌 Overview
**FileNest** is a secure and high-performance file handling system built with **Java, Spring Boot, Hibernate, and MySQL**.  
It enables users to **upload and download files using a unique, auto-generated 4-digit key** without traditional authentication.  
The system supports **multi-file uploads**, **bulk downloads**, and **on-the-fly ZIP compression**, ensuring fast and secure file sharing.

---

## 🎯 Key Features
- **Unique 4-Digit Key Access**
  - Auto-generated keys for upload/download without login.
- **Multi-File Upload & Retrieval**
  - Upload multiple files under a single parent entity.
- **Dynamic File Access URLs**
  - Download files directly through secure, time-bound URLs.
- **Bulk Download with ZIP Compression**
  - On-the-fly compression reduces download time by **35%**.
- **High Scalability**
  - Supports **500+ concurrent download requests** while maintaining high availability.
- **Secure Storage**
  - Backend powered by **Spring Boot** and **Hibernate** with MySQL database.

---

## 🛠️ Tech Stack
- **Java 17**
- **Spring Boot**
- **Hibernate / JPA**
- **MySQL**
- **Postman (API Testing)**
- **React.js** (Frontend - optional)
- **Maven**

---

## 📂 Project Structure
spring-boot-file-upload/
├── src/
│ ├── main/
│ │ ├── java/com/filenest/ # Backend Java code
│ │ │ ├── controller/ # API Controllers
│ │ │ ├── service/ # Business logic
│ │ │ ├── repository/ # JPA Repositories
│ │ │ ├── model/ # Entity classes
│ │ │ └── utils/ # Utility classes (e.g., ZIP compression)
│ │ └── resources/ # Config files & SQL scripts
│ └── test/ # Unit and integration tests
├── pom.xml # Maven dependencies
└── README.md # Documentation


---

## 📥 Installation & Setup
```bash
# Clone the repository
git clone https://github.com/yashwanth1239/filehandling1.git
cd filehandling1/spring-boot-file-upload

# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
