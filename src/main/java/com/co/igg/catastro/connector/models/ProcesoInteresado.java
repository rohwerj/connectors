package com.co.igg.catastro.connector.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class ProcesoInteresado {
    @Id
    private Long id;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
