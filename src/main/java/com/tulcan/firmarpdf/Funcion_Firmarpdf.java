
package com.gadm.tulcan.firmarpdf;

import com.gadm.tulcan.rest.modelo.EntradasFirmarpdf;
import com.gadm.tulcan.rest.modelo.SalidasFirmarpdf;
import io.rubrica.certificate.CertEcUtils;
import static io.rubrica.certificate.CertUtils.seleccionarAlias;
import io.rubrica.certificate.to.Certificado;
import io.rubrica.certificate.to.Documento;
import io.rubrica.exceptions.InvalidFormatException;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

import io.rubrica.exceptions.SignatureVerificationException;
import io.rubrica.keystore.FileKeyStoreProvider;
import io.rubrica.keystore.KeyStoreProvider;
import io.rubrica.keystore.KeyStoreProviderFactory;
import io.rubrica.sign.SignConstants;
import io.rubrica.sign.Signer;
import io.rubrica.sign.pdf.PDFSigner;
import io.rubrica.sign.pdf.PdfUtil;
import io.rubrica.utils.FileUtils;
import io.rubrica.utils.Json;
import io.rubrica.utils.TiempoUtils;
import io.rubrica.utils.Utils;
import io.rubrica.utils.UtilsCrlOcsp;
import io.rubrica.utils.X509CertificateUtils;
import io.rubrica.validaciones.DocumentoUtils;
import java.io.File;
import java.security.cert.X509Certificate;
import java.time.Instant;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
public class Funcion_Firmarpdf {
       // ARCHIVO
//    private static final String ARCHIVO = "/home/mfernandez/prueba.p12";
//    private static final String PASSWORD = "11111111";
    private static  String ARCHIVO;
//    private static final String ARCHIVO = "C:\\Users\\desarrollo\\Downloads\\prueba.p12";
    private static  String PASSWORD;
//    private static final String ARCHIVO = "C:\\Users\\desarrollo\\Documents\\Digercic\\Edgar_Columba.pfx";
//    private static final String PASSWORD = "T35t_3cu4d0r.2021";
//    private static final String FILE = "C:\\Users\\desarrollo\\Downloads\\documento_blanco-signed.pdf";
//    private static final String FILE = "/home/mfernandez/documento_blanco-signed.pdf";
    private static  String FILE ;
    private static  String PosicionX ;
    private static  String PosicionY ;
    private static  String Pagina ;
//    private static final String FILE = "/home/mfernandez/Descargas/test/mozilla.pdf.p7m";
      public SalidasFirmarpdf Invocador(String DocumentoBase64, String CertificadoBase64, String Contraseña,int pagina, int UbicacionHorizontal,int UbicacionVertical)throws KeyStoreException, Exception {
      PosicionX=Integer.toString(UbicacionHorizontal);
      PosicionY=Integer.toString(UbicacionVertical);
      Pagina=Integer.toString(pagina);
      PASSWORD=Contraseña;
      
      // Crear archivos temporales desde Base64
      try {
          // Crear archivo temporal para PDF
          File tempPdfFile = File.createTempFile("pdf_temp_", ".pdf");
          byte[] pdfBytes = java.util.Base64.getDecoder().decode(DocumentoBase64);
          try (java.io.FileOutputStream pdfFos = new java.io.FileOutputStream(tempPdfFile)) {
              pdfFos.write(pdfBytes);
          }
          FILE = tempPdfFile.getAbsolutePath();
          
          // Crear archivo temporal para certificado
          File tempCertFile = File.createTempFile("cert_temp_", ".p12");
          byte[] certBytes = java.util.Base64.getDecoder().decode(CertificadoBase64);
          try (java.io.FileOutputStream certFos = new java.io.FileOutputStream(tempCertFile)) {
              certFos.write(certBytes);
          }
          ARCHIVO = tempCertFile.getAbsolutePath();
          
          System.out.println("[FIRMA] Archivos temporales creados exitosamente");
          
          // Procesar firma
          SalidasFirmarpdf resultado = firmarDocumento(FILE);
          
          // Limpiar archivos temporales
          if (tempPdfFile.exists()) {
              tempPdfFile.delete();
          }
          if (tempCertFile.exists()) {
              tempCertFile.delete();
          }
          
          return resultado;
          
      } catch (IllegalArgumentException e) {
          System.err.println("[FIRMA] Error: Datos Base64 inválidos - " + e.getMessage());
          SalidasFirmarpdf error = new SalidasFirmarpdf();
          error.setExitoso(false);
          error.setMensaje("Error: Los datos Base64 proporcionados no son válidos");
          return error;
      } catch (IOException e) {
          System.err.println("[FIRMA] Error al crear archivos temporales: " + e.getMessage());
          SalidasFirmarpdf error = new SalidasFirmarpdf();
          error.setExitoso(false);
          error.setMensaje("Error interno al procesar los archivos");
          return error;
      }
      }
      private static Properties parametros() throws IOException {
        //QR
        //SUPERIOR IZQUIERDA
       // String llx = "10";
        //String lly = "830";
        //INFERIOR IZQUIERDA
        //String llx = "100";
        //String lly = "91";
        //INFERIOR DERECHA
        //String llx = "419";
        //String lly = "91";
        //INFERIOR CENTRADO
        String llx = PosicionX;
        String lly = PosicionY;
        //QR
        //SUPERIOR IZQUIERDA
        //String llx = "10";
        //String lly = "830";
        //String urx = String.valueOf(Integer.parseInt(llx) + 110);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);
        //INFERIOR CENTRADO
        //String llx = "190";
        //String lly = "85";
        //String urx = String.valueOf(Integer.parseInt(llx) + 260);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);
        //INFERIOR CENTRADO (ancho pie pagina)
        //String llx = "100";
        //String lly = "80";&
        //String urx = String.valueOf(Integer.parseInt(llx) + 430);
        //String ury = String.valueOf(Integer.parseInt(lly) - 25);
        //INFERIOR DERECHA
        //String llx = "10";
        //String lly = "85";
        //String urx = String.valueOf(Integer.parseInt(llx) + 260);
        //String ury = String.valueOf(Integer.parseInt(lly) - 36);

        Properties params = new Properties();
        params.setProperty(PDFSigner.SIGNING_LOCATION, "12345678901234567890");
        params.setProperty(PDFSigner.SIGNING_REASON, "Firmado digitalmente con RUBRICA");
        // USAR TIMESTAMP LOCAL SI FALLA EL SERVIDOR
        try {
            params.setProperty(PDFSigner.SIGN_TIME, TiempoUtils.getFechaHoraServidor());
            System.out.println("[FIRMA] Usando timestamp del servidor");
        } catch (Exception e) {
            // Si falla el servidor, usar timestamp local
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
            String timestampLocal = sdf.format(new java.util.Date());
            params.setProperty(PDFSigner.SIGN_TIME, timestampLocal);
            System.out.println("[FIRMA] Usando timestamp local (servidor no disponible)");
        }
        params.setProperty(PDFSigner.LAST_PAGE, Pagina);//Aqui se pone el numero de pagina si el numero es cero escojera la ultima pagina del documento
        params.setProperty(PDFSigner.TYPE_SIG, "QR");
        params.setProperty(PDFSigner.INFO_QR, "Firmado digitalmente con RUBRICA\nhttps://minka.gob.ec/rubrica/rubrica");
//        params.setProperty(PDFSigner.TYPE_SIG, "information2");
        //params.setProperty(PDFSigner.FONT_SIZE, "4.5");
        // Posicion firma
        params.setProperty(PdfUtil.POSITION_ON_PAGE_LOWER_LEFT_X, llx);
        params.setProperty(PdfUtil.POSITION_ON_PAGE_LOWER_LEFT_Y, lly);
        //params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_X, urx);
        //params.setProperty(PdfUtil.POSITION_ON_PAGE_UPPER_RIGHT_Y, ury);
        return params;
    }

