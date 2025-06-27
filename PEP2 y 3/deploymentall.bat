@echo off
setlocal enabledelayedexpansion

REM Configurar contexto de Kubernetes
kubectl config use-context docker-desktop

REM Preguntar si se desea limpiar antes de desplegar
set /p CLEAN="¿Deseas limpiar todos los recursos de Kubernetes antes de desplegar? (S/N): "
if /I "%CLEAN%"=="S" (
    echo 🔥 Limpiando recursos existentes...
    kubectl delete deployments,services,configmaps,secrets --all
    echo ✅ Limpieza completada.
)

REM Aplicar ConfigMaps y Secrets para cada microservicio
echo 🚀 Aplicando ConfigMaps y Secrets...
kubectl apply -f deployment/reservapago-db-config.yaml
kubectl apply -f deployment/clientefrecuente-db-config.yaml
kubectl apply -f deployment/descuentos-db-config.yaml
kubectl apply -f deployment/tarifas-db-config.yaml
kubectl apply -f deployment/tarifa-especial-db-config.yaml

REM Desplegar PostgreSQL
echo 🚀 Desplegando PostgreSQL...
kubectl create configmap postgres-config-map --from-literal=POSTGRES_DB=tingeso --from-literal=POSTGRES_USER=postgres --from-literal=POSTGRES_PASSWORD=1253
kubectl apply -f deployment/postgres-deployment.yaml

REM Esperar a que PostgreSQL esté listo
echo 🔄 Esperando a que PostgreSQL esté listo...
kubectl wait --for=condition=ready pod -l app=postgres-service --timeout=300s
if errorlevel 1 (
    echo ❌ Error: PostgreSQL no está listo después de 5 minutos.
    pause
    exit /b 1
)

REM Desplegar Config Service
echo 🚀 Desplegando Config Service...
kubectl apply -f deployment/config-service-deployment.yaml

REM Esperar a que Config Service esté listo
echo 🔄 Esperando a que Config Service esté listo...
kubectl wait --for=condition=ready pod -l app=config-service --timeout=300s
if errorlevel 1 (
    echo ❌ Error: Config Service no está listo después de 5 minutos.
    pause
    exit /b 1
)

REM Desplegar Eureka Service
echo 🚀 Desplegando Eureka Service...
kubectl apply -f deployment/eureka-service-deployment.yaml

REM Esperar a que Eureka Service esté listo
echo 🔄 Esperando a que Eureka Service esté listo...
kubectl wait --for=condition=ready pod -l app=eureka-service --timeout=300s
if errorlevel 1 (
    echo ❌ Error: Eureka Service no está listo después de 5 minutos.
    pause
    exit /b 1
)

REM Desplegar API Gateway
echo 🚀 Desplegando API Gateway...
kubectl apply -f deployment/gateway-service-deployment.yaml

REM Desplegar Microservicios
echo 🚀 Desplegando microservicios...
kubectl apply -f deployment/reservapago-service-deployment.yaml
kubectl apply -f deployment/clientefrecuente-service-deployment.yaml
kubectl apply -f deployment/descuentos-service-deployment.yaml
kubectl apply -f deployment/tarifas-service-deployment.yaml
kubectl apply -f deployment/tarifa-especial-service-deployment.yaml

REM Desplegar Frontend
echo 🚀 Desplegando Frontend...
kubectl apply -f deployment/frontend-deployment.yaml

echo ✅ Todos los Deployments han sido aplicados correctamente!

echo ✅ Despliegue completado exitosamente!

pause
