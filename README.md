# TaskApp

TaskApp is a simple JavaFX desktop application that allows users to register, log in, and manage tasks. The app uses PostgreSQL as the database backend.

## Features

- User registration and login
- Task management (create, view, update, delete tasks)
- JavaFX-based GUI

## Technologies Used

- Java 17
- JavaFX 17
- Maven
- PostgreSQL

## Getting Started

### Prerequisites

- JDK 17
- Maven
- PostgreSQL

### Installation

1. Clone the repository:

    ```bash
    git clone https://github.com/SpursFan21/TaskApp.git
    cd TaskApp
    ```

2. Configure the PostgreSQL database:

    - Ensure PostgreSQL is running.
    - Create the database and users table:
    
    ```sql
    CREATE DATABASE maventaskapp;
    \c maventaskapp;
    
    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        username VARCHAR(50) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL
    );
    ```

3. Build the project with Maven:

    ```bash
    mvn clean install
    ```

4. Run the application:

    ```bash
    mvn javafx:run
    ```

## Contributing

Contributions are welcome! Please fork the repository and submit a pull request for any features, bug fixes, or improvements.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.
