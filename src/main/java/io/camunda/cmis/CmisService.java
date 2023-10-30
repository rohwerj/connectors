package io.camunda.cmis;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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
import org.springframework.stereotype.Service;
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

import io.camunda.connector.Base64Function;
import io.camunda.dao.IDocumentProcessDao;
import io.camunda.interfaces.IDocumentProcess;
import io.camunda.interfaces.IProcesoService;
import io.camunda.services.ICmisService;
import jakarta.annotation.PostConstruct;
/**
 * CMIS Service to handle operations within the session.
 * 
 * @author 
 *
 */
@Service
public class CmisService implements ICmisService{
	
	@Autowired
	private IProcesoService procesoService;
	@Autowired
	private IDocumentProcess documentProcess;
    @Autowired
	private IDocumentProcessDao documentProcessDao;
	private Session session;


	  private static final Logger LOGGER = LoggerFactory.getLogger(Base64Function.class);

	  @PostConstruct
	  public void init()
	  {

	      String alfrescoBrowserUrl = System.getenv("alfresco.repository.url") + "/api/-default-/public/cmis/versions/1.1/browser";

	      Map<String, String> parameter = new HashMap<String, String>();

	    /*   parameter.put(SessionParameter.USER, System.getenv("alfresco.repository.user"));
	      parameter.put(SessionParameter.PASSWORD, System.getenv("alfresco.repository.pass"));
	      parameter.put(SessionParameter.BROWSER_URL, alfrescoBrowserUrl); */

	      parameter.put(SessionParameter.USER, "admin");
	      parameter.put(SessionParameter.PASSWORD, "admin");
	      parameter.put(SessionParameter.BROWSER_URL, "http://192.168.6.33:8080/alfresco/api/-default-/public/cmis/versions/1.1/browser");
	      parameter.put(SessionParameter.BINDING_TYPE, BindingType.BROWSER.value());
	      SessionFactory factory = SessionFactoryImpl.newInstance();
	      session = factory.getRepositories(parameter).get(0).createSession();

	  }

	//clases del cmis
	  public void updateProperties(CmisObject cmisObject, Map<String, Object> properties)
	  {
	      cmisObject.updateProperties(properties);
	  }
	
	  public ObjectId createRelationship(CmisObject sourceObject, CmisObject targetObject, String relationshipName)
	  {
	
	      Map<String, Object> properties = new HashMap<String, Object>();
	      properties.put(PropertyIds.NAME, "a new relationship");
	      properties.put(PropertyIds.OBJECT_TYPE_ID, relationshipName);
	      properties.put(PropertyIds.SOURCE_ID, sourceObject.getId());
	      properties.put(PropertyIds.TARGET_ID, targetObject.getId());
	
	      return session.createRelationship(properties);
	
	  }
	  
	  public void addAspect(CmisObject cmisObject, String aspect)
	  {
	
	      List<Object> aspects = cmisObject.getProperty("cmis:secondaryObjectTypeIds").getValues();
	      if (!aspects.contains(aspect))
	      {
	          aspects.add(aspect);
	          Map<String, Object> aspectListProps = new HashMap<String, Object>();
	          aspectListProps.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspects);
	          cmisObject.updateProperties(aspectListProps);
	      }
	
	  }
	
	  public ItemIterable<Relationship> getRelationships(ObjectId objectId, String relationshipName)
	  {
	
	      ObjectType typeDefinition = session.getTypeDefinition(relationshipName);
	      OperationContext operationContext = session.createOperationContext();
	      return session.getRelationships(objectId, true, RelationshipDirection.EITHER, typeDefinition, operationContext);
	
	  }
	
	  public void remove(CmisObject object)
	  {
	
	      if (BaseTypeId.CMIS_FOLDER.equals(object.getBaseTypeId()))
	      {
	          Folder folder = (Folder) object;
	          ItemIterable<CmisObject> children = folder.getChildren();
	          for (CmisObject child : children)
	          {
	              remove(child);
	          }
	      }
	      session.delete(object);
	  }

	@Override
	public Folder getDocLibFolder( String siteName, String folder) {
	  String path = "/Sites/" + siteName + "/documentLibrary" + folder;
	  return (Folder) session.getObjectByPath(path);
	}
	

