
package com.gadm.tulcan.rest.firmarpdf;

import com.gadm.tulcan.rest.modelo.EntradasFirmarpdf;
import com.gadm.tulcan.rest.modelo.SalidasFirmarpdf;
import com.gadm.tulcan.rest.utils.SecurityValidator;
import com.gadm.tulcan.firmarpdf.Funcion_Firmarpdf;
import java.security.KeyStoreException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/Firmarpdf")
public class firmarpdf {
    @GET
    @Produces (MediaType.TEXT_PLAIN)
    public String Home(){
        
    return "Modulo de firmado de PDF";
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response getDatos(EntradasFirmarpdf datos) {
        
        System.out.println("[FIRMA] Iniciando proceso de firma digital");
        
        try {
            // VALIDACIONES DE SEGURIDAD
            SecurityValidator.validatePdfBase64(datos.getDocumentopdf());
            SecurityValidator.validateCertificateBase64(datos.getArchivop12());
            SecurityValidator.validatePassword(datos.getContrasena());
            SecurityValidator.validatePositionParameters(datos.getPagina(), datos.getH(), datos.getV());
            
            System.out.println("[FIRMA] Validaciones completadas - Procesando documento");
            System.out.println("[FIRMA] Página: " + datos.getPagina());
            System.out.println("[FIRMA] Posición H: " + datos.getH());
            System.out.println("[FIRMA] Posición V: " + datos.getV());
            
        } catch (SecurityException e) {
            System.err.println("[FIRMA] Error de validación: " + e.getMessage());
            SalidasFirmarpdf error = new SalidasFirmarpdf();
            error.setExitoso(false);
            error.setMensaje("Error de validación: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(error).build();
        }
        
        Funcion_Firmarpdf comprobar = new Funcion_Firmarpdf();
        
        try {
            // Llamar al método Invocador que ahora devuelve SalidasFirmarpdf
            SalidasFirmarpdf resultado = comprobar.Invocador(
                datos.getDocumentopdf(), 
                datos.getArchivop12(), 
                datos.getContrasena(), 
                datos.getPagina(), 
                datos.getH(), 
                datos.getV()
            );
            
            // LIMPIAR DATOS SENSIBLES DE LA MEMORIA
            datos.setContrasena(null);
            datos.setArchivop12(null);
            datos.setDocumentopdf(null);
            
            if (resultado.isExitoso()) {
                System.out.println("[FIRMA] Documento firmado exitosamente");
                return Response.ok(resultado).build();
            } else {
                System.err.println("[FIRMA] Error en el proceso de firma: " + resultado.getMensaje());
                return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(resultado).build();
            }
            
        } catch (Exception e) {
            System.err.println("[FIRMA] Error crítico en el proceso de firma: " + e.getMessage());
            
            // LIMPIAR DATOS SENSIBLES INCLUSO EN CASO DE ERROR
            datos.setContrasena(null);
            datos.setArchivop12(null);
            datos.setDocumentopdf(null);
            
            // Devolver respuesta de error sin exponer detalles internos
            SalidasFirmarpdf error = new SalidasFirmarpdf();
            error.setExitoso(false);
            error.setMensaje("Error interno del servidor. Contacte al administrador.");
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(error).build();
        }
    }
}
 /*
{
    "ubicacion": "Aqui-va-ubicacion"
}
*/