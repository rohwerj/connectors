package com.co.igg.catastro.connector.utils;

import com.co.igg.catastro.connector.dao.IDocumentProcessDao;
import com.co.igg.catastro.connector.impl.CmisService;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.commons.impl.json.JSONObject;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

@Component
public class ProcesarDocumentos {

  private IDocumentProcessDao documentProcessDao;

  private static Logger log = LoggerFactory.getLogger(ProcesarDocumentos.class);

  //	@Autowired
  private CmisService cmisService;

  @Autowired
  public ProcesarDocumentos(CmisService cmisService, IDocumentProcessDao documentProcessDao) {
    this.cmisService = cmisService;
    this.documentProcessDao = documentProcessDao;
  }

  public class FileExistsException extends Exception {
    public FileExistsException(String message) {
      super(message);
    }
  }

  public Document uploadDocumentToAlfresco(
      String documentName, MultipartFile multipartFile, Long id)
      throws IOException, SAXException, TikaException {
    // Luego de buscar el objeto, continúas con el resto del código de uploadDocument
    cmisService.createWholeTree(
        "Sites", "catastro-mutaciones", "documentLibrary", "Mutaciones-Primera-Clase");
    Folder folder = cmisService.getDocLibFolder("catastro-mutaciones", "/Mutaciones-Primera-Clase");
    return cmisService.uploadDocumentToAlfrescoFromMultipartFile(
        folder, documentName, multipartFile, id);
  }

  public void uploadDocumentToALfrescoFromFile(File file, String id)
      throws IOException, SAXException, TikaException {
    // Luego de buscar el objeto, continúas con el resto del código de uploadDocument
    cmisService.createWholeTree(
        "Sites", "catastro-mutaciones", "documentLibrary", "Mutaciones-Primera-Clase");
    Folder folder = cmisService.getDocLibFolder("catastro-mutaciones", "/Mutaciones-Primera-Clase");
    cmisService.uploadDocumentToALfrescoFromFile(folder, file, id);
  }

  public String getInformationFromFile(Document doc)
      throws IOException, SAXException, TikaException {
    String docInBase64 = convertToBase64FromUrl(cmisService.requestDirectAccessUrl(doc.getId()));
    String infoFromDoc = getInfoFromB64(docInBase64);
    cmisService.persistDocInDB(doc.getId(), infoFromDoc);
    return docInBase64;
  }

  public String getInfoFromB64(String docInBase64) throws IOException {
    String endpointUrl = "https://api.pruebas.isipoint.co:8093";
    String modelId = "santiagoperezs/notificacion-aviso-donut-cord";
    String jsonResponse = null;
    JSONObject requestBody = new JSONObject();
    requestBody.put("inputs", docInBase64);
    requestBody.put("model_id", modelId);

    URL url = new URL(endpointUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // Establecer el método POST y las propiedades de la solicitud
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setDoOutput(true);

    // Escribir el body de la solicitud
    try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream())) {
      outputStream.writeBytes(requestBody.toString());
      outputStream.flush();
    }

    // Leer la respuesta
    try (BufferedReader reader =
        new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
      String response;
      StringBuilder responseBuilder = new StringBuilder();

      while ((response = reader.readLine()) != null) {
        responseBuilder.append(response);
      }

      // Puedes manejar la respuesta aquí según tus necesidades
      jsonResponse = responseBuilder.toString();
    } catch (IOException e) {
      throw new IOException("Error reading response: " + e.getMessage());
    } finally {
      connection.disconnect();
    }
    return jsonResponse;
  }

  public String convertToBase64FromUrl(String imageUrl) throws IOException {
    URL url = new URL(imageUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();

    // Establecer las credenciales de usuario para la autenticación
    String userCredentials = "admin" + ":" + "admin";
    String basicAuth = "Basic " + Base64.getEncoder().encodeToString(userCredentials.getBytes());
    connection.setRequestProperty("Authorization", basicAuth);

    try (InputStream inputStream = connection.getInputStream()) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = inputStream.read(buffer)) != -1) {
        byteArrayOutputStream.write(buffer, 0, bytesRead);
      }
      byte[] imageBytes = byteArrayOutputStream.toByteArray();
      byteArrayOutputStream.close();

      return Base64.getEncoder().encodeToString(imageBytes);
    } catch (IOException e) {
      throw new IOException("Error reading image: " + e.getMessage());
    } finally {
      connection.disconnect();
    }
  }

  public void readImagesFromFolder(String folderPath, String destinationPath) {
    List<byte[]> imageContents = new ArrayList<>();
    File folder = new File(folderPath);

    if (!folder.isDirectory()) {
      throw new IllegalArgumentException(
          "La ruta de la carpeta no es válida o no es un directorio.");
    }

    for (File file : folder.listFiles()) {
      if (file.isFile() && file.getName().endsWith(".jpg")) {
        String lockedFileName = file.getName() + ".locked"; // Nombre del archivo .locked

        // Verifica si el archivo está bloqueado
        File lockedFile = new File(destinationPath, lockedFileName);
        File originalFile = new File(destinationPath, file.getName());
        if (lockedFile.exists()) {
          log.info("El archivo {} está bloqueado por otro cliente.", file.getName());
          continue; // Salta el archivo si está bloqueado.
        } else if (originalFile.exists()) {
          log.info("El archivo {} ya ha sido procesado.", file.getName());
          continue; // Salta el archivo si está bloqueado.
        }

        try {
          // Crea el archivo .locked para bloquear el archivo
          lockedFile.createNewFile();

          byte[] imageBytes = Files.readAllBytes(file.toPath());
          String docInBase64 = convertImageToBase64FromFolder(imageBytes);
          String infoFromDoc = getInfoFromB64(docInBase64);
          // aca se manda para elastic
          String idDocumento = cmisService.persistDocInDB(infoFromDoc);
          try {
            uploadDocumentToALfrescoFromFile(file, idDocumento);
          } catch (SAXException e) {
            e.printStackTrace();
          } catch (TikaException e) {
            e.printStackTrace();
          }
          // Mover archivo después del procesamiento
          Path sourcePath = file.toPath();
          Path destinationFilePath = Path.of(destinationPath, file.getName());
          Files.move(sourcePath, destinationFilePath);

          log.info("Se trabajó el archivo: {}", file.getName());
        } catch (IOException e) {
          log.error("Error al abrir o procesar el archivo: {}", e.getMessage());
        } finally {
          // Elimina el archivo .locked para desbloquear el archivo
          lockedFile.delete();
        }
      }
    }
  }

  public String convertImageToBase64FromFolder(byte[] imageBytes) {
    return Base64.getEncoder().encodeToString(imageBytes);
  }
}