//otras clases 
  static void appendMatch(StringBuilder builder, String field, String value) {
      builder.append("{ \"match\": { \"").append(field).append("\": \"").append(value).append("\" } }, ");
  }
  public static String convertHitsListToString(List<JsonNode> hitsList) throws JsonProcessingException {
      ObjectMapper objectMapper = new ObjectMapper();
      return objectMapper.writeValueAsString(hitsList);
  }
  public static String convertToJsonQuery(JsonNode documento, Integer from, Integer size) {
      if (from == null) {
          from = 0;
      }
      if (size == null) {
          size = 20;
      }

      ObjectMapper objectMapper = new ObjectMapper();
      objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

      ObjectNode root = objectMapper.createObjectNode();
      ObjectNode queryNode = root.putObject("query");
      ObjectNode boolNode = queryNode.putObject("bool");
      ArrayNode mustArray = boolNode.putArray("must");

      Iterator<Map.Entry<String, JsonNode>> fieldsIterator = documento.fields();
      while (fieldsIterator.hasNext()) {
          Map.Entry<String, JsonNode> fieldEntry = fieldsIterator.next();
          String fieldName = fieldEntry.getKey();
          JsonNode fieldValue = fieldEntry.getValue();

          ObjectNode matchNode = mustArray.addObject();
          ObjectNode matchObj = matchNode.putObject("match");
          matchObj.put(fieldName, fieldValue.textValue());
      }

      return root.toPrettyString();
  }
  
  public static String convertToJsonQueryAllFields(String searchParam, Integer from, Integer size) {
  	if (from == null) {
          from = 0;
      }
      if (size == null) {
          size = 20;
      }
  	ObjectMapper objectMapper = new ObjectMapper();
      ObjectNode root = objectMapper.createObjectNode();
      ObjectNode queryNode = root.putObject("query");
      ObjectNode multiMatchNode = queryNode.putObject("multi_match");
      multiMatchNode.put("query", searchParam);
      multiMatchNode.putArray("fields").add("*");
      root.put("from", from);
      root.put("size", size);
      return root.toPrettyString();
  }

  public static CloseableHttpClient createHttpClientWithCredentials() {
      // Crear un proveedor de credenciales y agregar las credenciales de usuario y contraseña
      CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
      credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("admin", "admin"));

      // Crear un cliente HttpClient con el proveedor de credenciales
      return HttpClientBuilder.create()
              .setDefaultCredentialsProvider(credentialsProvider)
              .build();
  }
	private static Map<String, Object> getFieldValues(Object object) {
      Map<String, Object> fieldValues = new HashMap<>();
      Field[] fields = object.getClass().getDeclaredFields();
      
      try {
          for (Field field : fields) {
              field.setAccessible(true);
              Object value = field.get(object);
              if (value != null) {
              	if (((Number) value).doubleValue() != 0) {
	                    if (field.getType() == Date.class) {
	                    	 SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	                         String formattedDate = dateFormat.format((Date) value);
	                         fieldValues.put(field.getName(), formattedDate);
	                    } else if (field.getType() == short.class || field.getType() == byte.class ||
	                               field.getType() == Long.class || field.getType() == long.class) {
	                        fieldValues.put(field.getName(), value.toString());
	                    } else if (field.getType() == byte.class || field.getType() == Byte.class) {
	                        fieldValues.put(field.getName(), (int)(Byte)value); // Convertir Byte a int
	                    } else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
	                        fieldValues.put(field.getName(), value); // Mantener booleano sin cambios
	                    } else {
	                    	fieldValues.put(field.getName(), value);
	                    }
	                }
	            }
          }
      } catch (IllegalAccessException e) {
          e.printStackTrace();
      }
      
      return fieldValues;
  }

  public static List<JsonNode> getHitsFromResponse(String responseJson) throws JsonProcessingException {
      List<JsonNode> hitsList = new ArrayList<>();
      
      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode jsonResponse = objectMapper.readTree(responseJson);
      
      JsonNode hits = jsonResponse.path("hits").path("hits");
      if (hits.isArray()) {
          for (JsonNode hit : hits) {
              hitsList.add(hit.path("_source"));
          }
      }
      
      return hitsList;
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
  
  
  public String getMimeTypeFromMultipartFile(MultipartFile file) {
      return file.getContentType();
  }

  public String getResponseToSimpleQuery(String searchParam) {
      String alfrescoBaseUrl = System.getenv("alfresco.repository.url") + "/api/-default-/public/search/versions/1/search"; // Reemplazar con la URL base de tu 

      CloseableHttpClient httpClient = createHttpClientWithCredentials();

      // Crear una solicitud POST para obtener la URL de descarga directa del archivo
      HttpPost httpPost = new HttpPost(alfrescoBaseUrl);

      // Configurar los datos de la solicitud en el cuerpo de la solicitud POST
      try {
          JSONObject requestBodyJson = new JSONObject();
          JSONObject queryJson = new JSONObject();
          queryJson.put("query", "select * from cmis:document WHERE cmis:name LIKE '%" + searchParam + "%'");
          queryJson.put("language", "cmis");
          requestBodyJson.put("query", queryJson);

          // Establecer el cuerpo de la solicitud en la entidad de la solicitud POST
          StringEntity requestEntity = new StringEntity(requestBodyJson.toString(), "UTF-8");
          httpPost.setEntity(requestEntity);

          httpPost.setHeader("Content-Type", "application/json");
          httpPost.setHeader("Accept", "application/json");
      } catch (Exception e) {
          // Manejar excepciones al configurar los datos de la solicited
          return "Error: Excepción al configurar los datos de la solicitud POST.";
      }

      try {
          // Ejecutar la solicitud HTTP POST
          HttpResponse response = httpClient.execute(httpPost);
          int statusCode = response.getStatusLine().getStatusCode();

          if (statusCode == 200) {
              // Leer el contenido de la respuesta
              String content = EntityUtils.toString(response.getEntity());

              // En este ejemplo, simplemente retornamos el contenido completo de la respuesta
              return content;
          } else {
              // Error: La solicitud no fue exitosa, manejar el error según sea necesario
              return "Error: No se pudo obtener la URL de descarga directa del archivo.";
          }
      } catch (IOException e) {
          // Error: Manejar excepciones de conexión o lectura
          return "Error: Excepción al obtener la URL de descarga directa del archivo.";
      } finally {
          // Cerrar el cliente HttpClient después de usarlo
          try {
              httpClient.close();
          } catch (IOException e) {
              // Manejar excepciones al cerrar el cliente
          }
      }
  }

  public Folder getRootFolder()
  {
      return session.getRootFolder();
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

  public void persistDocInDB(String idDoc, String infoFromDoc) throws JsonMappingException, JsonProcessingException {
     
  }
  public String persistDocInDB(String infoFromDoc) throws JsonMappingException, JsonProcessingException {
  	String idOfDoc=null;
  	  	return idOfDoc;
  }
  public Long generateRandomLong() {
      Random random = new Random();
      long lowerBound = 0;
      long upperBound = 100000;

      // Generar un número aleatorio en el rango [lowerBound, upperBound)
      long randomValue = lowerBound + (long) (random.nextDouble() * (upperBound - lowerBound));
      
      return randomValue;
  }
  public ItemIterable<QueryResult> query(String query)
  {
      return session.query(query, false);
  }

  public String requestDirectAccessUrl(String nodeRef) {
      String alfrescoBaseUrl = System.getenv("alfresco.repository.url") + "/api/-default-/public/alfresco/versions/1/nodes/"; // Reemplazar con la URL base de tu instancia de Alfresco
      String apiUrl = nodeRef + "/content?attachment=true"; // URL de la API REST de Alfresco para obtener la URL de descarga directa del archivo

      String fullUrl = alfrescoBaseUrl + apiUrl;

      return fullUrl;
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
  public Document uploadDocumentToAlfresco(String documentName, MultipartFile multipartFile, Long id)
            throws IOException, SAXException, TikaException {
        // Luego de buscar el objeto, continúas con el resto del código de uploadDocument
		createWholeTree("Sites", "catastro-mutaciones", "documentLibrary", "Mutaciones-Primera-Clase");
        Folder folder = getDocLibFolder("catastro-mutaciones", "/Mutaciones-Primera-Clase");
        return uploadDocumentToAlfrescoFromMultipartFile(folder, documentName, multipartFile, id);
    }
  public void uploadDocumentToALfrescoFromFile(Folder folder, File file, String id) throws IOException, SAXException, TikaException {
      try {
      	DocumentoProceso documentoActualizar = this.documentProcess.findDocumentoProcesoById(id);
      	Document documentCreated=null;
      	String documentName = file.getName();
          // Comprobar si ya existe un documento con el mismo nombre en la carpeta
          if (isDocumentNameExists(folder, documentName)) {
              // Imprimir un mensaje o lanzar una excepción si ya existe el nombre
              LOGGER.info("El documento con nombre '" + documentName + "' ya existe en lel repositorio de alfresco.");
              // Puedes lanzar una excepción específica para manejar este caso si lo deseas
          } else {
              // Crea metadatos
              String nameDocument = id + "_" + System.currentTimeMillis() + "_" + documentName;
              Map<String, Object> properties = new HashMap<String, Object>();
              properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document");
              properties.put(PropertyIds.NAME, documentName);
              FileInputStream fileInputStream = new FileInputStream(file);
              
              String mimeType = FileContentTypeExample.getContentType(file);
              
              // Encapsula el contenido del archivo cargado para su posterior almacenamiento en un repositorio de contenido.
              ContentStream contentStream = new ContentStreamImpl(
                      nameDocument,
                      BigInteger.valueOf(fileInputStream.available()), // Utiliza la longitud del flujo de entrada
                      mimeType, // Puedes obtener el tipo MIME desde el nombre del documento
                      fileInputStream);
              
              // Aca se manda para Alfresco
              documentCreated = folder.createDocument(properties, contentStream, VersioningState.MAJOR);
              documentoActualizar.setFileName(documentName);
              documentoActualizar.setMimeType(mimeType);
              documentoActualizar.setContentUrl(documentCreated.getContentUrl());
              documentoActualizar.setDtCreacion(new Date());
              this.documentProcessDao.save(documentoActualizar);
          }
      } catch (IOException e) {
          LOGGER.error("Error al cargar el documento: " + e.getMessage());
          throw e;
      }

  }

  public static class FileContentTypeExample {
    private static final Map<String, String> MIME_TYPES = new HashMap<>();

    static {
        // Mapea extensiones de archivo a tipos MIME
        MIME_TYPES.put("txt", "text/plain");
        MIME_TYPES.put("pdf", "application/pdf");
        MIME_TYPES.put("jpg", "image/jpeg");
        // Agrega más extensiones y tipos MIME según sea necesario
    }

    public static String getContentType(File file) {
        String fileName = file.getName();
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            String fileExtension = fileName.substring(lastDotIndex + 1).toLowerCase();
            return MIME_TYPES.getOrDefault(fileExtension, "application/octet-stream");
        } else {
            // Si no se encuentra la extensión, se usa un tipo MIME genérico
            return "application/octet-stream";
        }
    }

    public static void main(String[] args) {
        File file = new File("ruta/al/archivo.pdf");
        String contentType = getContentType(file);
        System.out.println("Tipo de contenido: " + contentType);
    }
}



  
}