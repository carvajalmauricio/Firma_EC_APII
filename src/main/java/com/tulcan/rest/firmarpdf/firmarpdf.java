
package com.gadm.tulcan.rest.firmarpdf;
//Eliminar estos imports
import com.gadm.tulcan.rest.modelo.EntradasFirmarpdf;
import com.gadm.tulcan.rest.modelo.SalidasFirmarpdf;
import com.gadm.tulcan.firmarpdf.Funcion_Firmarpdf;
import java.security.KeyStoreException;
//eliminar estos imports
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

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
    public SalidasFirmarpdf getDatos(EntradasFirmarpdf datos) throws KeyStoreException, Exception{
        
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        // NO imprimir contraseñas por seguridad
        System.out.println("Procesando documento PDF...");
        System.out.println("Página: " + datos.getPagina());
        System.out.println("Posición H: " + datos.getH());
        System.out.println("Posición V: " + datos.getV());
        
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
            
            return resultado;
            
        } catch (Exception e) {
            System.err.println("Error en el proceso de firma: " + e.getMessage());
            e.printStackTrace();
            
            // Devolver respuesta de error
            SalidasFirmarpdf error = new SalidasFirmarpdf();
            error.setExitoso(false);
            error.setMensaje("Error interno: " + e.getMessage());
            return error;
        }
    }
}
 /*
{
    "ubicacion": "Aqui-va-ubicacion"
}
*/