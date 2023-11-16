package io.camunda.impl;

import com.co.igg.catastro.common.models.DocumentoProceso;
import io.camunda.dao.IDocumentProcessDao;
import io.camunda.interfaces.IDocumentProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DocumentProcess
    implements IDocumentProcess { // implementara lo descrito en ISolicitudService

  @Autowired private IDocumentProcessDao documentProcessDao;

  @Transactional
  public void saveDocumentoProceso(DocumentoProceso DocumentoProceso) {
    documentProcessDao.save(DocumentoProceso);
  }

  @Transactional(readOnly = true)
  public DocumentoProceso findDocumentoProcesoById(String id) {
    return documentProcessDao.findById(id).orElse(null);
  }
}
