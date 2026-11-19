@echo off
title MotoControl - mvn test (pruebas unitarias JUnit)
cd /d "%~dp0"
set "MVN=C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
if not defined JAVA_HOME for /d %%J in ("C:\Program Files\Java\jdk*") do set "JAVA_HOME=%%J"
if not defined JAVA_HOME for /d %%J in ("C:\Program Files\Eclipse Adoptium\jdk*") do set "JAVA_HOME=%%J"
if not defined JAVA_HOME for /d %%J in ("C:\Program Files\Microsoft\jdk*") do set "JAVA_HOME=%%J"
echo JAVA_HOME=%JAVA_HOME%
echo.
powershell -NoProfile -Command "& '%MVN%' -B test 2>&1 | Tee-Object -FilePath pruebas.log"
echo.
pause
