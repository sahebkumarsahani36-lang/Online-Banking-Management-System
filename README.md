# Online Banking Management System
A Java Swing and MySQL-based desktop application developed to demonstrate basic online banking operations.

## Project Overview
The Online Banking Management System provides separate interfaces for customers and administrators. Customers can manage account information, perform demo money transfers, and view transaction history. Administrators can monitor customers, accounts, and transactions.

## Features
### Customer Module
- Customer Registration and Login
- Customer Dashboard
- View Account Details
- Demo Money Transfer
- Transaction History
- Customer Profile

### Admin Module
- Admin Login
- Admin Dashboard
- View Total Customers
- View Total Accounts
- View Total Transactions
- View All Customer Transactions

## Technologies Used
- Java
- Java Swing
- MySQL
- JDBC
- Maven
- IntelliJ IDEA

## Team Members
1. **Saheb Kumar Sahni** – Admin
2. **Aryan Chaudhary** – Member
3. **Akshay Sharma** – Member

## Project Structure
```text
Online-Banking-Management-System/
src/
connection/
 gui/
Main.java
pom.xml
 .gitignore
```

## Setup Instructions
1. Install JDK and IntelliJ IDEA.
2. Install MySQL Server.
3. Create a database named `online_banking`.
4. Configure local database credentials in `db.properties`.
5. Open the project as a Maven project.
6. Build and run `Main.java`.

## Security Note
Database credentials are stored locally and are not included in this repository. This is an academic demonstration project and must not be used for real banking or financial transactions.

## Project Status
Academic Project – Development Version.
