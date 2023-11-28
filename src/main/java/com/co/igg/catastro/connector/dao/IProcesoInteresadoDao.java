package com.co.igg.catastro.connector.dao;

import com.co.igg.catastro.connector.models.ProcesoInteresado;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface IProcesoInteresadoDao
    extends PagingAndSortingRepository<ProcesoInteresado, Long>,
        JpaRepositoryImplementation<ProcesoInteresado, Long> {}
