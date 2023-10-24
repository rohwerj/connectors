package io.camunda.dao;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.co.igg.catastro.common.models.Proceso;

public interface IProcesoDao  extends PagingAndSortingRepository<Proceso, Long> , JpaRepositoryImplementation<Proceso, Long>{
	
	
}
