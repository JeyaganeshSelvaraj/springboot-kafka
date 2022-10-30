#!/usr/bin/env bash

APP_DIR=$(dirname "$0")
#APP_DIR=$(realpath "$BASEDIR/../")
MODULE_NAME=kafkaexample
CONF_DIR="$APP_DIR/config"
export APP_DIR="$APP_DIR"
HOSTNAME=$(hostname)
if [[ -z "${PROFILE}" ]]; then
  PROFILE="dev"
fi
PROP_FILE="$CONF_DIR/application-${PROFILE}.properties"
if [[ -z "${HOSTNAME}" ]]; then
  HOSTNAME="localhost"
fi
if [[ ! -s "$PROP_FILE" ]]; then
    echo "Property file $PROP_FILE does not exist"
    exit 1
fi

if [[ -z "${XMS}" ]]; then
  XMS="4096m"
fi
if [[ -z "${XMX}" ]]; then
  XMX="4096m"
fi

echo "Running ${MODULE_NAME} .."
export JAVA_TOOL_OPTIONS=" -XX:NewRatio=2 -Xms${XMS} -Xmx${XMX}
                    -XX:+UnlockDiagnosticVMOptions
                    -XX:+UseParallelGC
                    -XX:-DisableExplicitGC
                    -XX:+HeapDumpOnOutOfMemoryError
                    -XX:HeapDumpPath=${LOG_DIR}/${HOSTNAME}.${MODULE_NAME}.$$.hprof
                    -XX:+CrashOnOutOfMemoryError
                    -XX:+ExitOnOutOfMemoryError
                    -XX:ErrorFile=${LOG_DIR}/${HOSTNAME}.${MODULE_NAME}.$$.hs_err.log
                    -XX:MaxDirectMemorySize=10M
                    -XX:MaxMetaspaceSize=93563K
                    -XX:ReservedCodeCacheSize=240M
                    -Xss1M
                    -Dorg.apache.xml.dtm.DTMManager=org.apache.xml.dtm.ref.DTMManagerDefault
                    -Djavax.xml.transform.TransformerFactory=org.apache.xalan.processor.TransformerFactoryImpl
                    -Dorg.springframework.cloud.bindings.boot.enable=true "
java  "org.springframework.boot.loader.JarLauncher" --spring.config.location="$PROP_FILE" "$*"
