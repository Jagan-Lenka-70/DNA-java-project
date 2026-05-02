#!/bin/sh
# ----------------------------------------------------------------------------
# Apache Maven Wrapper startup script, version 3.3.2
# ----------------------------------------------------------------------------

set -euf
[ "${MVNW_VERBOSE-}" != debug ] || set -x

MVNW_REPOURL=${MVNW_REPOURL-}
MVNW_USERNAME=${MVNW_USERNAME-}
MVNW_PASSWORD=${MVNW_PASSWORD-}
MVNW_VERBOSE=${MVNW_VERBOSE-}

while [ "$#" -gt 0 ]; do
  case "$1" in
    -Dmaven.multiModuleProjectDirectory=*)
      MAVEN_PROJECTBASEDIR=${1#-Dmaven.multiModuleProjectDirectory=}
      shift
      ;;
    *)
      break
      ;;
  esac
done

if [ -z "${MAVEN_PROJECTBASEDIR-}" ]; then
  if [ -z "${MVNW_BASEDIR-}" ]; then
    MAVEN_PROJECTBASEDIR="$(
      while [ ! -d .mvn ] && [ "$PWD" != / ]; do cd ..; done
      pwd
    )"
  else
    MAVEN_PROJECTBASEDIR="$MVNW_BASEDIR"
  fi
fi

MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"

if [ ! -f "$MAVEN_WRAPPER_PROPERTIES" ]; then
  echo "Missing $MAVEN_WRAPPER_PROPERTIES" >&2
  exit 1
fi

distributionUrl="$(grep '^distributionUrl=' "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2-)"
wrapperUrl="$(grep '^wrapperUrl=' "$MAVEN_WRAPPER_PROPERTIES" | cut -d= -f2-)"

if [ ! -f "$MAVEN_WRAPPER_JAR" ]; then
  [ -n "$wrapperUrl" ] || {
    echo "Missing wrapperUrl in $MAVEN_WRAPPER_PROPERTIES" >&2
    exit 1
  }
  [ -z "$MVNW_VERBOSE" ] || echo "Downloading Maven wrapper jar from $wrapperUrl"
  if command -v curl >/dev/null 2>&1; then
    curl -fsSL ${MVNW_USERNAME:+--user "$MVNW_USERNAME:$MVNW_PASSWORD"} "$wrapperUrl" -o "$MAVEN_WRAPPER_JAR"
  elif command -v wget >/dev/null 2>&1; then
    wget ${MVNW_USERNAME:+--user="$MVNW_USERNAME" --password="$MVNW_PASSWORD"} -q -O "$MAVEN_WRAPPER_JAR" "$wrapperUrl"
  else
    echo "Neither curl nor wget is available to download wrapper jar." >&2
    exit 1
  fi
fi

exec "${JAVA_HOME:+$JAVA_HOME/bin/}java" \
  -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" \
  -classpath "$MAVEN_WRAPPER_JAR" \
  org.apache.maven.wrapper.MavenWrapperMain \
  "$@"
