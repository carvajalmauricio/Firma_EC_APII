package com.gadm.tulcan.rest;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuración de la aplicación JAX-RS
 */
@ApplicationPath("/api")
public class RestApplication extends Application {    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<>();
        
        // Registrar los recursos REST
        classes.add(com.gadm.tulcan.rest.firmarpdf.firmarpdf.class);
        classes.add(com.gadm.tulcan.rest.validarpdf.validarpdf.class);
        classes.add(com.gadm.tulcan.rest.health.HealthCheck.class);
        
        // Registrar filtros
        classes.add(com.gadm.tulcan.rest.filters.CorsFilter.class);
        
        return classes;
    }
}
