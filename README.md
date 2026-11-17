Online Banking Management System
A Java Swing and MySQL-based desktop application developed to demonstrate basic online banking operations.

Project Overview
The Online Banking Management System provides separate interfaces for customers and administrators. It allows customers to manage account information, perform demo money transfers, and view transaction history. Administrators can monitor customers, accounts, and transactions.

Features

Customer Module
Customer Registration and Login
Customer Dashboard
View Account Details
Demo Money Transfer
Transaction History
Customer Profile

Admin Module
Admin Login
Admin Dashboard
View Total Customers
View Total Accounts
View Total Transactions
View All Customer Transactions

Technologies Used
Java
Java Swing (GUI)
MySQL
JDBC
Maven
IntelliJ IDEA

Project Structure
Online-Banking-Management-System/
├── src/
│   ├── connection/
│   │   └── DBConnection.java
│   ├── gui/
│   │   ├── AdminDashboard.java
│   │   ├── AdminRoleSetup.java
│   │   ├── AdminTransactionHistoryFrame.java
│   │   ├── CustomerDashboard.java
│   │   ├── CustomerProfileFrame.java
│   │   ├── LoginFrame.java
│   │   ├── RegisterFrame.java
│   │   ├── TransactionHistoryFrame.java
│   │   └── TransferFrame.java
│   └── Main.java
├── pom.xml
└── .gitignore

Setup Instructions
Install JDK and IntelliJ IDEA.
Install MySQL Server.
Create a database named online_banking.
Configure your local database connection in db.properties.
Open the project in IntelliJ IDEA as a Maven project.
Build and run Main.java.

Security Not
Database credentials are stored locally and are not included in this repository. This is an academic demonstration project and should not be used for real banking or financial transactions.

Team Members
Add the names of all three project members here.

Project Status
Academic project – Development version.
