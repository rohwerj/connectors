package io.camunda.interfaces;

import com.co.igg.catastro.common.models.DocumentoProceso;

public interface IDocumentProcess {
	
	public void saveDocumentoProceso(DocumentoProceso DocumentoProceso);
	
	public DocumentoProceso findDocumentoProcesoById(String id);
		
}
