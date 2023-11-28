package com.co.igg.catastro.connector.interfaces;

import com.co.igg.catastro.connector.models.Documento;
import com.co.igg.catastro.connector.models.Proceso;
import com.co.igg.catastro.connector.models.ProcesoInteresado;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProcesoService {

  public void saveProceso(Proceso proceso);

  public Proceso findProcesoById(Long id);

  public Page<Proceso> findProcesoAll(Pageable pageable);

  public void saveProcesoInteresado(ProcesoInteresado procesoInteresado);

  public void saveDocumento(Documento documento);

  public Documento findByDsDocumento(String dsDocumento);
}
