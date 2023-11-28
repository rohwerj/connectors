package com.co.igg.catastro.connector.dao;

import com.co.igg.catastro.connector.models.DocumentoProceso;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IDocumentProcessDao
    extends PagingAndSortingRepository<DocumentoProceso, String>,
        JpaRepositoryImplementation<DocumentoProceso, String> {}
