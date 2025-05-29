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
     * Endpoint seguro para firmar documentos PDF
     */
    @POST
    public Response signDocument(SecureSigningRequest request, @Context HttpHeaders headers) {
        LOGGER.info("[SECURE_PDF_CONTROLLER] Recibida solicitud de firma");
        
        try {
            if (request == null) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Solicitud vacía");
            }
            
            // Determinar si es request seguro o legacy
            if (request.isSecureRequest()) {
                return handleSecureRequest(request, headers);
            } else {
                return handleLegacyRequest(request, headers);
            }
            
        } catch (Exception e) {
            LOGGER.severe("[SECURE_PDF_CONTROLLER] Error: " + e.getMessage());
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, "Error interno");
        }
    }
    
    /**
     * Maneja requests seguros con JWT + Cifrado
     */
    private Response handleSecureRequest(SecureSigningRequest request, HttpHeaders headers) {
        try {
            // Extraer headers de seguridad
            String authHeader = headers.getHeaderString("Authorization");
            String requestSignature = headers.getHeaderString("X-Request-Signature");
            String timestamp = headers.getHeaderString("X-Timestamp");
            String nonce = headers.getHeaderString("X-Nonce");
            
            if (authHeader == null || requestSignature == null || timestamp == null || nonce == null) {
                return createErrorResponse(Response.Status.BAD_REQUEST, 
                    "Headers de seguridad requeridos");
            }
            
            // Validar request completo
            String requestJson = objectMapper.writeValueAsString(request);
            Claims userClaims = SecurityUtils.validateSecureRequest(
                authHeader, requestJson, timestamp, nonce, requestSignature);
            
            // Descifrar datos sensibles
            String decryptedCertificate = SecurityUtils.decryptAES(request.getEncryptedCertificadoP12());
            String decryptedPassword = SecurityUtils.decryptAES(request.getEncryptedContrasena());
            
            // Crear request tradicional
            SigningRequest decryptedRequest = new SigningRequest();
            decryptedRequest.setDocumentoPdf(request.getDocumentoPdf());
            decryptedRequest.setCertificadoP12(decryptedCertificate);
            decryptedRequest.setContrasena(decryptedPassword);
            decryptedRequest.setPagina(request.getPagina());
            decryptedRequest.setPosicionHorizontal(request.getPosicionHorizontal());
            decryptedRequest.setPosicionVertical(request.getPosicionVertical());
            
            // Procesar firma
            SigningResponse response = signingService.signDocument(decryptedRequest);
            
            // Limpiar datos sensibles
            decryptedRequest.clearSensitiveData();
            request.clearSensitiveData();
            
            LOGGER.info("[SECURE_PDF_CONTROLLER] Documento firmado por: " + userClaims.get("id"));
            
            return Response.ok(response).build();
            
        } catch (Exception e) {
            LOGGER.warning("[SECURE_PDF_CONTROLLER] Error de seguridad: " + e.getMessage());
            return createErrorResponse(Response.Status.UNAUTHORIZED, "Error de autenticación");
        }
    }
    
    /**
     * Maneja requests legacy para compatibilidad
     */
    private Response handleLegacyRequest(SecureSigningRequest request, HttpHeaders headers) {
        LOGGER.warning("[SECURE_PDF_CONTROLLER] Request LEGACY detectado");
        
        String allowLegacy = System.getProperty("allow.legacy.requests", "false");
        if (!"true".equalsIgnoreCase(allowLegacy)) {
            return createErrorResponse(Response.Status.FORBIDDEN, 
                "Requests sin autenticación no permitidos");
        }
        
        try {
            // Convertir a SigningRequest legacy
            SigningRequest legacyRequest = new SigningRequest();
            legacyRequest.setDocumentoPdf(request.getDocumentoPdf());
            legacyRequest.setCertificadoP12(request.getCertificadoP12());
            legacyRequest.setContrasena(request.getContrasena());
            legacyRequest.setPagina(request.getPagina());
            legacyRequest.setPosicionHorizontal(request.getPosicionHorizontal());
            legacyRequest.setPosicionVertical(request.getPosicionVertical());
            
            SigningResponse response = signingService.signDocument(legacyRequest);
            return Response.ok(response).build();
            
        } catch (Exception e) {
            return createErrorResponse(Response.Status.INTERNAL_SERVER_ERROR, 
                "Error procesando solicitud");
        }
    }
    
    /**
     * Endpoint de información del servicio
     */
    @GET
    @Path("/info")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getServiceInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("service", "Secure PDF Signing Service");
        info.put("version", "3.0");
        info.put("features", new String[]{
            "JWT Authentication", "AES-256 Encryption", "HMAC Request Signing"
        });
        info.put("securityLevel", "Enterprise Grade");
        
        return Response.ok(info).build();
    }
    
    /**
     * Endpoint de estado del servicio
     */
    @GET
    @Path("/status")
    public Response getServiceStatus() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "Secure PDF Signing Service");
        status.put("version", "3.0");
        status.put("securityEnabled", true);
        
        return Response.ok(status).build();
    }
    
    /**
     * Validar tokens JWT
     */
    @POST
    @Path("/validate-token")
    public Response validateToken(@HeaderParam("Authorization") String authHeader) {
        try {
            if (authHeader == null) {
                return createErrorResponse(Response.Status.BAD_REQUEST, "Authorization header requerido");
            }
            
            Claims claims = SecurityUtils.validateJWT(authHeader);
            SecurityUtils.UserInfo userInfo = SecurityUtils.extractUserInfo(claims);
            
            Map<String, Object> result = new HashMap<>();
            result.put("valid", true);
            result.put("user", userInfo);
            
            return Response.ok(result).build();
            
        } catch (Exception e) {
            return createErrorResponse(Response.Status.UNAUTHORIZED, "Token inválido");
        }
    }
    
    /**
     * Crea respuesta de error estandarizada
     */
    private Response createErrorResponse(Response.Status status, String message) {
        SigningResponse errorResponse = new SigningResponse();
        errorResponse.setExitoso(false);
        errorResponse.setMensaje(message);
        
        return Response.status(status).entity(errorResponse).build();
    }
}