@ECHO OFF
SETLOCAL ENABLEDELAYEDEXPANSION

SET MAVEN_PROJECTBASEDIR=%~dp0
IF "%MAVEN_PROJECTBASEDIR:~-1%"=="\" SET MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%

SET WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
SET WRAPPER_PROPS=%WRAPPER_DIR%\maven-wrapper.properties
SET MAVEN_ZIP=%WRAPPER_DIR%\apache-maven-bin.zip
SET MAVEN_HOME=%WRAPPER_DIR%\apache-maven

IF NOT EXIST "%WRAPPER_PROPS%" (
  ECHO Missing %WRAPPER_PROPS%
  EXIT /B 1
)

SET DISTRIBUTION_URL=
FOR /F "usebackq tokens=1,* delims==" %%A IN ("%WRAPPER_PROPS%") DO (
  IF "%%A"=="distributionUrl" SET DISTRIBUTION_URL=%%B
)

IF "%DISTRIBUTION_URL%"=="" (
  ECHO Missing distributionUrl in %WRAPPER_PROPS%
  EXIT /B 1
)

IF NOT EXIST "%MAVEN_ZIP%" (
  FOR %%F IN ("%WRAPPER_DIR%\apache-maven-*-bin.zip") DO (
    IF EXIST "%%~fF" SET MAVEN_ZIP=%%~fF
  )
)

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  IF NOT EXIST "%MAVEN_ZIP%" (
    ECHO Downloading Maven distribution...
    powershell -NoProfile -ExecutionPolicy Bypass -Command ^
      "$ProgressPreference='SilentlyContinue';Invoke-WebRequest -Uri '%DISTRIBUTION_URL%' -OutFile '%MAVEN_ZIP%'" || EXIT /B 1
  )
  ECHO Extracting Maven distribution...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "Expand-Archive -Path '%MAVEN_ZIP%' -DestinationPath '%WRAPPER_DIR%' -Force" || EXIT /B 1
  FOR /D %%D IN ("%WRAPPER_DIR%\apache-maven-*") DO (
    IF EXIST "%%D\bin\mvn.cmd" (
      IF /I NOT "%%D"=="%MAVEN_HOME%" (
        RMDIR /S /Q "%MAVEN_HOME%" 2>NUL
        MOVE "%%D" "%MAVEN_HOME%" >NUL
      )
    )
  )
)

IF NOT EXIST "%MAVEN_HOME%\bin\mvn.cmd" (
  ECHO Maven extraction failed. Please delete %WRAPPER_DIR% and retry.
  EXIT /B 1
)

CALL "%MAVEN_HOME%\bin\mvn.cmd" %*
ENDLOCAL
