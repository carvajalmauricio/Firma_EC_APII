package com.tulcan.rest.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Utilidades de seguridad para validación JWT, cifrado AES y request signing
 */
public class SecurityUtils {
    
    private static final Logger LOGGER = Logger.getLogger(SecurityUtils.class.getName());
    
    // Constantes de seguridad (deben coincidir con el frontend)
    private static final String JWT_SECRET = "ISMmsioonObyBnOPMOImp223imnimoOMDOPW,ANBFVYRBF";
    private static final String AES_KEY = "MedicalSystemSecureKey2024!@#$%";
    private static final String HMAC_SECRET = "HMACSecretForRequestSigning789";
    
    // Cache para nonces usados (prevenir replay attacks)
    private static final ConcurrentHashMap<String, Long> usedNonces = new ConcurrentHashMap<>();
    
    // Algoritmos
    private static final String AES_ALGORITHM = "AES";
    private static final String HMAC_ALGORITHM = "HmacSHA256";
    
    /**
     * Valida un token JWT
     */
    public static Claims validateJWT(String token) throws Exception {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            
            Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
            
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            
            LOGGER.info("JWT validado exitosamente para usuario: " + claims.get("id"));
            return claims;
            
        } catch (Exception e) {
            LOGGER.warning("Token JWT inválido: " + e.getMessage());
            throw new Exception("Token de autenticación inválido: " + e.getMessage());
        }
    }
    
    /**
     * Descifra datos usando AES-256
     */
    public static String decryptAES(String encryptedData) throws Exception {
        try {
            SecretKeySpec secretKey = new SecretKeySpec(
                AES_KEY.getBytes(StandardCharsets.UTF_8), AES_ALGORITHM);
            
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
            
        } catch (Exception e) {
            LOGGER.severe("Error al descifrar datos AES: " + e.getMessage());
            throw new Exception("Error en descifrado de datos sensibles");
        }
    }
    
    /**
     * Valida la firma HMAC del request
     */
    public static boolean validateRequestSignature(String payload, String timestamp, 
                                                 String nonce, String providedSignature) {
        try {
            String canonicalString = String.join("\n",
                "POST",
                "/api/pdf/sign",
                timestamp,
                nonce,
                payload
            );
            
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec(
                HMAC_SECRET.getBytes(StandardCharsets.UTF_8), HMAC_ALGORITHM);
            mac.init(secretKeySpec);
            
            byte[] hmacBytes = mac.doFinal(canonicalString.getBytes(StandardCharsets.UTF_8));
            String expectedSignature = bytesToHex(hmacBytes);
            
            return constantTimeEquals(expectedSignature, providedSignature);
            
        } catch (Exception e) {
            LOGGER.warning("Error al validar firma del request: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida el timestamp para prevenir ataques de replay
     */
    public static boolean validateTimestamp(String timestamp) {
        try {
            Instant requestTime = Instant.parse(timestamp);
            Instant now = Instant.now();
            
            long minutesDiff = ChronoUnit.MINUTES.between(requestTime, now);
            
            if (Math.abs(minutesDiff) > 5) {
                LOGGER.warning("Timestamp fuera del rango válido: " + minutesDiff + " minutos");
                return false;
            }
            
            return true;
            
        } catch (Exception e) {
            LOGGER.warning("Timestamp inválido: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Valida y registra un nonce para prevenir replay attacks
     */
    public static boolean validateNonce(String nonce) {
        if (nonce == null || nonce.isEmpty()) {
            return false;
        }
        
        long currentTime = System.currentTimeMillis();
        
        if (usedNonces.containsKey(nonce)) {
            LOGGER.warning("Nonce duplicado detectado: " + nonce);
            return false;
        }
        
        usedNonces.put(nonce, currentTime);
        cleanupOldNonces(currentTime);
        
        return true;
    }
    
    /**
     * Limpia nonces antiguos del cache
     */
    private static void cleanupOldNonces(long currentTime) {
        final long TEN_MINUTES = 10 * 60 * 1000;
        
        usedNonces.entrySet().removeIf(entry -> 
            (currentTime - entry.getValue()) > TEN_MINUTES);
    }
    
    /**
     * Convierte bytes a hexadecimal
     */
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }
    
    /**
     * Comparación de strings en tiempo constante
     */
    private static boolean constantTimeEquals(String a, String b) {
        if (a == null || b == null) {
            return a == b;
        }
        
        if (a.length() != b.length()) {
            return false;
        }
        
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        
        return result == 0;
    }
    
    /**
     * Valida completamente un request seguro
     */
    public static Claims validateSecureRequest(String token, String payload, 
                                             String timestamp, String nonce, 
                                             String signature) throws Exception {
        
        Claims claims = validateJWT(token);
        
        if (!validateTimestamp(timestamp)) {
            throw new Exception("Timestamp inválido o fuera de rango");
        }
        
        if (!validateNonce(nonce)) {
            throw new Exception("Nonce inválido o ya utilizado");
        }
        
        if (!validateRequestSignature(payload, timestamp, nonce, signature)) {
            throw new Exception("Firma del request inválida");
        }
        
        LOGGER.info("Request seguro validado exitosamente para usuario: " + claims.get("id"));
        return claims;
    }
    
    /**
     * Extrae información del usuario desde los claims JWT
     */
    public static UserInfo extractUserInfo(Claims claims) {
        return new UserInfo(
            claims.get("id", String.class),
            claims.get("email", String.class),
            claims.get("rol", String.class)
        );
    }
    
    /**
     * Obtiene el número de nonces activos
     */
    public static int getActiveNoncesCount() {
        return usedNonces.size();
    }
    
    /**
     * Obtiene el timestamp de la última limpieza
     */
    public static long getLastCleanupTime() {
        return System.currentTimeMillis();
    }
    
    /**
     * Clase para almacenar información del usuario
     */
    public static class UserInfo {
        private final String id;
        private final String email;
        private final String rol;
        
        public UserInfo(String id, String email, String rol) {
            this.id = id;
            this.email = email;
            this.rol = rol;
        }
        
        public String getId() { return id; }
        public String getEmail() { return email; }
        public String getRol() { return rol; }
        
        @Override
        public String toString() {
            return "UserInfo{id='" + id + "', email='" + email + "', rol='" + rol + "'}";
        }
    }
}