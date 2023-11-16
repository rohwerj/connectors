package io.camunda.dao;

import com.co.igg.catastro.common.models.Documento;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IDocumentoDao
    extends PagingAndSortingRepository<Documento, Long>,
        JpaRepositoryImplementation<Documento, Long> {

  public Documento findByDsDocumento(String dsDocumento);
}
