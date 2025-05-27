package com.gadm.tulcan.rest.controllers;

import com.gadm.tulcan.rest.dto.ValidationRequest;
import com.gadm.tulcan.rest.dto.ValidationResponse;
import com.gadm.tulcan.rest.services.PdfValidationService;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controlador REST para operaciones de validación de PDFs firmados
 * Maneja únicamente las operaciones HTTP, delegando la lógica de negocio al servicio
 */
@Path("/pdf/validate")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PdfValidationController {
    
    private static final Logger LOGGER = Logger.getLogger(PdfValidationController.class.getName());
    
    private PdfValidationService validationService = new PdfValidationService();
    
    /**
     * Endpoint para validar las firmas de un documento PDF
     * 
     * @param request Datos de la solicitud de validación
     * @return Respuesta con información de las firmas encontradas
     */
    @POST
    public Response validateDocument(ValidationRequest request) {
        LOGGER.info("[PDF_VALIDATION_CONTROLLER] Recibida solicitud de validación");
        
        try {
            // Validar que la solicitud no sea nula
            if (request == null) {
                LOGGER.warning("[PDF_VALIDATION_CONTROLLER] Solicitud nula recibida");
                ValidationResponse errorResponse = ValidationResponse.error("Solicitud vacía");
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity(errorResponse)
                             .build();
            }
            
            // Log de la solicitud (sin datos sensibles)
            LOGGER.info(String.format("[PDF_VALIDATION_CONTROLLER] Procesando solicitud: %s", request.toString()));
            
            // Delegar al servicio
            ValidationResponse response = validationService.validateDocument(request);
            
            // Determinar código de respuesta HTTP basado en el resultado
            Response.Status status = determineHttpStatus(response);
            
            LOGGER.info(String.format("[PDF_VALIDATION_CONTROLLER] Respuesta enviada con status: %s", status));
            
            return Response.status(status)
                         .entity(response)
                         .build();
                         
        } catch (Exception e) {
            LOGGER.severe(String.format("[PDF_VALIDATION_CONTROLLER] Error inesperado: %s", e.getMessage()));
            
            ValidationResponse errorResponse = ValidationResponse.error("Error interno del servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(errorResponse)
                         .build();
        }
    }
    
    /**
     * Endpoint para verificar rápidamente si un documento tiene firmas válidas
     * 
     * @param request Datos de la solicitud
     * @return Respuesta simple indicando si tiene firmas válidas
     */
    @POST
    @Path("/quick-check")
    public Response quickValidationCheck(ValidationRequest request) {
        LOGGER.info("[PDF_VALIDATION_CONTROLLER] Recibida solicitud de verificación rápida");
        
        try {
            if (request == null || request.getDocumentoPdf() == null) {
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity(Collections.singletonMap("hasValidSignatures", false))
                             .build();
            }
            
            boolean hasValidSignatures = validationService.hasValidSignatures(request.getDocumentoPdf());
            
            return Response.ok()
                         .entity(Collections.singletonMap("hasValidSignatures", hasValidSignatures))
                         .build();
                         
        } catch (Exception e) {
            LOGGER.warning(String.format("[PDF_VALIDATION_CONTROLLER] Error en verificación rápida: %s", e.getMessage()));
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(Collections.singletonMap("hasValidSignatures", false))
                         .build();
        }
    }
    
    /**
     * Endpoint de información del servicio de validación
     * 
     * @return Información básica del servicio
     */
    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServiceInfo() {
        String info = "Servicio de Validación de PDFs Firmados\n" +
                     "Versión: 2.0\n" +
                     "Estado: Activo\n" +
                     "Formatos soportados: PDF con firmas digitales\n" +
                     "Funciones: Validación de firmas, verificación de certificados";
        
        return Response.ok(info).build();
    }
    
    /**
     * Endpoint de estado del servicio
     * 
     * @return Estado actual del servicio
     */
    @GET
    @Path("/status")
    public Response getServiceStatus() {
        try {
            // Obtener estadísticas del servicio
            PdfValidationService.ServiceStats stats = validationService.getServiceStats();
            
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", "UP");
            statusMap.put("service", "PDF Validation Service");
            statusMap.put("version", "2.0");
            statusMap.put("timestamp", System.currentTimeMillis());
            statusMap.put("stats", stats);
            
            return Response.ok().entity(statusMap).build();
                         
        } catch (Exception e) {
            LOGGER.warning(String.format("[PDF_VALIDATION_CONTROLLER] Error obteniendo estado: %s", e.getMessage()));
            
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put("status", "DOWN");
            errorMap.put("error", "Service temporarily unavailable");
            
            return Response.status(Response.Status.SERVICE_UNAVAILABLE)
                         .entity(errorMap)
                         .build();
        }
    }
    
    /**
     * Determina el código de estado HTTP apropiado basado en la respuesta del servicio
     */
    private Response.Status determineHttpStatus(ValidationResponse response) {
        if (response == null) {
            return Response.Status.INTERNAL_SERVER_ERROR;
        }
        
        if (response.isExitoso()) {
            return Response.Status.OK;
        }
        
        // Analizar el mensaje para determinar el tipo de error
        String mensaje = response.getMensaje();
        if (mensaje != null) {
            if (mensaje.toLowerCase().contains("validación") || 
                mensaje.toLowerCase().contains("validation") ||
                mensaje.toLowerCase().contains("requerido") ||
                mensaje.toLowerCase().contains("vacío")) {
                return Response.Status.BAD_REQUEST;
            }
            
            if (mensaje.toLowerCase().contains("no se encontraron firmas")) {
                return Response.Status.NOT_FOUND;
            }
            
            if (mensaje.toLowerCase().contains("interno") || 
                mensaje.toLowerCase().contains("internal")) {
                return Response.Status.INTERNAL_SERVER_ERROR;
            }
        }
        
        // Por defecto, error interno del servidor
        return Response.Status.INTERNAL_SERVER_ERROR;
    }
}
