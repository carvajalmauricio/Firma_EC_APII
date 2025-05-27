package com.gadm.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO para solicitudes de firma de PDF
 */
@XmlRootElement
public class SigningRequest {
    
    private String documentoPdf;
    
    private String certificadoP12;
    
    private String contrasena;
    
    private int pagina = 0;
    
    private int posicionHorizontal;
    
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
