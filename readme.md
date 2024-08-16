# Cosmos Odyssey Backend Server

This repository contains the core service for the Cosmos Odyssey web application, designed to offer the best travel routes between the planets of our solar system. The application allows customers to select travel routes and make reservations.

The backend server aggregates travel routes and prices offered by various providers, calculates the best routes, and offers customers the ability to easily find the best travel option, including trips with layovers.

## Important

**The entire software operates through the collaboration of multiple servers. The complete solution can be launched using Docker containers and Compose:**

[https://github.com/kenbockler/cosmos-odyssey-docker.git](https://github.com/kenbockler/cosmos-odyssey-docker.git)

However, if you only want to start the core backend server, you can follow the steps below.

## Getting Started

### Building the Project

1. Clone the repository:

    ```bash
    git clone https://github.com/kenbockler/cosmos-odyssey-core.git
    ```

2. Navigate to the cloned repository directory:

    ```bash
    cd cosmos-odyssey-core
    ```

3. Clean and build the project:

    ```bash
    ./gradlew clean build
    ```

### Running the Core Server

1. Start the core backend server:

    ```bash
    java -jar build/libs/cosmos-odyssey-core-1.0.0.jar
    ```

The core application should now be running and accessible on port 9090.