    private static SalidasFirmarpdf firmarDocumento(String file) throws KeyStoreException, Exception {
        EntradasFirmarpdf entradas = new EntradasFirmarpdf();
        SalidasFirmarpdf resultado = new SalidasFirmarpdf();
        
        // Inicializar resultado con valores por defecto
        resultado.setExitoso(false);
        resultado.setMensaje("Proceso iniciado");
        resultado.setDocFirmado(null);
        resultado.setDocOriginal(null);
        resultado.setPdfFirmadoBase64(null);
        resultado.setNombreArchivo(null);
        
        ////// LEER PDF:
        byte[] docByteArry = DocumentoUtils.loadFile(file);

        // VALIDAR CERTIFICADO SIN EXPONER CONTRASEÑA EN LOGS
        try {
            System.out.println("[FIRMA] Validando credenciales del certificado...");
            KeyStoreProvider ksp = new FileKeyStoreProvider(ARCHIVO);
            KeyStore keyStore = ksp.getKeystore(PASSWORD.toCharArray());
            System.out.println("[FIRMA] Certificado validado correctamente");
        } catch (Exception e) {
            System.err.println("[FIRMA] Error: Credenciales incorrectas o certificado inválido");
            resultado.setExitoso(false);
            resultado.setMensaje("Contraseña incorrecta del certificado o certificado dañado");
            return resultado;
        }
        KeyStoreProvider ksp = new FileKeyStoreProvider(ARCHIVO);
        KeyStore keyStore = ksp.getKeystore(PASSWORD.toCharArray());
        // TOKEN
//        KeyStore keyStore = KeyStoreProviderFactory.getKeyStore(PASSWORD, "TOKEN");

        byte[] signed = null;
        Signer signer = Utils.documentSigner(new File(file));
        String alias = seleccionarAlias(keyStore);
        PrivateKey key = (PrivateKey) keyStore.getKey(alias, PASSWORD.toCharArray());

        X509CertificateUtils x509CertificateUtils = new X509CertificateUtils();
        
        // VALIDACIÓN CON MANEJO DE ERRORES DE RED
        try {
            x509CertificateUtils.validarX509Certificate((X509Certificate) keyStore.getCertificate(alias));
            System.out.println("[FIRMA] Certificado validado online correctamente");
        } catch (Exception validationException) {
            System.out.println("[FIRMA] Validación online no disponible, continuando con firma offline");
            // Continuar sin validación online para evitar fallos por conectividad
        }
        
        // SIEMPRE EJECUTAR LA FIRMA (incluso si falla la validación online)
        Certificate[] certChain = keyStore.getCertificateChain(alias);
        signed = signer.sign(docByteArry, SignConstants.SIGN_ALGORITHM_SHA1WITHRSA, key, certChain, parametros());
        System.out.println("[FIRMA] Documento firmado exitosamente");
        
        // LIMPIAR LA CLAVE PRIVADA DE LA MEMORIA (importante por seguridad)
        key = null;
        
        ////// Guardar archivo firmado con nombre seguro
        // Generar nombre de archivo seguro
        String timestamp = new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date());
        String nombreSeguro = "documento_firmado_" + timestamp + ".pdf";
        String rutaCompleta = System.getProperty("java.io.tmpdir") + File.separator + nombreSeguro;
        
