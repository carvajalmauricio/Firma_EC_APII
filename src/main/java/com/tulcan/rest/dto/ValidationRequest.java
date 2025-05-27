package com.gadm.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO para solicitudes de validación de PDF
 */
@XmlRootElement
public class ValidationRequest {
    
    @NotNull(message = "El documento PDF es requerido")
    @Size(min = 1, message = "El documento PDF no puede estar vacío")
    private String documentoPdf;
    
    // Constructor vacío
    public ValidationRequest() {}
    
    // Constructor con parámetros
    public ValidationRequest(String documentoPdf) {
        this.documentoPdf = documentoPdf;
    }
    
    // Getters y Setters
    public String getDocumentoPdf() {
        return documentoPdf;
    }
    
    public void setDocumentoPdf(String documentoPdf) {
        this.documentoPdf = documentoPdf;
    }
    
    /**
     * Limpia datos sensibles de la memoria
     */
    public void clearSensitiveData() {
        this.documentoPdf = null;
    }
    
    @Override
    public String toString() {
        return "ValidationRequest{" +
                "hasDocument=" + (documentoPdf != null && !documentoPdf.isEmpty()) +
                '}';
    }
}
