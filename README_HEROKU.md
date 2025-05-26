# Firma EC API

Componente para realizar y validar firmas electrónicas en documentos PDF, desarrollado en Java con JAX-RS.

## Características

- **Firmar PDFs**: Endpoint para aplicar firmas electrónicas a documentos PDF
- **Validar PDFs**: Endpoint para verificar firmas electrónicas en documentos
- **Health Check**: Monitoreo del estado de la aplicación
- **CORS habilitado**: Permite peticiones desde diferentes dominios

## Endpoints disponibles

### Health Check
- `GET /api/health` - Estado de la aplicación (JSON)
- `GET /api/health/status` - Estado simple (texto)

### Firmar PDF
- `POST /api/Firmarpdf` - Firmar documento PDF
- `GET /api/Firmarpdf` - Información del módulo

### Validar PDF
- `POST /api/Validarpdf` - Validar firmas en PDF
- `GET /api/Validarpdf` - Información del módulo

## Despliegue en Heroku

### Opción 1: Usando la extensión de GitHub para Heroku

1. **Instalar la extensión de Heroku para GitHub**
2. **Conectar tu repositorio**: Ve a tu dashboard de Heroku y conecta este repositorio
3. **Configurar despliegue automático**: Habilita el despliegue automático desde la rama principal
4. **Desplegar**: Cada push al repositorio desplegará automáticamente

### Opción 2: Usando Heroku CLI

```bash
# Instalar Heroku CLI y hacer login
heroku login

# Crear aplicación en Heroku
heroku create tu-nombre-app

# Configurar buildpack de Java
heroku buildpacks:set heroku/java

# Desplegar
git push heroku main

# Ver logs
heroku logs --tail
```

### Opción 3: Deploy con un click

[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)

## Configuración

El proyecto está configurado con:

- **Java 11**: Runtime de Java
- **Maven**: Gestión de dependencias y build
- **Jersey**: Implementación de JAX-RS
- **Webapp Runner**: Servidor embebido para Heroku

## Variables de entorno

- `JAVA_TOOL_OPTIONS`: Configuración de memoria JVM
- `MAVEN_CUSTOM_OPTS`: Opciones adicionales de Maven

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   └── com/gadm/tulcan/rest/
│   │       ├── firmarpdf/       # Endpoints para firmar PDFs
│   │       ├── validarpdf/      # Endpoints para validar PDFs
│   │       ├── health/          # Health checks
│   │       ├── filters/         # Filtros CORS
│   │       └── modelo/          # Modelos de datos
│   ├── resources/               # Archivos de configuración
│   └── webapp/                  # Recursos web
│       └── WEB-INF/
│           └── web.xml          # Configuración del servlet
├── Procfile                     # Configuración de proceso Heroku
├── system.properties            # Versión de Java
└── app.json                     # Configuración de la app Heroku
```

## Desarrollo local

```bash
# Compilar
mvn clean compile

# Empaquetar
mvn clean package

# Ejecutar localmente
java -jar target/dependency/webapp-runner.jar --port 8080 target/*.war
```

## Tecnologías utilizadas

- Java 11
- JAX-RS (Jersey)
- Maven
- iText7 (manipulación de PDFs)
- BouncyCastle (criptografía)
- SQLite (base de datos)

## Licencia

Este proyecto está bajo licencia AGPL v3.