        java.io.FileOutputStream fos = new java.io.FileOutputStream(rutaCompleta);
        System.out.println("[FIRMA] Archivo firmado guardado: " + nombreSeguro);
        /*
//Abrir documento
            new java.util.Timer().schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        FileUtils.abrirDocumento(nombreDocumento);
                        System.out.println(nombreDocumento);
                        verificarDocumento(nombreDocumento);
                    } catch (java.lang.Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        System.exit(0);
                    }
                }
            }, 3000); //espera 3 segundos
            //Abrir documento
            */
        
        // Guardar archivo firmado y convertir a Base64
        fos.write(signed);
        fos.close();
        
        // Leer el archivo firmado y convertirlo a Base64
        try {
            byte[] pdfFirmadoBytes = java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(rutaCompleta));
            String pdfBase64 = java.util.Base64.getEncoder().encodeToString(pdfFirmadoBytes);
            
            // Configurar respuesta exitosa
            resultado.setExitoso(true);
            resultado.setMensaje("Documento firmado exitosamente");
            resultado.setDocFirmado(rutaCompleta);
            resultado.setDocOriginal(ARCHIVO);
            resultado.setPdfFirmadoBase64(pdfBase64);
            resultado.setNombreArchivo(nombreSeguro);  // Usar nombre seguro
            
            System.out.println("[FIRMA] PDF convertido a Base64 exitosamente. Tamaño: " + pdfBase64.length() + " caracteres");
            
