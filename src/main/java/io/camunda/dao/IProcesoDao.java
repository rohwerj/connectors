package io.camunda.dao;

import com.co.igg.catastro.common.models.Proceso;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IProcesoDao
    extends PagingAndSortingRepository<Proceso, Long>, JpaRepositoryImplementation<Proceso, Long> {}
