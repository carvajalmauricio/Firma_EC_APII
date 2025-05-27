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
@ApplicationPath(\"/api\")\npublic class RestConfiguration extends Application {\n    \n    @Override\n    public Set<Class<?>> getClasses() {\n        Set<Class<?>> classes = new HashSet<>();\n        \n        // Registrar controladores nuevos\n        classes.add(PdfSigningController.class);\n        classes.add(PdfValidationController.class);\n        \n        // Mantener controladores legacy para compatibilidad (temporal)\n        classes.add(com.gadm.tulcan.rest.firmarpdf.firmarpdf.class);\n        classes.add(com.gadm.tulcan.rest.validarpdf.validarpdf.class);\n        \n        // Servicios de salud y monitoreo\n        classes.add(HealthCheck.class);\n        \n        // Filtros\n        classes.add(CorsFilter.class);\n        \n        return classes;\n    }\n    \n    @Override\n    public Set<Object> getSingletons() {\n        Set<Object> singletons = new HashSet<>();\n        // Aquí se pueden registrar instancias singleton si es necesario\n        return singletons;\n    }\n    \n    /**\n     * Obtiene información de la configuración de la aplicación\n     */\n    public static ApplicationInfo getApplicationInfo() {\n        return new ApplicationInfo();\n    }\n    \n    /**\n     * Clase interna para información de la aplicación\n     */\n    public static class ApplicationInfo {\n        private final String name = \"Firma EC API\";\n        private final String version = \"2.0.0\";\n        private final String description = \"API REST para firma y validación digital de documentos PDF\";\n        private final String[] supportedFormats = {\"PDF\"};\n        private final String[] supportedCertificates = {\"P12\", \"PFX\"};\n        \n        // Getters\n        public String getName() { return name; }\n        public String getVersion() { return version; }\n        public String getDescription() { return description; }\n        public String[] getSupportedFormats() { return supportedFormats; }\n        public String[] getSupportedCertificates() { return supportedCertificates; }\n        \n        @Override\n        public String toString() {\n            return String.format(\"%s v%s - %s\", name, version, description);\n        }\n    }\n}"