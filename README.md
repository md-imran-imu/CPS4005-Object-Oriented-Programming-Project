## St Mary’s Digital Library System
### Project Overview

The St Mary’s Digital Library System is a robust, desktop-based management solution designed to streamline the operations of a university library. Developed using Java and SQLite, the system adheres to strict Object-Oriented Programming (OOP) principles to ensure scalability, data integrity, and a professional user experience for library staff.

---
### Key Features
1. #### Unified Inventory Management
* Comprehensive CRUD: Full lifecycle management for the book collection, including adding new titles, updating categories, and removing obsolete records.

* Data Integrity: Implements logic to prevent duplicate entries of the same title and author combination.

* Real-time Availability: Automated status tracking (Available vs. Borrowed) that updates instantly upon loan transactions. 

2. Member & Personnel Database
* Role-Based Memberships: Support for different membership tiers, including Students and Staff.

* Smart Validation: Integrated Regular Expression (Regex) patterns to ensure all registered emails follow the standard user@domain.com format.

* Input Enforcement: Strict "no-empty-field" policies to maintain a clean and reliable database.

3. Advanced Borrowing & Loan Tracking
* User-Friendly Date Selection: Replaces manual typing with a professional JSpinner date-picker, reducing human error in return-date entry.

* Logical Constraints: Automated validation ensures the "Due Date" cannot be set before the "Issue Date."

* Record Filtering: Advanced search functionality allows staff to filter borrowing history by specific date ranges and keywords.
4. Professional Software Architecture
* Encapsulation & OOP: Core entities are modeled as discrete classes (Book, Member, BorrowRecord) with private attributes and public interfaces.

* Thread-Safe UI: Table refreshes are handled asynchronously on the Event Dispatch Thread (EDT) to prevent interface freezing.

* Custom Exception Handling: A centralized LibraryException class provides meaningful, context-specific error messages to the user.
---

### Project Structure
```bash
CSE4005-OOP-PROJECT/
├── bin/                    # Compiled Java bytecode (.class files)
├── lib/                    # External dependencies
│   └── sqlite-jdbc-3.53.0.0.jar
├── src/library/            # Source code package
│   ├── Main.java             # System entry point
│   ├── LibraryGUI.java       # Swing-based presentation layer
│   ├── DatabaseHandler.java  # SQLite Data Access Object (DAO)
│   ├── Book.java             # Entity model for inventory
│   ├── Member.java           # Entity model for users
│   ├── BorrowRecord.java     # Entity model for loans
│   └── LibraryException.java # Specialized error handling
├── st_marys_library.db     # Production SQLite database
├── schema.sql              # SQL script for table definitions
└── git_log.txt             # Exported version control history
```

---

### Getting Started
#### Prerequisites
* Java Development Kit (JDK) 22 or higher.

* Terminal/Command Prompt access.

1. #### Clone & Setup
Navigate to your project directory:
```bash
cd CPS4005-Object-Oriented-Programming-Project
```

2. #### Compilation
Compile the project using the SQLite driver in the classpath:
```bash
javac -d bin -cp "lib/sqlite-jdbc-3.53.0.0.jar" src/library/*.java
```

2. #### Launching the Application
Execute the compiled bytecode:
```bash
java -cp "bin;lib/sqlite-jdbc-3.53.0.0.jar" library.Main
```





---
