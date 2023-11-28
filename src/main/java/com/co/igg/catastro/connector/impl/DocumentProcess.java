package com.co.igg.catastro.connector.impl;

import com.co.igg.catastro.connector.models.DocumentoProceso;
import com.co.igg.catastro.connector.dao.IDocumentProcessDao;
import com.co.igg.catastro.connector.interfaces.IDocumentProcess;
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
