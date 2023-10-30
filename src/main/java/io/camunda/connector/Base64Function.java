package io.camunda.connector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.FileableCmisObject;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Repository;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.api.Tree;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisRuntimeException;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.commons.io.FileExistsException;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import com.co.igg.catastro.common.models.DocumentoProceso;
import com.co.igg.catastro.common.models.Proceso;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.dao.IDocumentProcessDao;
import io.camunda.interfaces.IProcesoService;
import io.camunda.models.CustomMultipartFile;
import io.camunda.cmis.CmisService;
import jakarta.annotation.PostConstruct;

@OutboundConnector(
        name = "UploadToAlfresco", inputVariables = {"files", "filesNames", "idProceso"}, type = "io.camunda:upload-document:1")
public class Base64Function implements OutboundConnectorFunction {

  private CmisService cmisService;
  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Function.class);

  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    var connectorRequest = context.getVariablesAsType(Base64Request.class);
    context.replaceSecrets(connectorRequest);
      return executeConnector(connectorRequest);
  }

  private Base64Result executeConnector(final Base64Request connectorRequest) throws IOException {
    LOGGER.info("Executing my connector alfresco with request");
      LOGGER.info("String: {}",connectorRequest.toString());
      String[] filesNames= connectorRequest.getFilesNames();
      byte[][] files= connectorRequest.getFiles();
      Long idProceso= connectorRequest.getIdProceso();
      List<MultipartFile> multipartFiles = convertBytesToMultipartFiles(files, filesNames);

      if (null != multipartFiles && multipartFiles.size() > 0) {
    	    for (MultipartFile multipartFile : multipartFiles) {
    	        String fileName = multipartFile.getOriginalFilename();
    	        LOGGER.info("filename: {}", fileName);
    	        LOGGER.info("inputstream: {}", multipartFile.getInputStream());
			  /*     try {
			        Document docCreated=cmisService.uploadDocumentToAlfresco( fileName,  multipartFile, idProceso );
			        if(docCreated==null){
			          throw new FileExistsException("El archivo ya existe en la base de datos.");
			        }
			//											String docInB64=procesarDocumentos.getInformationFromFile(docCreated);
			      } catch (NumberFormatException | IOException | SAXException | TikaException e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			      } */
    	    }
    	}
    var result = new Base64Result();
    LOGGER.info("getting out of connector");
    return result;
  }

 
    private List<MultipartFile> convertBytesToMultipartFiles(byte[][] fileBytesArray, String[] fileNamesArray) {
        List<MultipartFile> recreatedFiles = new ArrayList<>();
        for (int i = 0; i < fileBytesArray.length; i++) {
            CustomMultipartFile mockMultipartFile = new CustomMultipartFile(fileBytesArray[i], fileNamesArray[i], fileNamesArray[i]);
            recreatedFiles.add(mockMultipartFile);
        }
        return recreatedFiles;
    }
}