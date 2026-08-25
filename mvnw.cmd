@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Batch Script for Windows
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%" == "" @ECHO OFF
@SETLOCAL

set MAVEN_VERSION=3.9.9
set MAVEN_ZIP=apache-maven-%MAVEN_VERSION%-bin.zip
set MAVEN_URL=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/%MAVEN_ZIP%
set WRAPPER_DIR=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MAVEN_HOME=%WRAPPER_DIR%\apache-maven-%MAVEN_VERSION%

if exist "%MAVEN_HOME%\bin\mvn.cmd" goto runMaven

echo [INFO] Maven not found. Downloading Apache Maven %MAVEN_VERSION%...
if not exist "%WRAPPER_DIR%" mkdir "%WRAPPER_DIR%"
powershell -Command "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; (New-Object System.Net.WebClient).DownloadFile('%MAVEN_URL%', '%WRAPPER_DIR%\%MAVEN_ZIP%')"
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Failed to download Maven from %MAVEN_URL%
    exit /b 1
)

echo [INFO] Extracting Maven...
powershell -Command "Expand-Archive -Path '%WRAPPER_DIR%\%MAVEN_ZIP%' -DestinationPath '%WRAPPER_DIR%' -Force"
del "%WRAPPER_DIR%\%MAVEN_ZIP%"

:runMaven
"%MAVEN_HOME%\bin\mvn.cmd" %*
