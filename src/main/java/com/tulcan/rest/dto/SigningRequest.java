package com.gadm.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * DTO para solicitudes de firma de PDF
 */
@XmlRootElement
public class SigningRequest {
    
    @NotNull(message = "El documento PDF es requerido")
    @Size(min = 1, message = "El documento PDF no puede estar vacío")
    private String documentoPdf;
    
    @NotNull(message = "El certificado es requerido")
    @Size(min = 1, message = "El certificado no puede estar vacío")
    private String certificadoP12;
    
    @NotNull(message = "La contraseña es requerida")
    @Size(min = 1, max = 100, message = "La contraseña debe tener entre 1 y 100 caracteres")
    private String contrasena;
    
    @Min(value = 0, message = "El número de página debe ser mayor o igual a 0")
    @Max(value = 1000, message = "El número de página debe ser menor o igual a 1000")
    private int pagina = 0;
    
    @Min(value = 0, message = "La posición horizontal debe ser mayor o igual a 0")
    @Max(value = 2000, message = "La posición horizontal debe ser menor o igual a 2000")
    private int posicionHorizontal;
    
    @Min(value = 0, message = "La posición vertical debe ser mayor o igual a 0")
    @Max(value = 2000, message = "La posición vertical debe ser menor o igual a 2000")
    private int posicionVertical;
    
    // Constructor vacío requerido para JAX-RS
    public SigningRequest() {}
    
    // Constructor completo
    public SigningRequest(String documentoPdf, String certificadoP12, String contrasena, 
                         int pagina, int posicionHorizontal, int posicionVertical) {
        this.documentoPdf = documentoPdf;
        this.certificadoP12 = certificadoP12;
        this.contrasena = contrasena;
        this.pagina = pagina;
        this.posicionHorizontal = posicionHorizontal;
        this.posicionVertical = posicionVertical;
    }
    
    // Getters y Setters
    public String getDocumentoPdf() {
        return documentoPdf;
    }
    
    public void setDocumentoPdf(String documentoPdf) {
        this.documentoPdf = documentoPdf;
    }
    
    public String getCertificadoP12() {
        return certificadoP12;
    }
    
    public void setCertificadoP12(String certificadoP12) {
        this.certificadoP12 = certificadoP12;
    }
    
    public String getContrasena() {
        return contrasena;
    }
    
    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }
    
    public int getPagina() {
        return pagina;
    }
    
    public void setPagina(int pagina) {
        this.pagina = pagina;
    }
    
    public int getPosicionHorizontal() {
        return posicionHorizontal;
    }
    
    public void setPosicionHorizontal(int posicionHorizontal) {
        this.posicionHorizontal = posicionHorizontal;
    }
    
    public int getPosicionVertical() {
        return posicionVertical;
    }
    
    public void setPosicionVertical(int posicionVertical) {
        this.posicionVertical = posicionVertical;
    }
    
    /**
     * Limpia datos sensibles de la memoria
     */
    public void clearSensitiveData() {
        this.contrasena = null;
        this.certificadoP12 = null;
        this.documentoPdf = null;
    }
    
    @Override
    public String toString() {
        return "SigningRequest{" +
                "pagina=" + pagina +
                ", posicionHorizontal=" + posicionHorizontal +
                ", posicionVertical=" + posicionVertical +
                ", hasCertificate=" + (certificadoP12 != null && !certificadoP12.isEmpty()) +
                ", hasDocument=" + (documentoPdf != null && !documentoPdf.isEmpty()) +
                ", hasPassword=" + (contrasena != null && !contrasena.isEmpty()) +
                '}';
    }
}
