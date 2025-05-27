package com.gadm.tulcan.rest.services;

import com.gadm.tulcan.rest.dto.ValidationRequest;
import com.gadm.tulcan.rest.dto.ValidationResponse;
import com.gadm.tulcan.rest.utils.SecurityValidator;
import com.gadm.tulcan.rest.certificados.certificados;
import com.gadm.tulcan.validarpdf.Funcion_Validarpdf;
import com.gadm.tulcan.rest.modelo.SalidasValidarpdf;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Servicio para manejo de validación de PDFs firmados
 * Contiene toda la lógica de negocio relacionada con la validación
 */
@ApplicationScoped
public class PdfValidationService {
    
    private static final Logger LOGGER = Logger.getLogger(PdfValidationService.class.getName());
    
    /**
     * Valida las firmas digitales de un documento PDF
     * 
     * @param request Datos de la solicitud de validación
     * @return Respuesta con el resultado de la validación
     */
    public ValidationResponse validateDocument(ValidationRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            LOGGER.info("[VALIDATION_SERVICE] Iniciando proceso de validación de PDF");
            
            // 1. Validar entrada
            ValidationResponse validationResult = validateRequest(request);
            if (!validationResult.isExitoso()) {
                return validationResult;
            }
            
            // 2. Procesar validación
            ValidationResponse result = processValidation(request);
            
            // 3. Calcular tiempo de procesamiento
            long processingTime = System.currentTimeMillis() - startTime;
            result.setTiempoValidacion(processingTime);
            
            // 4. Limpiar datos sensibles
            request.clearSensitiveData();
            
            LOGGER.info(String.format("[VALIDATION_SERVICE] Proceso completado en %d ms. Exitoso: %s", 
                       processingTime, result.isExitoso()));
            
            return result;
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[VALIDATION_SERVICE] Error crítico en el proceso de validación", e);
            
            // Limpiar datos sensibles incluso en caso de error
            request.clearSensitiveData();
            
            return ValidationResponse.error("Error interno durante el proceso de validación");
        }
    }
    
    /**
     * Valida la solicitud de validación
     */
    private ValidationResponse validateRequest(ValidationRequest request) {
        try {
            LOGGER.info("[VALIDATION_SERVICE] Validando solicitud de validación");
            
            // Validar que el documento no esté vacío
            if (request.getDocumentoPdf() == null || request.getDocumentoPdf().trim().isEmpty()) {
                return ValidationResponse.error("El documento PDF es requerido");
            }
            
            // Validar formato Base64 del PDF
            SecurityValidator.validatePdfBase64(request.getDocumentoPdf());
            
            LOGGER.info("[VALIDATION_SERVICE] Validación de solicitud completada exitosamente");
            return ValidationResponse.success("Validación exitosa", null);
            
        } catch (SecurityException e) {
            LOGGER.warning(String.format("[VALIDATION_SERVICE] Error de validación: %s", e.getMessage()));
            return ValidationResponse.error("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[VALIDATION_SERVICE] Error inesperado durante validación de solicitud", e);
            return ValidationResponse.error("Error interno durante la validación de la solicitud");
        }
    }
    
    /**
     * Procesa la validación del documento PDF
     */
    private ValidationResponse processValidation(ValidationRequest request) {
        try {
            LOGGER.info("[VALIDATION_SERVICE] Iniciando proceso de validación de firmas");
            
            // Usar las funciones existentes de validación
            certificados firmas = new certificados();
            Funcion_Validarpdf validador = new Funcion_Validarpdf();
            firmas.Encerar();
            
            // Procesar validación
            boolean validacionExitosa = validador.Invocador(request.getDocumentoPdf());
            
            if (!validacionExitosa) {
                LOGGER.warning("[VALIDATION_SERVICE] No se pudo procesar el documento para validación");
                return ValidationResponse.error("No se pudo procesar el documento PDF para validación");
            }
            
            // Obtener resultados de la validación
            List<SalidasValidarpdf> resultados = firmas.getListado();
            
            if (resultados == null || resultados.isEmpty()) {
                LOGGER.info("[VALIDATION_SERVICE] No se encontraron firmas en el documento");
                return ValidationResponse.noSignatures();
            }
            
            // Convertir resultados al nuevo formato
            return convertValidationResults(resultados);
            
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[VALIDATION_SERVICE] Error durante el proceso de validación", e);
            
            // Analizar el tipo de error
            String errorMessage = analyzeValidationError(e);
            return ValidationResponse.error(errorMessage);
        }
    }
    
    /**
     * Convierte los resultados del sistema legacy al nuevo formato
     */
    private ValidationResponse convertValidationResults(List<SalidasValidarpdf> legacyResults) {
        try {
            // Por ahora, retornamos información básica
            // En el futuro se puede expandir para mapear cada certificado individualmente
            
            int cantidadFirmas = legacyResults.size();
            String mensaje = String.format("Se encontraron %d firma(s) digital(es) en el documento", cantidadFirmas);
            
            LOGGER.info(String.format("[VALIDATION_SERVICE] Validación exitosa: %d firmas encontradas", cantidadFirmas));
            
            ValidationResponse response = ValidationResponse.success(mensaje, null);
            response.setCantidadFirmas(cantidadFirmas);
            
            return response;
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[VALIDATION_SERVICE] Error convirtiendo resultados de validación", e);
            return ValidationResponse.error("Error procesando los resultados de la validación");
        }
    }
    
    /**
     * Analiza el error de validación para proporcionar un mensaje más específico
     */
    private String analyzeValidationError(Exception e) {
        String errorMessage = e.getMessage();
        
        if (errorMessage == null) {
            return "Error desconocido durante el proceso de validación";
        }
        
        // Analizar errores comunes
        if (errorMessage.toLowerCase().contains("pdf")) {
            return "Error procesando el documento PDF";
        }
        
        if (errorMessage.toLowerCase().contains("signature") || 
            errorMessage.toLowerCase().contains("firma")) {
            return "Error analizando las firmas del documento";
        }
        
        if (errorMessage.toLowerCase().contains("certificate") || 
            errorMessage.toLowerCase().contains("certificado")) {
            return "Error validando los certificados del documento";
        }
        
        if (errorMessage.toLowerCase().contains("base64")) {
            return "Error en el formato de los datos recibidos";
        }
        
        if (errorMessage.toLowerCase().contains("format") || 
            errorMessage.toLowerCase().contains("formato")) {
            return "Formato de documento no soportado";
        }
        
        // Error genérico
        return "Error interno durante el proceso de validación";
    }
    
    /**
     * Verifica si un documento tiene firmas digitales válidas
     */
    public boolean hasValidSignatures(String documentoBase64) {
        try {
            ValidationRequest request = new ValidationRequest(documentoBase64);
            ValidationResponse response = validateDocument(request);
            
            return response.isExitoso() && response.getCantidadFirmas() > 0;
            
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "[VALIDATION_SERVICE] Error verificando firmas", e);
            return false;
        }
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
        private long totalValidationRequests = 0;
        private long successfulValidations = 0;
        private long failedValidations = 0;
        private long documentsWithSignatures = 0;
        private long documentsWithoutSignatures = 0;
        private double averageProcessingTime = 0.0;
        
        // Getters
        public long getTotalValidationRequests() { return totalValidationRequests; }
        public long getSuccessfulValidations() { return successfulValidations; }
        public long getFailedValidations() { return failedValidations; }
        public long getDocumentsWithSignatures() { return documentsWithSignatures; }
        public long getDocumentsWithoutSignatures() { return documentsWithoutSignatures; }
        public double getAverageProcessingTime() { return averageProcessingTime; }
    }
}
