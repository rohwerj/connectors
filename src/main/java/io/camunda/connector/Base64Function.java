package io.camunda.connector;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@OutboundConnector(
        name = "OpenWeatherAPI", inputVariables = {"inputs", "model_id", "units", "apiKey"}, type = "io.camunda:weather-api:1")
public class Base64Function implements OutboundConnectorFunction {

  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Function.class);

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    var connectorRequest = context.getVariablesAsType(Base64Request.class);
    LOGGER.info("Pre-secret replacement: " + connectorRequest.toString());
    context.replaceSecrets(connectorRequest);
      return executeConnector(connectorRequest);
  }

  private Base64Result executeConnector(final Base64Request connectorRequest) throws IOException {
    System.out.println("Executing my connector with request ");
    LOGGER.info("1");
    String urlString = "https://api.pruebas.isipoint.co:8093";
    URL url = new URL(urlString);
    HttpURLConnection http = (HttpURLConnection)url.openConnection();
    // Configurar el método de solicitud como POST
    http.setRequestMethod("POST");
    // Configurar el tipo de contenido del cuerpo (en este caso, application/json)
    http.setRequestProperty("Content-Type", "application/json");
    http.setRequestProperty("Accept", "application/json");

    // Configurar para permitir enviar datos en la solicitud
    http.setDoOutput(true);
    LOGGER.info("2");

    // Construir el cuerpo de la solicitud en formato JSON
    ObjectMapper objectMapper = new ObjectMapper();
    String inputs = connectorRequest.getInputs();
    String modelId = connectorRequest.getModel_id();
    LOGGER.info("inputs: {}",inputs);
    LOGGER.info("modelId: {}",modelId);

    // Crear un objeto JSON con los campos "inputs" y "model_id"
    ObjectNode requestBody = objectMapper.createObjectNode();
    requestBody.put("inputs", inputs);
    requestBody.put("model_id", modelId);

     // Convertir el objeto JSON en una cadena
     String requestBodyString = objectMapper.writeValueAsString(requestBody);
    LOGGER.info("requestBodyString: {}",requestBodyString);
    
     // Escribir los datos en el cuerpo de la solicitud
     try (OutputStream os = http.getOutputStream()) {
         byte[] input = requestBodyString.getBytes("utf-8");
         os.write(input, 0, input.length);
     }
     
    http.disconnect();
    String weatherReport;
    if (http.getResponseCode() == 200) {
        weatherReport = convertInputStreamToString(http.getInputStream());
        LOGGER.info("Weather report: " + weatherReport);
    } else {
        LOGGER.error("Error accessing OpenWeather API: " + http.getResponseCode() + " - " + http.getResponseMessage());
        // Throwing an exception will fail the job
        throw new IOException(http.getResponseMessage());
    }

    var result = new Base64Result();
    result.setForecast(weatherReport);
    result.setCode(http.getResponseCode());
    return result;
  }

  /**
   * Reads the input stream line-by-line and returns its content in <code>String</code> representation.
   *
   * @param inputStream input stream to convert.
   * @return converted <code>InputStream</code> content.
   * @throws IllegalArgumentException in case if input stream is unable to be read.
   */
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