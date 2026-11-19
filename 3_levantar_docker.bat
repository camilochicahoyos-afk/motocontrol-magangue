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
echo Pagina web:             http://localhost:8080
echo Documentacion tecnica:  http://localhost:8080/swagger-ui.html
echo Estado del servicio:    http://localhost:8080/actuator/health
pause
