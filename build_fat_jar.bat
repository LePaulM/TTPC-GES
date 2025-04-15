@echo off
setlocal enabledelayedexpansion

echo === [BUILD] Compilation dynamique de TTPC-GES ===

set "SRC=src"
set "OUT=out"
set "LIB=lib\sqlite-jdbc.jar"
set "MAINCLASS=Main"
set "JAR=TTPC-GES-fat.jar"

rem Nettoyage
if exist "%OUT%" rmdir /s /q "%OUT%"
mkdir "%OUT%"

echo [INFO] Compilation de tous les fichiers Java dans src/...
set "JAVA_FILES="
for /R %SRC% %%f in (*.java) do (
    set "JAVA_FILES=!JAVA_FILES! %%f"
)

javac --release 17 -cp "%LIB%" -d "%OUT%" !JAVA_FILES!
if errorlevel 1 (
    echo [ERREUR] Compilation échouée.
    pause
    exit /b
)

echo Manifest-Version: 1.0> manifest.txt
echo Main-Class: %MAINCLASS%>> manifest.txt
echo Class-Path: lib/sqlite-jdbc.jar>> manifest.txt

echo [INFO] Construction du JAR...
jar cfm %JAR% manifest.txt -C out .

echo [OK] JAR généré : %JAR%
pause
