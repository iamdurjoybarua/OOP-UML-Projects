# OOP Unified Modeling Language (UML) Projects

A collection of object-oriented programming (OOP) system design examples, demonstrating various UML concepts including **Class Diagrams**, **Use Case Diagrams** (where applicable), **Classes**, **Associations**, **Generalization (Inheritance)**, **Attributes**, **Operations (Methods)**, and **Enumerations**. This repository features accompanying Java implementations for several mini-projects, showcasing how theoretical designs translate into functional code.

## Table of Contents

-   [Overview](#overview)
-   [Features](#features)
-   [Project Structure](#project-structure)
-   [Systems Covered](#systems-covered)
-   [How to View UML Diagrams](#how-to-view-uml-diagrams)
-   [How to Run Java Code](#how-to-run-java-code)
-   [Author](#author)
-   [License](#license)

## Overview

This repository serves as a practical showcase for fundamental concepts in Object-Oriented Analysis and Design (OOAD). It aims to bridge the gap between UML modeling and Java programming by providing a series of examples where system designs are first conceptualized using UML diagrams and then implemented in Java. Each example provides a clear domain model and a runnable demonstration.

## Features

-   **Comprehensive UML Coverage:** Illustrates the application of key UML elements such as classes, attributes, methods, various types of associations (aggregation, composition), and generalization (inheritance).
-   **Practical System Examples:** Includes designs and implementations for common real-world mini-systems, making the concepts relatable.
-   **Java OOP Implementations:** Clean, well-commented, and consolidated Java code demonstrating how to translate UML designs into functional object-oriented programs.
-   **Clear Relationships:** Highlights the relationships between different system components as defined by the UML diagrams.

## Project Structure

The repository is organized to clearly separate UML diagrams (as PlantUML source files and potentially rendered images) from Java source code:

## Systems Covered

This repository includes design and implementation for the following systems, each with its corresponding UML diagram(s) and Java code:

1.  **Class Diagram for ATM**
    * *Description:* This class diagram for the ATM maps out the structure and attributes of how an ATM works. It also shows the relationship between multiple classes.
    

2.  **Class Diagram for Hotel Management System**
    * *Description:* This hotel management class diagram carefully links all classes joining them together through arrows to show the relationship between them.
   

3.  **Class Diagram for Library Management System**
    * *Description:* The library management system class diagram has multiple classes like the user, librarian, book, account, etc. It then describes the attributes and operations of each of the classes linking them together for the library management system.
    

4.  **Class Diagram for Online Shopping**
    * *Description:* This online shopping class diagram shows the domain model for online shopping. This diagram will help software engineers and business analysts easily understand the diagram. The diagram links classes like user and account to show how an order is placed and then shipped.
 

5.  **Class Diagram for Hospital Management System**
    * *Description:* This domain model shows several class diagrams like a patient, staff, treatment, and the relationships between them.
  

6.  **Class Diagram for Banking System**
    * *Description:* This class diagram for a banking system shows banks, ATMs, customers, etc as different classes. The attributes are listed in the second compartment for each of these and then they are linked together showing the relationship with each.
    

7.  **Class Diagram for Student Registration System**
    * *Description:* This class diagram shows multiple classes like student, account, course registration manager, course, etc. Registration, course, and account are subclasses of the registration manager and are linked to it using a solid arrow.
    

8.  **Class Diagram for Airline Reservation System**
    * *Description:* This Airline Reservation System Class diagram template showcases the classes, their structure, attributes, operations, and relationships. The main classes in the chart shown below are Reservation, passenger, ticket booking, employee, etc.


## How to Run Java Code

To compile and run the Java demonstration applications:

1.  **Ensure Java Development Kit (JDK) is installed:** You need Java 8 or newer.
2.  **Navigate to the `src` directory** in your terminal or command prompt:
    ```bash
    cd src
    ```
3.  **Compile all Java files:**
    ```bash
    javac *.java
    ```
    *(This command will compile all `.java` files in the `src` directory.)*
4.  **Run the demonstrations:**
    ```bash
    java AirlineReservationSystemDemo
    java ATMSystemDemo
    java BankingSystemDemo
    java HospitalManagementSystem  # Corrected executable filename
    java HotelManagementSystemDemo
    java LibrarySystemDemo
    java OnlineShoppingSystem     # Corrected executable filename
    java StudentRegistrationSystem # Corrected executable filename
    ```


## Author

Durjoy Barua (https://github.com/iamdurjoybarua)

## License

This project is open-source and available under the MIT License (https://opensource.org/licenses/MIT).

