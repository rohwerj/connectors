package io.camunda.dao;

import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.co.igg.catastro.common.models.ProcesoInteresado;

public interface IProcesoInteresadoDao extends PagingAndSortingRepository<ProcesoInteresado, Long>, JpaRepositoryImplementation<ProcesoInteresado, Long>{

}
