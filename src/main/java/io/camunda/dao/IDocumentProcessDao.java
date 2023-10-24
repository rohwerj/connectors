package io.camunda.dao;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.co.igg.catastro.common.models.DocumentoProceso;

public interface IDocumentProcessDao  extends PagingAndSortingRepository<DocumentoProceso, String>,JpaRepositoryImplementation<DocumentoProceso, String> {
	
}
