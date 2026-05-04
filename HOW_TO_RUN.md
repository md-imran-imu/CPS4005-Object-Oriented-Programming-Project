# St Mary’s Digital Library System

An advanced Library Management System developed for St Mary’s University. This application provides a robust, thread-safe solution for managing book inventories, member records, and borrowing transactions using Java Swing and SQLite.


## Prerequisites

To compile and run this software locally, the host machine must have:
* **Java Development Kit (JDK) 22** or higher installed and configured in the system `PATH`.
* **Command Prompt / Terminal** access.

*Note: The required SQLite JDBC driver is already included in the `lib` folder. No external downloads are necessary.*

## How to Run the Application

Please follow these exact steps from the terminal or command prompt.

### Step 1: Open the Project Directory
Navigate to the root folder of the unzipped project (where this README is located):
```bash
cd CPS4005-OOP-PROJECT
```

### Step 2: Compile the Java Source Code
Compile the application by creating a `bin` directory and linking the SQLite JDBC driver. Run this exact command:
```bash
javac -d bin -cp "lib/sqlite-jdbc-3.53.0.0.jar" src/library/*.java
```
*This will generate the required `.class` files inside a newly created `bin` folder.*

### Step 3: Execute the Program
Launch the application using the appropriate command for your operating system:

**For Windows (CMD or PowerShell):**
```cmd
java -cp "bin;lib/sqlite-jdbc-3.53.0.0.jar" library.Main
```

**For macOS / Linux:**
```bash
java -cp "bin:lib/sqlite-jdbc-3.53.0.0.jar" library.Main
```

---

## ⚠️ Important Troubleshooting Note
If the application throws a **"Database is locked"** error during any write operations (like adding a book or issuing a loan), please ensure that the `st_marys_library.db` file is **not currently open** in SQLite Studio or any other external database viewer.