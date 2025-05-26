
package com.gadm.tulcan.rest.modelo;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class SalidasFirmarpdf {
    private String docFirmado;           // Ruta del documento firmado
    private String docOriginal;          // Ruta del certificado original
    private String pdfFirmadoBase64;     // PDF firmado en Base64
    private String nombreArchivo;        // Nombre del archivo firmado
    private boolean exitoso;             // Si la firma fue exitosa
    private String mensaje;              // Mensaje de estado
    
    public SalidasFirmarpdf(){
    
    }

    public String getDocFirmado() {
        return docFirmado;
    }

    public void setDocFirmado(String docFirmado) {
        this.docFirmado = docFirmado;
    }

    public String getDocOriginal() {
        return docOriginal;
    }

    public void setDocOriginal(String docOriginal) {
        this.docOriginal = docOriginal;
    }

    public String getPdfFirmadoBase64() {
        return pdfFirmadoBase64;
    }

    public void setPdfFirmadoBase64(String pdfFirmadoBase64) {
        this.pdfFirmadoBase64 = pdfFirmadoBase64;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

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
}
