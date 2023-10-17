# Project base 64

This project uses Spring Boot and Docker for its execution. Make sure to follow these steps to get the application up and running:

# Step 1: Configure the Broker URL
# Ensure that the URL in the `application.yml` file is correctly configured in the `broker.gateway-address` field. This URL is essential for communication with the broker and should be the correct address.

zeebe.client:
 broker.gateway-address: 35.175.130.16:26500
 security.plaintext: true

# Step 2: Compile the Project
# Open a terminal and navigate to the project directory. Then, execute the following command to clean and build the project using Maven:

mvn clean install

# Step 3: Run the Application
# Once the project has been successfully compiled, you can start the Spring Boot application using the following command:

mvn spring-boot:run

This will initiate the application and get it up and running.


# Step 4: Start a Process
# To start a process, you need to use a JSON file located in the `resources` folder. Follow these steps to initiate a process:

1. Import the JSON file into a tool like Postman.
2. Configure the necessary parameters in the JSON file.
3. Execute the JSON file to start the desired process.

By following these steps, you can initiate processes within the application.
