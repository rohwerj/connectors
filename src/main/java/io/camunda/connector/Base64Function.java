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
        name = "UploadToAlfresco", inputVariables = {"files", "filesNames"}, type = "io.camunda::upload-document:1")
public class Base64Function implements OutboundConnectorFunction {

  private Session session;

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

      SessionFactory factory = SessionFactoryImpl.newInstance();
      session = factory.getRepositories(parameter).get(0).createSession();

  }
  
  @Override
  public Object execute(OutboundConnectorContext context) throws Exception {
    var connectorRequest = context.getVariablesAsType(Base64Request.class);
    context.replaceSecrets(connectorRequest);
      return executeConnector(connectorRequest);
  }

  private Base64Result executeConnector(final Base64Request connectorRequest) throws IOException {
    LOGGER.info("Executing my connector alfresco with request");
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

    // Construir el cuerpo de la solicitud en formato JSON
    ObjectMapper objectMapper = new ObjectMapper();
    byte[][] files = connectorRequest.getFiles();
    String[] filesNames = connectorRequest.getFilesNames();

    // Crear un objeto JSON con los campos "inputs" y "model_id"
    ObjectNode requestBody = objectMapper.createObjectNode();


     // Convertir el objeto JSON en una cadena
     String requestBodyString = objectMapper.writeValueAsString(requestBody);

     // Escribir los datos en el cuerpo de la solicitud
     try (OutputStream os = http.getOutputStream()) {
         byte[] input = requestBodyString.getBytes("utf-8");
         os.write(input, 0, input.length);
     }
        
    http.disconnect();
    String documentInformation;
    if (http.getResponseCode() == 200) {
        documentInformation = convertInputStreamToString(http.getInputStream());
        LOGGER.info("docuent information report: " + documentInformation);
    } else {
        LOGGER.error("Error accessing documentBase64 API: " + http.getResponseCode() + " - " + http.getResponseMessage());
        // Throwing an exception will fail the job
        throw new IOException(http.getResponseMessage());
    }

    var result = new Base64Result();
    result.setResult(documentInformation);
    LOGGER.info("getting out of connector");
    return result;
  }

  public void createWholeTree(String... targetFolderNames) {
    Folder[] folders = new Folder[targetFolderNames.length];
    Folder rootFolder = getRootFolder();

    for (int i = 0; i < targetFolderNames.length; i++) {
        String targetObjectName = targetFolderNames[i];

        Folder parentFolder = (i == 0) ? rootFolder : folders[i - 1];

        Folder foundObject = findSubfolderByNameInFolder(parentFolder, targetObjectName);
//	        revisa si se encontro o no el folder hijo
        if (foundObject != null) {
            folders[i] = foundObject;
        } else {
            folders[i] = createFolderIfNotExists(parentFolder, targetObjectName);
        }
    }
}

  public Folder getRootFolder()
    {
        return session.getRootFolder();
    }

  public Folder findSubfolderByNameInFolder(Folder parentFolder, String targetFolderName) {
	    List<Tree<FileableCmisObject>> children = parentFolder.getDescendants(1);

	    for (Tree<FileableCmisObject> tree : children) {
	        FileableCmisObject fileableObject = tree.getItem();

	        if (fileableObject instanceof Folder) {
	            Folder subfolder = (Folder) fileableObject;
	            String folderName = subfolder.getName();

	            if (folderName.equals(targetFolderName)) {
	                // Si encontramos el subfolder con el nombre deseado, lo retornamos
	                return subfolder;
	            }
	        }
	    }

	    // Si no se encontró el subfolder con el nombre deseado, retornamos null
	    return null;
	}
    
    public Folder createFolderIfNotExists(Folder parentFolder, String folderName) {
        Folder subFolder = null;
        for(CmisObject child : parentFolder.getChildren()) {
            if(folderName.equalsIgnoreCase(child.getName())) {
                subFolder = (Folder) child;
            }
        }

        if(subFolder == null) {
            Map<String, Object> props = new HashMap<>();
            props.put("cmis:objectTypeId", "cmis:folder");
            props.put("cmis:name", folderName);

            subFolder = parentFolder.createFolder(props);
        }
        return subFolder; 
    }

	public Document uploadDocumentToAlfresco(String documentName, MultipartFile multipartFile, Long id)
            throws IOException, SAXException, TikaException {
        // Luego de buscar el objeto, continúas con el resto del código de uploadDocument
		createWholeTree("Sites", "catastro-mutaciones", "documentLibrary", "Mutaciones-Primera-Clase");
        Folder folder = getDocLibFolder("catastro-mutaciones", "/Mutaciones-Primera-Clase");
        return uploadDocumentToAlfrescoFromMultipartFile(folder, documentName, multipartFile, id);
    }
	
	public Folder getDocLibFolder( String siteName, String folder) {
        String path = "/Sites/" + siteName + "/documentLibrary" + folder;
        return (Folder) session.getObjectByPath(path);
    }
	
    public Document uploadDocumentToAlfrescoFromMultipartFile(Folder folder, String documentName, MultipartFile multipartFile, Long id) throws IOException, SAXException, TikaException {
    	Document documentCreated=null;
    	try {
            // Comprobar si ya existe un documento con el mismo nombre en la carpeta
            if (isDocumentNameExists(folder, documentName)) {
                // Imprimir un mensaje o lanzar una excepción si ya existe el nombre
            	LOGGER.info("El documento con nombre '" + documentName + "' ya existe en la carpeta.");
                // Puedes lanzar una excepción específica para manejar este caso si lo deseas
            }else{
            	//crea metadatos
                String nameDocument = id + "_" + System.currentTimeMillis() + "_" + documentName;
                Map<String, Object> properties = new HashMap<String, Object>();
                properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
                properties.put(PropertyIds.NAME, documentName);
                
                //encapsula el contenido del archivo cargado para su posterior almacenamiento en un repositorio de contenido.
                InputStream inputStream = multipartFile.getInputStream();
                ContentStream contentStream = new ContentStreamImpl(
                        nameDocument,
                        BigInteger.valueOf(multipartFile.getSize()),
                        getMimeTypeFromMultipartFile(multipartFile),
                        inputStream);
                Proceso procesoAsociado = this.procesoService.findProcesoById(id);
                //aca es que se manda para alfresco
                documentCreated=folder.createDocument(properties, contentStream, VersioningState.MAJOR);
                //aca se crea en la bd
                DocumentoProceso documentoCreado = new DocumentoProceso();
                documentoCreado.setIdDocumento(documentCreated.getId());
                documentoCreado.setFileName(documentCreated.getName());
                documentoCreado.setMimeType(documentCreated.getContentStreamMimeType());
                documentoCreado.setContentUrl(documentCreated.getContentUrl());
                documentoCreado.setDtCreacion(new Date());
                documentoCreado.setProceso(procesoAsociado);   	
                this.documentProcessDao.save(documentoCreado);
                LOGGER.info("documentCreated:",documentCreated);
            }
        } catch (IOException | CmisRuntimeException e) {
            // Imprimir el error utilizando log.info
        	LOGGER.info("Error al subir el documento: " + e.getMessage(), e);
            // Relanzar la excepción para que el método llamante también la maneje si es necesario
            throw e;
        }
		return documentCreated;
    }
    
    public String getMimeTypeFromMultipartFile(MultipartFile file) {
        return file.getContentType();
    }
    
    private boolean isDocumentNameExists(Folder folder, String documentName) {
        for (CmisObject object : folder.getChildren()) {
            if (object instanceof Document) {
                Document document = (Document) object;
                if (document.getName().equals(documentName)) {
                    return true;
                }
            }
        }
        return false;
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