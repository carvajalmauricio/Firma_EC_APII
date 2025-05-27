package com.gadm.tulcan.rest.utils;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Validador de seguridad para datos de entrada
 */
public class SecurityValidator {
    
    // Tamaño máximo para archivos PDF en Base64 (10MB aproximadamente)
    private static final int MAX_PDF_SIZE_BASE64 = 14000000; // 10MB * 1.4 (overhead Base64)
    
    // Tamaño máximo para certificados P12 en Base64 (5MB aproximadamente)
    private static final int MAX_CERT_SIZE_BASE64 = 7000000; // 5MB * 1.4
    
    // Patrón para validar Base64
    private static final Pattern BASE64_PATTERN = Pattern.compile("^[A-Za-z0-9+/]*={0,2}$");
    
    /**
     * Valida que el PDF en Base64 sea válido
     */
    public static void validatePdfBase64(String pdfBase64) throws SecurityException {
        if (pdfBase64 == null || pdfBase64.trim().isEmpty()) {
            throw new SecurityException("El documento PDF no puede estar vacío");
        }
        
        if (pdfBase64.length() > MAX_PDF_SIZE_BASE64) {
            throw new SecurityException("El documento PDF excede el tamaño máximo permitido (10MB)");
        }
        
        if (!BASE64_PATTERN.matcher(pdfBase64).matches()) {
            throw new SecurityException("El documento PDF no tiene un formato Base64 válido");
        }
        
        try {
            Base64.getDecoder().decode(pdfBase64);
        } catch (IllegalArgumentException e) {
            throw new SecurityException("El documento PDF no es un Base64 válido");
        }
    }
    
    /**
     * Valida que el certificado P12 en Base64 sea válido
     */
    public static void validateCertificateBase64(String certBase64) throws SecurityException {
        if (certBase64 == null || certBase64.trim().isEmpty()) {
            throw new SecurityException("El certificado no puede estar vacío");
        }
        
        if (certBase64.length() > MAX_CERT_SIZE_BASE64) {
            throw new SecurityException("El certificado excede el tamaño máximo permitido (5MB)");
        }
        
        if (!BASE64_PATTERN.matcher(certBase64).matches()) {
            throw new SecurityException("El certificado no tiene un formato Base64 válido");
        }
        
        try {
            Base64.getDecoder().decode(certBase64);
        } catch (IllegalArgumentException e) {
            throw new SecurityException("El certificado no es un Base64 válido");
        }
    }
    
    /**
     * Valida que la contraseña sea válida
     */
    public static void validatePassword(String password) throws SecurityException {
        if (password == null || password.trim().isEmpty()) {
            throw new SecurityException("La contraseña no puede estar vacía");
        }
        
        if (password.length() > 100) {
            throw new SecurityException("La contraseña excede la longitud máxima permitida");
        }
        
        // Verificar que no contenga caracteres peligrosos
        if (password.contains("../") || password.contains("..\\") || 
            password.contains("<") || password.contains(">") ||
            password.contains("'") || password.contains("\"")) {
            throw new SecurityException("La contraseña contiene caracteres no permitidos");
        }
    }
    
    /**
     * Valida los parámetros de posición
     */
    public static void validatePositionParameters(int pagina, int h, int v) throws SecurityException {
        if (pagina < 0 || pagina > 1000) {
            throw new SecurityException("El número de página debe estar entre 0 y 1000");
        }
        
        if (h < 0 || h > 2000) {
            throw new SecurityException("La posición horizontal debe estar entre 0 y 2000");
        }
        
        if (v < 0 || v > 2000) {
            throw new SecurityException("La posición vertical debe estar entre 0 y 2000");
        }
    }
    
    /**
     * Limpia de forma segura una cadena que contiene datos sensibles
     */
    public static void clearSensitiveString(StringBuilder sb) {
        if (sb != null) {
            // Sobrescribir con ceros
            for (int i = 0; i < sb.length(); i++) {
                sb.setCharAt(i, '\0');
            }
            sb.setLength(0);
        }
    }
    
    /**
     * Limpia de forma segura un array de caracteres
     */
    public static void clearSensitiveCharArray(char[] chars) {
        if (chars != null) {
            for (int i = 0; i < chars.length; i++) {
                chars[i] = '\0';
            }
        }
    }
}
