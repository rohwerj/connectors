package com.co.igg.catastro.connector.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Documento {
    @Id
    private Long id;

    private String dsDocumento;

    public void setDsDocumento(String dsDocumento) {
        this.dsDocumento = dsDocumento;
    }

    public String getDsDocumento() {
        return dsDocumento;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
