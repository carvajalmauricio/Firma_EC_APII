@echo off
echo ===================================
echo   Firma EC API - Build Script
echo ===================================

REM Verificar si Maven está instalado
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Maven no está instalado o no está en el PATH
    echo Por favor instala Maven desde: https://maven.apache.org/download.cgi
    echo Y agrega el directorio bin de Maven al PATH
    pause
    exit /b 1
)

echo Limpiando proyecto anterior...
mvn clean

echo Compilando proyecto...
mvn compile

echo Empaquetando aplicación...
mvn package

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================
    echo   BUILD EXITOSO!
    echo ===================================
    echo La aplicación se ha empaquetado correctamente.
    echo El archivo WAR está en: target\firma-ec-api.war
    echo.
    echo Para probar localmente ejecuta:
    echo java -jar target\dependency\webapp-runner.jar --port 8080 target\firma-ec-api.war
    echo.
) else (
    echo.
    echo ===================================
    echo   ERROR EN EL BUILD
    echo ===================================
    echo Revisa los errores anteriores.
)

pause
