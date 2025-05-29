package com.gadm.tulcan.rest.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gadm.tulcan.rest.dto.SigningRequest;
import com.gadm.tulcan.rest.dto.SecureSigningRequest;
import com.gadm.tulcan.rest.dto.SigningResponse;
import com.gadm.tulcan.rest.services.PdfSigningService;
import com.gadm.tulcan.rest.utils.SecurityUtils;
import io.jsonwebtoken.Claims;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Controlador REST seguro para operaciones de firma digital de PDFs
 * Versión 3.0: JWT + Cifrado AES + Request Signing
 */
@Path("/pdf/sign")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PdfSigningController {
    
    private static final Logger LOGGER = Logger.getLogger(PdfSigningController.class.getName());
    
    private PdfSigningService signingService = new PdfSigningService();
    private ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Endpoint para firmar un documento PDF
     * 
     * @param request Datos de la solicitud de firma
     * @return Respuesta con el documento firmado o error
     */
    @POST
    public Response signDocument(SigningRequest request) {
        LOGGER.info("[PDF_SIGNING_CONTROLLER] Recibida solicitud de firma");
        
        try {
            // Validar que la solicitud no sea nula
            if (request == null) {
                LOGGER.warning("[PDF_SIGNING_CONTROLLER] Solicitud nula recibida");
                SigningResponse errorResponse = SigningResponse.validationError("Solicitud vacía");
                return Response.status(Response.Status.BAD_REQUEST)
                             .entity(errorResponse)
                             .build();
            }
            
            // Log de la solicitud (sin datos sensibles)
            LOGGER.info(String.format("[PDF_SIGNING_CONTROLLER] Procesando solicitud: %s", request.toString()));
            
            // Delegar al servicio
            SigningResponse response = signingService.signDocument(request);
            
            // Determinar código de respuesta HTTP basado en el resultado
            Response.Status status = determineHttpStatus(response);
            
            LOGGER.info(String.format("[PDF_SIGNING_CONTROLLER] Respuesta enviada con status: %s", status));
            
            return Response.status(status)
                         .entity(response)
                         .build();
                         
        } catch (Exception e) {
            LOGGER.severe(String.format("[PDF_SIGNING_CONTROLLER] Error inesperado: %s", e.getMessage()));
            
            SigningResponse errorResponse = SigningResponse.serverError("Error interno del servidor");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                         .entity(errorResponse)
                         .build();
        }
    }
    
    /**
     * Endpoint de información del servicio de firma
     * 
     * @return Información básica del servicio
     */
    @GET
    @Path("/info")
    @Produces(MediaType.TEXT_PLAIN)
    public Response getServiceInfo() {
        String info = "Servicio de Firma Digital de PDFs\n" +
                     "Versión: 2.0\n" +
                     "Estado: Activo\n" +
                     "Formatos soportados: PDF\n" +
                     "Certificados soportados: P12, PFX";
        
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
            PdfSigningService.ServiceStats stats = signingService.getServiceStats();
            
            Map<String, Object> statusMap = new HashMap<>();
            statusMap.put("status", "UP");
            statusMap.put("service", "PDF Signing Service");
            statusMap.put("version", "2.0");
            statusMap.put("timestamp", System.currentTimeMillis());
            statusMap.put("stats", stats);
            
            return Response.ok()
                         .entity(statusMap)
                         .build();
                         
        } catch (Exception e) {
            LOGGER.warning(String.format("[PDF_SIGNING_CONTROLLER] Error obteniendo estado: %s", e.getMessage()));
            
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
    private Response.Status determineHttpStatus(SigningResponse response) {
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
                mensaje.toLowerCase().contains("validation")) {
                return Response.Status.BAD_REQUEST;
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
