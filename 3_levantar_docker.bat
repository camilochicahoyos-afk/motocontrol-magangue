@echo off
title MotoControl - docker compose
cd /d "%~dp0"
echo ===== docker compose up -d --build =====
docker compose up -d --build > docker.log 2>&1
type docker.log
echo.
echo ===== docker compose ps =====
docker compose ps
docker compose ps >> docker.log 2>&1
echo.
echo API:      http://localhost:8080/api/v1/conductores
echo Swagger:  http://localhost:8080/swagger-ui.html
echo Health:   http://localhost:8080/actuator/health
pause
