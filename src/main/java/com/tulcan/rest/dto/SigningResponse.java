package com.gadm.tulcan.rest.dto;

import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;

/**
 * DTO para respuestas de firma de PDF
 */
@XmlRootElement
public class SigningResponse {
    
    private boolean exitoso;
    private String mensaje;
    private String nombreArchivo;
    private String pdfFirmadoBase64;
    private String rutaDocumentoFirmado;
    private String rutaCertificadoOriginal;
    private LocalDateTime fechaProcesamiento;
    private long tiempoProcesamiento; // en milisegundos
    private int tamanioArchivo; // en bytes
    
    // Constructor vacío
    public SigningResponse() {
        this.fechaProcesamiento = LocalDateTime.now();
    }
    
    // Constructor para respuestas exitosas
    public SigningResponse(boolean exitoso, String mensaje) {
        this();
        this.exitoso = exitoso;
        this.mensaje = mensaje;
    }
    
    // Constructor completo para respuestas exitosas
    public SigningResponse(String mensaje, String nombreArchivo, String pdfFirmadoBase64, 
                          String rutaDocumentoFirmado, String rutaCertificadoOriginal) {
        this();
        this.exitoso = true;
        this.mensaje = mensaje;
        this.nombreArchivo = nombreArchivo;
        this.pdfFirmadoBase64 = pdfFirmadoBase64;
        this.rutaDocumentoFirmado = rutaDocumentoFirmado;
        this.rutaCertificadoOriginal = rutaCertificadoOriginal;
    }
    
    // Métodos estáticos para crear respuestas comunes
    public static SigningResponse success(String mensaje) {
        return new SigningResponse(true, mensaje);
    }
    
    public static SigningResponse error(String mensaje) {
        return new SigningResponse(false, mensaje);
    }
    
    public static SigningResponse validationError(String mensaje) {
        return new SigningResponse(false, "Error de validación: " + mensaje);
    }
    
    public static SigningResponse serverError(String mensaje) {
        return new SigningResponse(false, "Error interno del servidor: " + mensaje);
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
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }
    
    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    public String getPdfFirmadoBase64() {
        return pdfFirmadoBase64;
    }
    
    public void setPdfFirmadoBase64(String pdfFirmadoBase64) {
        this.pdfFirmadoBase64 = pdfFirmadoBase64;
        // Calcular tamaño aproximado del archivo
        if (pdfFirmadoBase64 != null) {
            this.tamanioArchivo = (int) (pdfFirmadoBase64.length() * 0.75); // Aproximación Base64 a bytes
        }
    }
    
    public String getRutaDocumentoFirmado() {
        return rutaDocumentoFirmado;
    }
    
    public void setRutaDocumentoFirmado(String rutaDocumentoFirmado) {
        this.rutaDocumentoFirmado = rutaDocumentoFirmado;
    }
    
    public String getRutaCertificadoOriginal() {
        return rutaCertificadoOriginal;
    }
    
    public void setRutaCertificadoOriginal(String rutaCertificadoOriginal) {
        this.rutaCertificadoOriginal = rutaCertificadoOriginal;
    }
    
    public LocalDateTime getFechaProcesamiento() {
        return fechaProcesamiento;
    }
    
    public void setFechaProcesamiento(LocalDateTime fechaProcesamiento) {
        this.fechaProcesamiento = fechaProcesamiento;
    }
    
    public long getTiempoProcesamiento() {
        return tiempoProcesamiento;
    }
    
    public void setTiempoProcesamiento(long tiempoProcesamiento) {
        this.tiempoProcesamiento = tiempoProcesamiento;
    }
    
    public int getTamanioArchivo() {
        return tamanioArchivo;
    }
    
    public void setTamanioArchivo(int tamanioArchivo) {
        this.tamanioArchivo = tamanioArchivo;
    }
    
    @Override
    public String toString() {
        return "SigningResponse{" +
                "exitoso=" + exitoso +
                ", mensaje='" + mensaje + '\'' +
                ", nombreArchivo='" + nombreArchivo + '\'' +
                ", tiempoProcesamiento=" + tiempoProcesamiento + "ms" +
                ", tamanioArchivo=" + tamanioArchivo + " bytes" +
                ", fechaProcesamiento=" + fechaProcesamiento +
                '}';
    }
}
