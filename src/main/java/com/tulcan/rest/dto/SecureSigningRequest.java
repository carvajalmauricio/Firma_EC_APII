package com.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * DTO para solicitudes de firma seguras con cifrado AES
 * Compatible con requests legacy y seguros
 */
@XmlRootElement
public class SecureSigningRequest {
    
    private String documentoPdf;
    
    // Campos cifrados (nuevos)
    private String encryptedCertificadoP12;
    private String encryptedContrasena;
    
    // Campos de seguridad
    private String timestamp;
    private String nonce;
    
    // Posicionamiento
    private int pagina = 0;
    private int posicionHorizontal;
    private int posicionVertical;
    
    // Campos legacy para compatibilidad
    private String certificadoP12;
    private String contrasena;
    
    public SecureSigningRequest() {}
    
    // Getters y Setters para campos cifrados
    public String getEncryptedCertificadoP12() {
        return encryptedCertificadoP12;
    }
    
    public void setEncryptedCertificadoP12(String encryptedCertificadoP12) {
        this.encryptedCertificadoP12 = encryptedCertificadoP12;
    }
    
    public String getEncryptedContrasena() {
        return encryptedContrasena;
    }
    
    public void setEncryptedContrasena(String encryptedContrasena) {
        this.encryptedContrasena = encryptedContrasena;
    }
    
    // Campos de seguridad
    public String getTimestamp() {
        return timestamp;
    }
    
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
    
    public String getNonce() {
        return nonce;
    }
    
    public void setNonce(String nonce) {
        this.nonce = nonce;
    }
    
    // Campos estándar
    public String getDocumentoPdf() {
        return documentoPdf;
    }
    
    public void setDocumentoPdf(String documentoPdf) {
        this.documentoPdf = documentoPdf;
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
    
    // Campos legacy
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
    
    /**
     * Verifica si usa el formato seguro (cifrado)
     */
    public boolean isSecureRequest() {
        return encryptedCertificadoP12 != null && !encryptedCertificadoP12.isEmpty();
    }
    
    /**
     * Limpia datos sensibles
     */
    public void clearSensitiveData() {
        this.contrasena = null;
        this.certificadoP12 = null;
        this.encryptedContrasena = null;
        this.encryptedCertificadoP12 = null;
        this.documentoPdf = null;
    }
    
    @Override
    public String toString() {
        return "SecureSigningRequest{" +
                "pagina=" + pagina +
                ", posicionHorizontal=" + posicionHorizontal +
                ", posicionVertical=" + posicionVertical +
                ", timestamp='" + timestamp + '\'' +
                ", nonce='" + nonce + '\'' +
                ", isSecure=" + isSecureRequest() +
                ", hasDocument=" + (documentoPdf != null && !documentoPdf.isEmpty()) +
                '}';
    }
}