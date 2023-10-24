package io.camunda.connector;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.dao.IDocumentProcessDao;
import io.camunda.interfaces.IProcesoService;
import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import java.util.Map;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import java.util.Properties;

@OutboundConnector(
        name = "UploadToAlfresco", inputVariables = {"files", "filesNames"}, type = "io.camunda::upload-document:1")
public class Base64Function implements OutboundConnectorFunction {
    private Session session;
  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Function.class);
  @Autowired
  private IProcesoService procesoService;
  
  @Autowired
  private IDocumentProcessDao documentProcessDao;

  // Set values from "application.properties" file
  @Value("${alfresco.repository.url}")
  String alfrescoUrl;
  
  @Value("${alfresco.repository.user}")
  String alfrescoUser;
  
  @Value("${alfresco.repository.pass}")
  String alfrescoPass;

  @PostConstruct
  public void init()
  {
      String alfrescoBrowserUrl = alfrescoUrl + "/api/-default-/public/cmis/versions/1.1/browser";
  }

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    var connectorRequest = context.getVariablesAsType(Base64Request.class);
    context.replaceSecrets(connectorRequest);
      return executeConnector(connectorRequest);
  }

  private Base64Result executeConnector(final Base64Request connectorRequest) throws IOException {
    LOGGER.info("Executing my connector alfresco with request");
   

    Properties properties = new Properties();

    try (FileInputStream fileInputStream = new FileInputStream("env.txt")) {
        properties.load(fileInputStream);
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Accede a las propiedades del archivo "application.properties" como sea necesario
    String alfrescoUrl = properties.getProperty("alfresco.repository.url");
    String alfrescoUser = properties.getProperty("alfresco.repository.user");
    String alfrescoPass = properties.getProperty("alfresco.repository.pass");

    // Imprime las propiedades
    System.out.println("alfresco.repository.url: " + alfrescoUrl);
    System.out.println("alfresco.repository.user: " + alfrescoUser);
    System.out.println("alfresco.repository.pass: " + alfrescoPass);

    var result = new Base64Result();

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
//LOGGER.error("Error during response reading: ", ex);
      return "{}";
    }

    return result.toString();
  }
}