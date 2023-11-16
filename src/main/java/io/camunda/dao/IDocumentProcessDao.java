package io.camunda.dao;

import com.co.igg.catastro.common.models.DocumentoProceso;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IDocumentProcessDao
    extends PagingAndSortingRepository<DocumentoProceso, String>,
        JpaRepositoryImplementation<DocumentoProceso, String> {}
