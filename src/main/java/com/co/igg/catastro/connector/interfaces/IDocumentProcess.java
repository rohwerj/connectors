package com.co.igg.catastro.connector.interfaces;

import com.co.igg.catastro.connector.models.DocumentoProceso;

public interface IDocumentProcess {

  public void saveDocumentoProceso(DocumentoProceso DocumentoProceso);

  public DocumentoProceso findDocumentoProcesoById(String id);
}
