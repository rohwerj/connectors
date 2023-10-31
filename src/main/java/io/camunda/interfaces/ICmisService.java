package io.camunda.interfaces;

import java.io.IOException;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.tika.exception.TikaException;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

public interface ICmisService {
	public Folder getDocLibFolder( String siteName, String folder);
}
