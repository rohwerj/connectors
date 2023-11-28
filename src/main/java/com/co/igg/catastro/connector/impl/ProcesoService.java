package com.co.igg.catastro.connector.impl;

import com.co.igg.catastro.connector.models.Documento;
import com.co.igg.catastro.connector.models.Proceso;
import com.co.igg.catastro.connector.models.ProcesoInteresado;
import com.co.igg.catastro.connector.dao.IDocumentoDao;
import com.co.igg.catastro.connector.dao.IProcesoDao;
import com.co.igg.catastro.connector.dao.IProcesoInteresadoDao;
import com.co.igg.catastro.connector.interfaces.IProcesoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcesoService implements IProcesoService {

  @Autowired private IProcesoDao procesoDao;

  @Autowired private IProcesoInteresadoDao procesoInteresadoDao;

  @Autowired private IDocumentoDao documentoDao;

  // Del Proceso
  @Transactional
  public void saveProceso(Proceso proceso) {
    procesoDao.save(proceso);
  }

  @Transactional(readOnly = true)
  public Proceso findProcesoById(Long id) {
    return procesoDao.findById(id).orElse(null);
  }

  @Transactional(readOnly = true)
  public Page<Proceso> findProcesoAll(Pageable pageable) {
    return procesoDao.findAll(pageable);
  }

  // De la Proceso para una Persona
  @Transactional
  public void saveProcesoInteresado(ProcesoInteresado procesoInteresado) {
    procesoInteresadoDao.save(procesoInteresado);
  }

  @Transactional
  public void saveDocumento(Documento documento) {
    documentoDao.save(documento);
  }

  @Transactional(readOnly = true)
  public Documento findByDsDocumento(String dsDocumento) {
    return documentoDao.findByDsDocumento(dsDocumento);
  }
}
