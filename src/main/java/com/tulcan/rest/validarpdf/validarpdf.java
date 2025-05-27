package com.gadm.tulcan.rest.validarpdf;

import com.gadm.tulcan.rest.certificados.certificados;
import com.gadm.tulcan.rest.modelo.EntradasValidarpdf;
import com.gadm.tulcan.rest.modelo.SalidasValidarpdf;
import com.gadm.tulcan.rest.utils.SecurityValidator;
import com.gadm.tulcan.validarpdf.Funcion_Validarpdf;
import java.security.KeyStoreException;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Validarpdf")
public class validarpdf {
    
    @GET
    @Produces (MediaType.TEXT_PLAIN)
    public String Home(){
        return "Modulo de validacion de PDF";
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatos(EntradasValidarpdf datos) {
        
        System.out.println("[VALIDACION] Iniciando proceso de validacion de PDF");
        
        try {
            // VALIDACIONES DE SEGURIDAD
            if (datos == null || datos.getUbicacion() == null || datos.getUbicacion().trim().isEmpty()) {
                throw new SecurityException("La ubicacion del documento no puede estar vacia");
            }
            
            SecurityValidator.validatePdfBase64(datos.getUbicacion());
            
            System.out.println("[VALIDACION] Validaciones completadas - Procesando documento");
            
        } catch (SecurityException e) {
            System.err.println("[VALIDACION] Error de validacion: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                          .entity("Error de validacion: " + e.getMessage())
                          .build();
        }
        
        try {
            certificados firmas = new certificados();
            Funcion_Validarpdf comprobar = new Funcion_Validarpdf();
            firmas.Encerar();
            
            List<SalidasValidarpdf> salida = null;
            
            if (!comprobar.Invocador(datos.getUbicacion())) {
                System.err.println("[VALIDACION] Error: No se pudo validar el documento");
                return Response.status(Response.Status.BAD_REQUEST)
                              .entity("No se pudo validar el documento PDF")
                              .build();
            } else {
                salida = firmas.getListado();
                System.out.println("[VALIDACION] Documento validado exitosamente");
            }
            
            // LIMPIAR DATOS SENSIBLES
            datos.setUbicacion(null);
            
            if (salida != null && !salida.isEmpty()) {
                return Response.ok(salida).build();
            } else {
                return Response.status(Response.Status.NOT_FOUND)
                              .entity("No se encontraron firmas en el documento")
                              .build();
            }
            
        } catch (Exception e) {
            System.err.println("[VALIDACION] Error critico en el proceso de validacion: " + e.getMessage());
            
            // LIMPIAR DATOS SENSIBLES INCLUSO EN CASO DE ERROR
            datos.setUbicacion(null);
            
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                          .entity("Error interno del servidor. Contacte al administrador.")
                          .build();
        }
    }
}
/*
Ejemplo de uso:
{
    "ubicacion": "Base64-del-documento-PDF-aqui"
}
*/
