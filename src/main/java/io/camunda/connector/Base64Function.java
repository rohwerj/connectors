package io.camunda.connector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileExistsException;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import io.camunda.connector.api.annotation.OutboundConnector;
import io.camunda.connector.api.outbound.OutboundConnectorContext;
import io.camunda.connector.api.outbound.OutboundConnectorFunction;
import io.camunda.impl.CmisService;
import io.camunda.models.CustomMultipartFile;

@Service
@OutboundConnector(
        name = "UploadToAlfresco", inputVariables = {"files", "filesNames", "idProceso"}, type = "io.camunda:upload-document:1")
public class Base64Function implements OutboundConnectorFunction {

	@Autowired
	private CmisService cmisService;
	
//	@Autowired
//	public Base64Function(CmisService cmisService ) {
//		super();
//		this.cmisService = cmisService;
//	} 
	
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
      //CmisService cmisService= new CmisService();
      LOGGER.info("Cmis: {}",cmisService);
     // LOGGER.info("this: {}",this.cmisService);
      String[] filesNames= connectorRequest.getFilesNames();
      byte[][] files= connectorRequest.getFiles();
      Long idProceso= connectorRequest.getIdProceso();
      List<MultipartFile> multipartFiles = convertBytesToMultipartFiles(files, filesNames);

      if (null != multipartFiles && multipartFiles.size() > 0) {
    	    for (MultipartFile multipartFile : multipartFiles) {
    	        String fileName = multipartFile.getOriginalFilename();
    	        LOGGER.info("filename: {}", fileName);
    	        LOGGER.info("inputstream: {}", multipartFile.getInputStream());
    	        try {
			        Document docCreated=cmisService.uploadDocumentToAlfresco( fileName,  multipartFile, idProceso );
			       
			      } catch (Exception e) {
			        // TODO Auto-generated catch block
			        e.printStackTrace();
			      } 
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