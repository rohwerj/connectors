package io.camunda.connector;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.co.igg.catastro.common.models.DocumentoProceso;
import com.co.igg.catastro.common.models.Proceso;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.dao.IDocumentProcessDao;
import io.camunda.interfaces.IProcesoService;
import jakarta.annotation.PostConstruct;

@OutboundConnector(
        name = "UploadToAlfresco", inputVariables = {"files", "filesNames"}, type = "io.camunda:upload-document:1")
public class Base64Function implements OutboundConnectorFunction {

  private Session session;


  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Function.class);

  @PostConstruct
  public void init()
  {

      String alfrescoBrowserUrl = System.getenv("alfresco.repository.url") + "/api/-default-/public/cmis/versions/1.1/browser";

      Map<String, String> parameter = new HashMap<String, String>();

      parameter.put(SessionParameter.USER, System.getenv("alfresco.repository.user"));
      parameter.put(SessionParameter.PASSWORD, System.getenv("alfresco.repository.pass"));

      parameter.put(SessionParameter.BROWSER_URL, alfrescoBrowserUrl);
      parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
      LOGGER.info("User: {}",System.getenv("alfresco.repository.user"));
      LOGGER.info("Pass: {}",System.getenv("alfresco.repository.pass"));
      LOGGER.info("Url: {}",alfrescoBrowserUrl);
      SessionFactory factory = SessionFactoryImpl.newInstance();
      /* session = factory.getRepositories(parameter).get(0).createSession(); */

  }

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    var connectorRequest = context.getVariablesAsType(Base64Request.class);
    context.replaceSecrets(connectorRequest);
      return executeConnector(connectorRequest);
  }

  private Base64Result executeConnector(final Base64Request connectorRequest) throws IOException {
    LOGGER.info("Executing my connector alfresco with request");
       String alfrescoBrowserUrl = System.getenv("alfresco.repository.url") + "/api/-default-/public/cmis/versions/1.1/browser";
      LOGGER.info("User: {}",System.getenv("alfresco.repository.user"));
      LOGGER.info("Pass: {}",System.getenv("alfresco.repository.pass"));
      LOGGER.info("Url: {}",alfrescoBrowserUrl);
    var result = new Base64Result();
    LOGGER.info("getting out of connector");
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