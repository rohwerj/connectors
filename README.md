# Project Configuration and Execution Guide

This is a tutorial to configure and run the project in the root folder. Make sure to follow these steps for a successful execution.
## Requirements
Have in you desktop camunda modeler installed. 
Download it in: https://camunda.com/download/modeler/

## Steps

```shell
# 1. Project Compilation

# To compile the entire project without running tests, follow these steps:

# In the root folder, use the following Maven command:

mvn -DskipTests clean install

# Make sure to include the -DskipTests option to prevent tests from running during compilation.

# 2. Running the Connector

# Once the project is compiled, you can run the connector with Docker. Follow these steps:

# In the same root folder, execute the following Docker command keeping in mind that you need to change the path "C:/Users/Admin/Desktop/xample", for your own:

docker run --rm --name=connectors -v "C:/Users/Admin/Desktop/xample/camunda-8-connector-openweather-api/target/document-base64-api-0.1.0-SNAPSHOT.jar":/opt/app/connector.jar --env-file env.txt camunda/connectors-bundle:0.2.0

# Ensure that the env.txt file contains the following configuration:

zeebe.client.cloud.cluster-id=1c342914-988c-47c9-9e20-3627a420cb04
zeebe.client.cloud.client-id=dIrgwB83y1x1us3VqHpGWMbsFvP~cnIJ
zeebe.client.cloud.client-secret=.-egHL5uBjEl3eA1Z-DwKCF.00.eisFNBdUfVvW8FUXOe_fsaot0X8IzF6Nl5419
zeebe.client.cloud.region=jfk-1

# These details are obtained once the cluster is created.

# 3. Configuration for Local Execution

# When you want to run the project locally, use the following environment variables instead of the ones mentioned earlier. Ensure your local environment is configured according to these variables:

# Zeebe (Configuration for Local)

zeebe.client.broker.gateway-address=127.0.0.1:26500
zeebe.client.security.plaintext=true

# Operate (Connect to Operate Locally with Username and Password)

camunda.operate.client.url=http://localhost:8081
camunda.operate.client.username=demo
camunda.operate.client.password=demo

# If you are running against a self-managed environment, you might also need to configure the Keycloak endpoint to not use Operate username/password authentication:

camunda.operate.client.keycloak-url=http://localhost:18080
camunda.operate.client.keycloak-realm=camunda-platform
camunda.operate.client.client-id=xxx
camunda.operate.client.client-secret=xxx

# Run the docker compose if you are running it on locally or in your AWS instance

# Deploy your process

1. You are going to find in this project a file called base-64.json copy it and go to your program files folder, then camunda, then resourcs and finally element-templates. it should look like this "C:\Program Files\Camunda Desktop\resources\element-templates", you have to paste base-64.json there
2. You are going to find the bpmn here with the name document.bpmn, go to your camunda desktop modeler and open document.bpmn with it.
3. Click on the box Get document info and in the right panel where it says template. it should say applied in blue, if it doesn´t, select the option change type that has the wrench tool icon, then select service task and in the right side wwhere it says teplate, click on the blue button that says select adn the select the template Get element information and apply.
4. Deploy the process clicking on the space rocket that should be located on the buttom of the program. It needs to be set up like this:
Deployment name: document
Target: Camunda Platform 8 Self-
Cluster endpoint: http://<ip address>:26500
Authentication: none
Remember credentials: on

5. Click on deploy and wait for the message that says that it was succesfully deployed
# Now you are ready to compile and run the project with the appropriate configurations. Enjoy working on your project!

## Testing the Project

To test the project, follow these steps:

1. Open your BPMN model in the Camunda Modeler.

2. In the Camunda Modeler, execute the model by clicking "Run" in the upper-right corner.

3. A "Run Model" dialog will appear. Here, you need to provide the required variables in JSON format. Use the following JSON structure:

{
        "inputs": "<imageinb64>", 
        "model_id": "santiagoperezs/notificacion-aviso-donut-cord"
}
