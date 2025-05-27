package com.gadm.tulcan.rest.services;

import com.gadm.tulcan.rest.dto.SigningRequest;
import com.gadm.tulcan.rest.dto.SigningResponse;
import com.gadm.tulcan.rest.utils.SecurityValidator;
import com.gadm.tulcan.firmarpdf.Funcion_Firmarpdf;

import javax.enterprise.context.ApplicationScoped;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servicio para manejo de firma digital de PDFs
 * Contiene toda la lógica de negocio relacionada con la firma
 */
@ApplicationScoped
public class PdfSigningService {
    
    private static final Logger LOGGER = Logger.getLogger(PdfSigningService.class.getName());
    
    /**
     * Firma un documento PDF usando un certificado digital
     * 
     * @param request Datos de la solicitud de firma
     * @return Respuesta con el resultado de la operación
     */
    public SigningResponse signDocument(SigningRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            LOGGER.info("[SIGNING_SERVICE] Iniciando proceso de firma digital");
            
            // 1. Validar entrada
            SigningResponse validationResult = validateRequest(request);
            if (!validationResult.isExitoso()) {
                return validationResult;
            }
            
            // 2. Procesar firma usando la función existente
            SigningResponse result = processSignature(request);
            
            // 3. Calcular tiempo de procesamiento
            long processingTime = System.currentTimeMillis() - startTime;
            result.setTiempoProcesamiento(processingTime);
            
            // 4. Limpiar datos sensibles
            request.clearSensitiveData();
            
            LOGGER.info(String.format("[SIGNING_SERVICE] Proceso completado en %d ms. Exitoso: %s", 
                       processingTime, result.isExitoso()));
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[SIGNING_SERVICE] Error crítico en el proceso de firma", e);
            
            // Limpiar datos sensibles incluso en caso de error
            request.clearSensitiveData();
            
            return SigningResponse.serverError("Error interno durante el proceso de firma");
        }
    }
    
    /**
     * Valida la solicitud de firma
     */
    private SigningResponse validateRequest(SigningRequest request) {
        try {
            LOGGER.info("[SIGNING_SERVICE] Validando solicitud de firma");
            
            // Validar PDF
            SecurityValidator.validatePdfBase64(request.getDocumentoPdf());
            
            // Validar certificado
            SecurityValidator.validateCertificateBase64(request.getCertificadoP12());
            
            // Validar contraseña
            SecurityValidator.validatePassword(request.getContrasena());
            
            // Validar parámetros de posición
            SecurityValidator.validatePositionParameters(
                request.getPagina(), 
                request.getPosicionHorizontal(), 
                request.getPosicionVertical()
            );
            
            LOGGER.info("[SIGNING_SERVICE] Validación completada exitosamente");
            return SigningResponse.success("Validación exitosa");
            
        } catch (SecurityException e) {
            LOGGER.warning(String.format("[SIGNING_SERVICE] Error de validación: %s", e.getMessage()));
            return SigningResponse.validationError(e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[SIGNING_SERVICE] Error inesperado durante validación", e);
            return SigningResponse.serverError("Error interno durante la validación");
        }
    }
    
    /**
     * Procesa la firma digital del documento
     */
    private SigningResponse processSignature(SigningRequest request) {
        try {
            LOGGER.info("[SIGNING_SERVICE] Iniciando proceso de firma digital");
            
            // Usar la función existente de firma
            Funcion_Firmarpdf firmador = new Funcion_Firmarpdf();
            
            // Convertir la respuesta antigua al nuevo formato
            com.gadm.tulcan.rest.modelo.SalidasFirmarpdf resultadoLegacy = firmador.Invocador(
                request.getDocumentoPdf(),
                request.getCertificadoP12(),
                request.getContrasena(),
                request.getPagina(),
                request.getPosicionHorizontal(),
                request.getPosicionVertical()
            );
            
            // Convertir al nuevo formato de respuesta
            return convertLegacyResponse(resultadoLegacy);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[SIGNING_SERVICE] Error durante el proceso de firma", e);
            
            // Analizar el tipo de error para dar una respuesta más específica
            String errorMessage = analyzeError(e);
            return SigningResponse.serverError(errorMessage);
        }
    }
    
    /**
     * Convierte la respuesta del sistema legacy al nuevo formato
     */
    private SigningResponse convertLegacyResponse(com.gadm.tulcan.rest.modelo.SalidasFirmarpdf legacy) {
        if (legacy == null) {
            return SigningResponse.serverError("No se recibió respuesta del sistema de firma");
        }
        
        if (legacy.isExitoso()) {
            SigningResponse response = SigningResponse.success(legacy.getMensaje());
            response.setNombreArchivo(legacy.getNombreArchivo());
            response.setPdfFirmadoBase64(legacy.getPdfFirmadoBase64());
            response.setRutaDocumentoFirmado(legacy.getDocFirmado());
            response.setRutaCertificadoOriginal(legacy.getDocOriginal());
            
            LOGGER.info(String.format("[SIGNING_SERVICE] Firma exitosa: %s", legacy.getNombreArchivo()));
            return response;
        } else {
            LOGGER.warning(String.format("[SIGNING_SERVICE] Firma falló: %s", legacy.getMensaje()));
            return SigningResponse.error(legacy.getMensaje());
        }
    }
    
    /**
     * Analiza el error para proporcionar un mensaje más específico
     */
    private String analyzeError(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage == null) {
            return "Error desconocido durante el proceso de firma";
        }
        
        // Analizar errores comunes
        if (errorMessage.toLowerCase().contains("password") || 
            errorMessage.toLowerCase().contains("contraseña")) {
            return "Error con la contraseña del certificado";
        }
        
        if (errorMessage.toLowerCase().contains("certificate") || 
            errorMessage.toLowerCase().contains("certificado")) {
            return "Error con el certificado digital";
        }
        
        if (errorMessage.toLowerCase().contains("pdf")) {
            return "Error procesando el documento PDF";
        }
        
        if (errorMessage.toLowerCase().contains("base64")) {
            return "Error en el formato de los datos recibidos";
        }
        
        // Error genérico
        return "Error interno durante el proceso de firma";
    }
    
    /**
     * Obtiene estadísticas del servicio (para monitoreo futuro)
     */
    public ServiceStats getServiceStats() {
        // Placeholder para futuras métricas
        return new ServiceStats();
    }
    
    /**
     * Clase interna para estadísticas del servicio
     */
    public static class ServiceStats {
        private long totalSigningRequests = 0;
        private long successfulSignings = 0;
        private long failedSignings = 0;
        private double averageProcessingTime = 0.0;
        
        // Getters
        public long getTotalSigningRequests() { return totalSigningRequests; }
        public long getSuccessfulSignings() { return successfulSignings; }
        public long getFailedSignings() { return failedSignings; }
        public double getAverageProcessingTime() { return averageProcessingTime; }
    }
}
