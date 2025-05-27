package com.gadm.tulcan.rest.config;

import com.gadm.tulcan.rest.controllers.PdfSigningController;
import com.gadm.tulcan.rest.controllers.PdfValidationController;
import com.gadm.tulcan.rest.filters.CorsFilter;
import com.gadm.tulcan.rest.health.HealthCheck;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración principal de la aplicación REST
 * Consolida toda la configuración en una sola clase
 */
@ApplicationPath("/api")
public class RestConfiguration extends Application {
    
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Registrar controladores nuevos
        classes.add(PdfSigningController.class);
        classes.add(PdfValidationController.class);
        
        // Mantener controladores legacy para compatibilidad (temporal)
        classes.add(com.gadm.tulcan.rest.firmarpdf.firmarpdf.class);
        classes.add(com.gadm.tulcan.rest.validarpdf.validarpdf.class);
        
        // Servicios de salud y monitoreo
        classes.add(HealthCheck.class);
        
        // Filtros
        classes.add(CorsFilter.class);
        
        return classes;
    }
    
    @Override
    public Set<Object> getSingletons() {
        Set<Object> singletons = new HashSet<>();
        // Aquí se pueden registrar instancias singleton si es necesario
        return singletons;
    }
    
    /**
     * Obtiene información de la configuración de la aplicación
     */
    public static ApplicationInfo getApplicationInfo() {
        return new ApplicationInfo();
    }
    
    /**
     * Clase interna para información de la aplicación
     */
    public static class ApplicationInfo {
        private final String name = "Firma EC API";
        private final String version = "2.0.0";
        private final String description = "API REST para firma y validación digital de documentos PDF";
        private final String[] supportedFormats = {"PDF"};
        private final String[] supportedCertificates = {"P12", "PFX"};
        
        // Getters
        public String getName() { return name; }
        public String getVersion() { return version; }
        public String getDescription() { return description; }
        public String[] getSupportedFormats() { return supportedFormats; }
        public String[] getSupportedCertificates() { return supportedCertificates; }
        
        @Override
        public String toString() {
            return String.format("%s v%s - %s", name, version, description);
        }
    }
}
