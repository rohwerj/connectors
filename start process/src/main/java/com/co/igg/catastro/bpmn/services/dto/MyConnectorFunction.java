//https://camunda.com/blog/2022/11/how-to-build-camunda-platform-8-connector/
package com.co.igg.catastro.bpmn.services.dto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.IOException;

@OutboundConnector(
		name = "base 64", 
		inputVariables = { "inputs", "model_id" }, 
		type = "io.camunda:unique-id-catastro")
    public class MyConnectorFunction implements OutboundConnectorFunction {

        private static final Logger LOGGER = LoggerFactory.getLogger(MyConnectorFunction.class);

        @Override
        public Object execute(OutboundConnectorContext context) throws Exception {
            // aca traemos los valores de las variables y los secretos
            System.out.println("Entre a execute");
            var connectorRequest = context.bindVariables(MyConnectorRequest.class);
            //context.replaceSecrets(connectorRequest);
            return executeConnector(connectorRequest);
        }

        private MyConnectorResult executeConnector(final MyConnectorRequest connectorRequest) throws IOException {
            // TODO: implement connector logic
            System.out.println("Executing my connector with request ");
            String urlString = "https://api.pruebas.isipoint.co:8093";
            URL url = new URL(urlString);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestProperty("Accept", "application/json");

            http.disconnect();

            String documentInformation;
            if (http.getResponseCode() == 200) {
              documentInformation= convertInputStreamToString(http.getInputStream());
              System.out.println("Document information" + documentInformation);
            } else {
                LOGGER.error("Error accessing OpenWeather API: " + http.getResponseCode() + " - " + http.getResponseMessage());
                // Throwing an exception will fail the job
                throw new IOException(http.getResponseMessage());   
            }

            var result = new MyConnectorResult();
            result.setResult("{\"code\": " + http.getResponseCode() + ", \"documentInformation\": " + documentInformation + "}");
            
            return result;
        }
        private static String convertInputStreamToString(InputStream inputStream) {
            StringBuilder result = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
              String line;
              while ((line = reader.readLine()) != null) {
                result.append(line);
              }
            } catch (IOException ex) {
              LOGGER.error("Error during response reading: ", ex);
              return "{}";
            }

            return result.toString();
          }
    }
