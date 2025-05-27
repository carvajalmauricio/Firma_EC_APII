package com.gadm.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO para respuestas de validación de PDF
 */
@XmlRootElement
public class ValidationResponse {
    
    private boolean exitoso;
    private String mensaje;
    private int cantidadFirmas;
    private List<CertificateInfo> certificados;
    private LocalDateTime fechaValidacion;
    private long tiempoValidacion; // en milisegundos
    
    // Constructor vacío
    public ValidationResponse() {
        this.fechaValidacion = LocalDateTime.now();
    }
    
    // Constructor con parámetros básicos
    public ValidationResponse(boolean exitoso, String mensaje) {
        this();
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
    
    // Constructor completo
    public ValidationResponse(boolean exitoso, String mensaje, List<CertificateInfo> certificados) {
        this();
        this.exitoso = exitoso;
        this.mensaje = mensaje;
        this.certificados = certificados;
        this.cantidadFirmas = certificados != null ? certificados.size() : 0;
    }
    
    // Métodos estáticos para crear respuestas comunes
    public static ValidationResponse success(String mensaje, List<CertificateInfo> certificados) {
        return new ValidationResponse(true, mensaje, certificados);
    }
    
    public static ValidationResponse error(String mensaje) {
        return new ValidationResponse(false, mensaje);
    }
    
    public static ValidationResponse noSignatures() {
        return new ValidationResponse(false, "No se encontraron firmas digitales en el documento");
    }
    
    // Getters y Setters
    public boolean isExitoso() {
        return exitoso;
    }
    
    public void setExitoso(boolean exitoso) {
        this.exitoso = exitoso;
    }
    
    public String getMensaje() {
        return mensaje;
    }
    
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }
    
    public int getCantidadFirmas() {
        return cantidadFirmas;
    }
    
    public void setCantidadFirmas(int cantidadFirmas) {
        this.cantidadFirmas = cantidadFirmas;
    }
    
    public List<CertificateInfo> getCertificados() {
        return certificados;
    }
    
    public void setCertificados(List<CertificateInfo> certificados) {
        this.certificados = certificados;
        this.cantidadFirmas = certificados != null ? certificados.size() : 0;
    }
    
    public LocalDateTime getFechaValidacion() {
        return fechaValidacion;
    }
    
    public void setFechaValidacion(LocalDateTime fechaValidacion) {
        this.fechaValidacion = fechaValidacion;
    }
    
    public long getTiempoValidacion() {
        return tiempoValidacion;
    }
    
    public void setTiempoValidacion(long tiempoValidacion) {
        this.tiempoValidacion = tiempoValidacion;
    }
    
    @Override
    public String toString() {
        return "ValidationResponse{" +
                "exitoso=" + exitoso +
                ", mensaje='" + mensaje + '\'' +
                ", cantidadFirmas=" + cantidadFirmas +
                ", tiempoValidacion=" + tiempoValidacion + "ms" +
                ", fechaValidacion=" + fechaValidacion +
                '}';
    }
}

/**
 * Información básica de un certificado encontrado
 */
@XmlRootElement
class CertificateInfo {
    private String nombreCompleto;
    private String cedula;
    private String entidadCertificadora;
    private LocalDateTime fechaEmision;
    private LocalDateTime fechaExpiracion;
    private boolean valido;
    private String estado;
    
    // Constructor vacío
    public CertificateInfo() {}
    
    // Getters y Setters
    public String getNombreCompleto() {
        return nombreCompleto;
    }
    
    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }
    
    public String getCedula() {
        return cedula;
    }
    
    public void setCedula(String cedula) {
        this.cedula = cedula;
    }
    
    public String getEntidadCertificadora() {
        return entidadCertificadora;
    }
    
    public void setEntidadCertificadora(String entidadCertificadora) {
        this.entidadCertificadora = entidadCertificadora;
    }
    
    public LocalDateTime getFechaEmision() {
        return fechaEmision;
    }
    
    public void setFechaEmision(LocalDateTime fechaEmision) {
        this.fechaEmision = fechaEmision;
    }
    
    public LocalDateTime getFechaExpiracion() {
        return fechaExpiracion;
    }
    
    public void setFechaExpiracion(LocalDateTime fechaExpiracion) {
        this.fechaExpiracion = fechaExpiracion;
    }
    
    public boolean isValido() {
        return valido;
    }
    
    public void setValido(boolean valido) {
        this.valido = valido;
    }
    
    public String getEstado() {
        return estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }
}
