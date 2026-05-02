@ECHO OFF
SETLOCAL

SET MAVEN_PROJECTBASEDIR=%~dp0
IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

SET WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
SET WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar

IF NOT EXIST "%WRAPPER_PROPS%" (
  ECHO Missing %WRAPPER_PROPS%
  EXIT /B 1
)

FOR /F "tokens=1,* delims==" %%A IN ('type "%WRAPPER_PROPS%" ^| findstr /B /C:"wrapperUrl="') DO SET WRAPPER_URL=%%B

IF NOT EXIST "%WRAPPER_JAR%" (
  IF "%WRAPPER_URL%"=="" (
    ECHO Missing wrapperUrl in %WRAPPER_PROPS%
    EXIT /B 1
  )
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ProgressPreference='SilentlyContinue';Invoke-WebRequest -Uri '%WRAPPER_URL%' -OutFile '%WRAPPER_JAR%'" || EXIT /B 1
)

IF "%JAVA_HOME%"=="" (
  SET JAVA_EXE=java
) ELSE (
  SET JAVA_EXE=%JAVA_HOME%\bin\java.exe
)

"%JAVA_EXE%" -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -classpath "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
ENDLOCAL
