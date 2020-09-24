#!/usr/bin/env sh
set -eu

export JAVA_OPTS="${JAVA_OPTS:-} -Xmx1024m -Xms128m -Djava.security.egd=file:/dev/./urandom"

# hvor skal gc log, heap dump etc kunne skrives til med Docker?
export todo_JAVA_OPTS="${JAVA_OPTS} -XX:ErrorFile=./hs_err_pid<pid>.log -XX:HeapDumpPath=./java_pid<pid>.hprof -XX:-HeapDumpOnOutOfMemoryError -Xloggc:<filename>"
export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.los.web.server.jetty.JettyServer"}
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-"./conf/logback.xml"}

exec java -cp "app.jar:lib/*"${EXTRA_CLASS_PATH:-""} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} \
    -Dlogback.configurationFile=${LOGBACK_CONFIG?} \
    -Dconf=${CONF:-"./conf"} \
    -Dwebapp=${WEBAPP:-"./webapp"} \
    -Dklient=${KLIENT:-"./klient"} \
    -Di18n=${I18N:-"./i18n"} \
    -Djavax.xml.soap.SAAJMetaFactory="com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl" $@
