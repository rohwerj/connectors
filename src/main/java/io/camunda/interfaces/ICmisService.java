package io.camunda.interfaces;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.springframework.web.multipart.MultipartFile;

public interface ICmisService {
  public Folder getDocLibFolder(String siteName, String folder);

  public Document uploadDocumentToAlfresco(
      String fileName, MultipartFile multipartFile, Long idProceso) throws Exception;
}
