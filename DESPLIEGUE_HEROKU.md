# Guía de Despliegue en Heroku - Firma EC API

## ✅ Tu proyecto ya está listo para Heroku

He configurado tu proyecto con todos los archivos necesarios para el despliegue en Heroku:

### Archivos configurados:
- ✅ `Procfile` - Comando para ejecutar la aplicación
- ✅ `system.properties` - Versión de Java (11)
- ✅ `app.json` - Configuración de la aplicación Heroku
- ✅ `pom.xml` - Dependencias y plugins actualizados
- ✅ `web.xml` - Configuración del servlet container
- ✅ `RestApplication.java` - Configuración JAX-RS
- ✅ `CorsFilter.java` - Filtro para peticiones cross-origin
- ✅ `HealthCheck.java` - Endpoint para monitoreo

## 🚀 Opciones de Despliegue

### Opción 1: GitHub + Heroku (Recomendado)

1. **Sube tu código a GitHub**:
   ```bash
   git init
   git add .
   git commit -m "Configuración inicial para Heroku"
   git remote add origin https://github.com/carvajalmauricio/Firma_EC_APII.git
   git push -u origin main
   ```

2. **En Heroku Dashboard**:
   - Ve a [dashboard.heroku.com](https://dashboard.heroku.com)
   - Click "New" → "Create new app"
   - Nombre: `tu-firma-ec-api` (debe ser único)
   - Región: United States o Europe
   - Click "Create app"

3. **Conectar con GitHub**:
   - En la pestaña "Deploy"
   - Sección "Deployment method" → Click "GitHub"
   - Busca tu repositorio "Firma_EC_API"
   - Click "Connect"
   - Habilita "Automatic deploys" si quieres despliegue automático

4. **Deploy Manual**:
   - En "Manual deploy" → Click "Deploy Branch"

### Opción 2: Heroku CLI

1. **Instalar Heroku CLI**:
   - Descarga desde: https://devcenter.heroku.com/articles/heroku-cli

2. **Comandos**:
   ```bash
   heroku login
   heroku create tu-firma-ec-api
   git push heroku main
   ```

### Opción 3: Deploy Button (Un Click)

Usa este botón en tu README:

```markdown
[![Deploy to Heroku](https://www.herokucdn.com/deploy/button.svg)](https://heroku.com/deploy)
```

## 📋 Requisitos Previos

### En tu máquina local:
1. **Git** instalado
2. **Maven** instalado (opcional, Heroku lo maneja)
3. **Java 11** (opcional para desarrollo local)

### Verificar Maven (opcional):
```bash
mvn --version
```

Si no tienes Maven, puedes usar el script `build.bat` que incluí.

## 🧪 Probar la Aplicación

Una vez desplegada, tu API estará disponible en:
```
https://tu-app-name.herokuapp.com
```

### Endpoints de prueba:
- **Health Check**: `GET https://tu-app.herokuapp.com/api/health`
- **Estado**: `GET https://tu-app.herokuapp.com/api/health/status`
- **Firmar PDF**: `POST https://tu-app.herokuapp.com/api/Firmarpdf`
- **Validar PDF**: `POST https://tu-app.herokuapp.com/api/Validarpdf`

## 🔧 Configuración Adicional

### Variables de Entorno (si necesitas):
```bash
heroku config:set JAVA_TOOL_OPTIONS="-Xmx512m"
heroku config:set CUSTOM_CONFIG="valor"
```

### Ver Logs:
```bash
heroku logs --tail
```

### Escalar la aplicación:
```bash
heroku ps:scale web=1
```

## 📝 Notas Importantes

1. **Límites del plan gratuito**:
   - La app "dormirá" después de 30 min de inactividad
   - 550 horas gratis por mes
   - 512 MB de RAM

2. **Primera respuesta lenta**:
   - La primera petición puede tardar 10-30 segundos (cold start)
   - Considera usar un servicio de "ping" para mantenerla activa

3. **Archivos temporales**:
   - Heroku tiene un sistema de archivos efímero
   - Los archivos se borran al reiniciar
   - Usa servicios externos para almacenamiento persistente

## 🆘 Solución de Problemas

### Error de build:
```bash
heroku logs --tail
```

### Verificar buildpack:
```bash
heroku buildpacks
heroku buildpacks:set heroku/java
```

### Reiniciar aplicación:
```bash
heroku restart
```

## ✨ Mejoras Futuras

Para producción considera:
- Base de datos externa (PostgreSQL)
- Almacenamiento de archivos (AWS S3)
- Monitoreo y alertas
- CDN para archivos estáticos
- Certificados SSL personalizados

---

¡Tu aplicación está lista para desplegarse! 🎉
