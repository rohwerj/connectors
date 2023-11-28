package com.co.igg.catastro.connector.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class DocumentoProceso {
    @Id
    private Long id;

    public void setIdDocumento(String id) {

    }

    public void setFileName(String name) {

    }

    public void setMimeType(String contentStreamMimeType) {

    }

    public void setContentUrl(String contentUrl) {
    }

    public void setDtCreacion(Date date) {

    }

    public void setProceso(Proceso procesoAsociado) {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
