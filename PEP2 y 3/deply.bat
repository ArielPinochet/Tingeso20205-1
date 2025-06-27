@echo off
set DOCKER_USER=magikarp511
set BD_USER=postgres
set BD_PASS=1253

echo 🔹 Construyendo y subiendo imágenes a Docker Hub...

REM Config Service
echo 🚀 Procesando config-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/config-service:latest .\config-service
docker push %DOCKER_USER%/config-service:latest

REM Eureka Service
echo 🚀 Procesando eureka-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/eureka-service:latest .\eureka-service
docker push %DOCKER_USER%/eureka-service:latest

REM Gateway Service
echo 🚀 Procesando gateway-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/gateway-service:latest .\gateway-service
docker push %DOCKER_USER%/gateway-service:latest

REM ReservaPago Service
echo 🚀 Procesando reservapago-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/reservapago-service:latest .\Tingeso.ReservaPago
docker push %DOCKER_USER%/reservapago-service:latest

REM ClienteFrecuente Service
echo 🚀 Procesando clientefrecuente-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/clientefrecuente-service:latest .\Tingeso.ClienteFrecuente
docker push %DOCKER_USER%/clientefrecuente-service:latest

REM Descuentos Service
echo 🚀 Procesando descuentos-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/descuentos-service:latest .\Tingeso.Descuento
docker push %DOCKER_USER%/descuentos-service:latest

REM Tarifas Service
echo 🚀 Procesando tarifas-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/tarifas-service:latest .\Tingeso.Tarifas
docker push %DOCKER_USER%/tarifas-service:latest

REM TarifaEspecial Service
echo 🚀 Procesando tarifa-especial-service...
docker build --build-arg BD_USER=%BD_USER% --build-arg BD_PASS=%BD_PASS% -t %DOCKER_USER%/tarifa-especial-service:latest .\Tingeso.TarifaEspeciales
docker push %DOCKER_USER%/tarifa-especial-service:latest

REM Frontend
echo 🚀 Procesando frontend...
docker build -t %DOCKER_USER%/frontend:latest .\frontend
docker push %DOCKER_USER%/frontend:latest

echo ✅ ¡Todas las imágenes han sido subidas correctamente!
pause
