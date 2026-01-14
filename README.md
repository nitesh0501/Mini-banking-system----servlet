Mini-Banking Application
This project implements a basic banking system,common financial transactions. The application is built using Java Servlets, deployed on Apache Tomcat, and utilizes MySQL as the database management system. 

Key Features
User Authentication: Secure signup and login processes.

Account Management: Functionality for depositing and withdrawing funds.

Transaction Tracking: A dedicated page to view account transaction history. 


Technology Stack

Backend: Java Servlet

Server: Apache Tomcat

Database: MySQL

Database Migration: Liquibase

Code Quality: SonarQube

Testing: JUnit (for Unit Testing) 

Project Structure
The application is structured around several Servlets and Filters to manage requests and enforce security measures. 
Servlets

SignupServlet: Handles new user registration.

LoginServlet: Manages user authentication and session creation. 

DashboardServlet: Displays the user's account summary. 

DepositServlet: Processes fund deposits into the user's account.

WithdrawalServlet: Manages fund withdrawals from the user's account.

TransactionServlet: Retrieves and displays the user's transaction history. 

Filters
Filters are used to secure access to specific application paths, ensuring that only authenticated users can access sensitive pages and operations. 

DashboardFilter
DepositFilter
WithdrawalFilter
TransactionFilter
