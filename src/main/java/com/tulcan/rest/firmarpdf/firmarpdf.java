
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
        
        EntradasFirmarpdf entradas= new EntradasFirmarpdf();
        entradas=datos;
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
         System.out.println(datos.getArchivop12());
        System.out.println(datos.getDocumentopdf());
       
        System.out.println(datos.getPagina());
        System.out.println(datos.getH());
        System.out.println(datos.getV());
        SalidasFirmarpdf firmar=new SalidasFirmarpdf();
        Funcion_Firmarpdf comprobar=new Funcion_Firmarpdf();
        SalidasFirmarpdf salida=null;
       
       
       if(comprobar.Invocador(entradas.getDocumentopdf(), entradas.getArchivop12(), entradas.getContrasena(), entradas.getPagina(), entradas.getH(), entradas.getV() )==false){
       
       salida=null;
       
       }else{
       salida=firmar;
       
       }
       
       
      
      
    return salida;
    }

    @POST
    @Path("/upload")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    @Produces(MediaType.APPLICATION_JSON)
    public SalidasFirmarpdf firmarConArchivos(
    @FormDataParam("documento") InputStream documentoStream,
    @FormDataParam("documento") FormDataContentDisposition documentoDetail,
    @FormDataParam("certificado") InputStream certificadoStream,
    @FormDataParam("certificado") FormDataContentDisposition certificadoDetail,
    @FormDataParam("contrasena") String contrasena,
    @FormDataParam("pagina") int pagina,
    @FormDataParam("h") int h,
    @FormDataParam("v") int v) throws Exception {
    
    // Guardar archivos temporalmente
    File tempPdf = File.createTempFile("doc_", ".pdf");
    File tempCert = File.createTempFile("cert_", ".p12");
    
    try {
        // Escribir streams a archivos temporales
        Files.copy(documentoStream, tempPdf.toPath(), StandardCopyOption.REPLACE_EXISTING);
        Files.copy(certificadoStream, tempCert.toPath(), StandardCopyOption.REPLACE_EXISTING);
        
        // Procesar firma
        Funcion_Firmarpdf comprobar = new Funcion_Firmarpdf();
        if (comprobar.Invocador(tempPdf.getAbsolutePath(), tempCert.getAbsolutePath(), 
                              contrasena, pagina, h, v)) {
            SalidasFirmarpdf resultado = new SalidasFirmarpdf();
            // Configurar resultado
            return resultado;
        }
        return null;
    } finally {
        // Limpiar archivos temporales
        tempPdf.delete();
        tempCert.delete();
    }
}
}
 /*
{
    "ubicacion": "Aqui-va-ubicacion"
}
*/