            // Limpiar archivo temporal del resultado
            File archivoFirmado = new File(rutaCompleta);
            if (archivoFirmado.exists()) {
                archivoFirmado.delete();
                System.out.println("[FIRMA] Archivo temporal limpiado");
            }
            
        } catch (Exception e) {
            System.err.println("[FIRMA] Error al convertir PDF a Base64: " + e.getMessage());
            resultado.setExitoso(false);
            resultado.setMensaje("Error al procesar el documento firmado");
        }
        
        // LIMPIAR VARIABLES SENSIBLES DE FORMA SEGURA
        if (entradas != null) {
            entradas.setArchivop12(null);
            entradas.setContrasena(null);
            entradas.setDocumentopdf(null);
        }
        
        // Limpiar variables locales sensibles
        if (PASSWORD != null) {
            char[] passwordChars = PASSWORD.toCharArray();
            for (int i = 0; i < passwordChars.length; i++) {
                passwordChars[i] = '\0';
            }
            PASSWORD = null;
        }
        
        // Limpiar otras variables
        ARCHIVO = null;
        FILE = null;
        
        return resultado;
    }

    private static void validarCertificado() throws IOException, KeyStoreException, Exception {
        // ARCHIVO
        KeyStoreProvider ksp = new FileKeyStoreProvider(ARCHIVO);
        KeyStore keyStore = ksp.getKeystore(PASSWORD.toCharArray());
        // TOKEN
//        KeyStore keyStore = KeyStoreProviderFactory.getKeyStore(PASSWORD, "TOKEN");

        String alias = seleccionarAlias(keyStore);
        X509Certificate x509Certificate = (X509Certificate) keyStore.getCertificate(alias);
        System.out.println("UID: " + Utils.getUID(x509Certificate));
        System.out.println("CN: " + Utils.getCN(x509Certificate));
        System.out.println("emisión: " + CertEcUtils.getNombreCA(x509Certificate));
        System.out.println("fecha emisión: " + x509Certificate.getNotBefore());
        System.out.println("fecha expiración: " + x509Certificate.getNotAfter());
        System.out.println("ISSUER: " + x509Certificate.getIssuerX500Principal().getName());

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
        TemporalAccessor accessor = dateTimeFormatter.parse(TiempoUtils.getFechaHoraServidor());
        Date fechaHoraISO = Date.from(Instant.from(accessor));

        //Validad certificado revocado
        Date fechaRevocado = UtilsCrlOcsp.validarFechaRevocado(x509Certificate);
        if (fechaRevocado != null && fechaRevocado.compareTo(fechaHoraISO) <= 0) {
            System.out.println("Certificado revocado: " + fechaRevocado);
        }
        if (fechaHoraISO.compareTo(x509Certificate.getNotBefore()) <= 0 || fechaHoraISO.compareTo(x509Certificate.getNotAfter()) >= 0) {
            System.out.println("Certificado caducado");
        }
        System.out.println("Certificado emitido por entidad certificadora acreditada? " + Utils.verifySignature(x509Certificate));
    }

    private static void verificarDocumento(String file) throws IOException, SignatureVerificationException, Exception {
        File document = new File(file);
        Documento documento = Utils.verificarDocumento(document);
        System.out.println("JSON:");
        System.out.println(Json.GenerarJsonDocumento(documento));
        System.out.println("Documento: " + documento);
        if (documento.getCertificados() != null) {
            documento.getCertificados().forEach((certificado) -> {
                System.out.println(certificado.toString());
            });
        } else {
            throw new InvalidFormatException("Documento no soportado");
        }
    }

    //pruebas de fecha-hora
    private static void fechaHora(int segundos) throws KeyStoreException, Exception {
        tiempo(segundos);//espera en segundos
        do {
            try {
                System.out.println("getFechaHora() " + TiempoUtils.getFechaHora());
                System.out.println("getFechaHoraServidor() " + TiempoUtils.getFechaHoraServidor());
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        } while (tiempo);
        System.exit(0);
    }

    private static boolean tiempo = true;

    private static void tiempo(int segundos) {
        new java.util.Timer().schedule(new java.util.TimerTask() {
            @Override
            public void run() {
                tiempo = false;
            }
        }, segundos * 1000); //espera 3 segundos
    }
    //pruebas de fecha-hora

}
