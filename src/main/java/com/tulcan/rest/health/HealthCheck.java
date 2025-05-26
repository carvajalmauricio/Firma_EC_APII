package com.gadm.tulcan.rest.health;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;

/**
 * Endpoint para verificar el estado de la aplicación
 */
@Path("/health")
public class HealthCheck {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Firma_EC_API");
        response.put("timestamp", System.currentTimeMillis());
        response.put("version", "1.0.0");
        
        return Response.ok(response).build();
    }
    
    @GET
    @Path("/status")
    @Produces(MediaType.TEXT_PLAIN)
    public String getStatus() {
        return "API is running successfully!";
    }
}
