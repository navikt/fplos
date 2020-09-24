#!/usr/bin/env sh


#  fjern .\web\klient\target\web-klient-2.8.2-SNAPSHOT\WEB-INF\lib\metrics-core-3.2.5.jar
#  og slf4j-api-1.7.25.jar

set -eu

## fix classpath ifht. hvordan det bygges av Maven
export PATH_SEP=":"
[[ -z $WINDIR ]] || export PATH_SEP=";"

export CLASSPATH="$(echo $(ls web/server/target/server-*.jar))${PATH_SEP?}web/server/target/lib/*"

export WEBAPP="$(echo web/webapp/target/webapp/)"
export KLIENT="$(echo web/klient/target/web-klient-2.8.2-SNAPSHOT)"
export I18N="$(echo i18n/src/main/resources/META-INF/resources)"

LOGBACK_CONFIG="web/server/src/test/resources/logback-dev.xml"
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-}

## export app.properties til environment
PROP_FILE="web/server/app.properties"
PROP_FILE_LOCAL="web/server/app-local.properties"
PROP_FILE_TEMPLATE="web/server/src/test/resources/app-dev.properties"
[[ ! -f "${PROP_FILE?}" ]] && cp ${PROP_FILE_TEMPLATE?} ${PROP_FILE} && echo "Oppdater passwords i ${PROP_FILE_LOCAL}" && exit 1
export SYSTEM_PROPERTIES="$( grep -v "^#" $PROP_FILE | grep -v '^[[:space:]]*$' | grep -v ' ' | sed -e 's/^/ -D/g' | tr '\n' ' ')"

## export app-local.properties også til env (inneholder hemmeligheter, eks. passord)
[[ -f "${PROP_FILE_LOCAL}" ]] && export SYSTEM_PROPERTIES_LOCAL="$( grep -v "^#" $PROP_FILE_LOCAL | grep -v '^[[:space:]]*$' | grep -v ' ' | sed -e 's/^/ -D/g' | tr '\n' ' ')"

## Eksponer TRUSTSTROE til run-java.sh
export NAV_TRUSTSTORE_PATH="web/server/truststore.jts"
if ! test -r "${NAV_TRUSTSTORE_PATH}";
then
    echo "Kjør JettyDevServer en gang for å kopiere ut truststore (n.n.f.f.w.s.t.JettyDevServer#setupSikkerhetLokalt())";
    exit 1;
fi
## Eksponerer bare det som allerede ligger i no.nav.modig.testcertificates.TestCertificates og det er kun for testsystemer
export NAV_TRUSTSTORE_PASSWORD="changeit"

## Overstyr port for lokal kjøring
export SERVER_PORT=8071

## Sett opp samme struktur som i Dockerfile
DIR="conf"
if [ ! -d "${DIR}" ]; then
    mkdir "${DIR}"
fi
cp web/server/target/classes/jetty/jaspi-conf.xml conf/
cp web/server/target/test-classes/logback-dev.xml conf/logback.xml
cp web/server/target/classes/jetty/login.conf conf/

## Sample JPDA settings for remote socket debugging
#JAVA_OPTS="${JAVA_OPTS:-} -Xrunjdwp:transport=dt_socket,address=8787,server=y,suspend=n"

export JAVA_OPTS="${JAVA_OPTS:-} ${SYSTEM_PROPERTIES_LOCAL?} ${SYSTEM_PROPERTIES:-}"

set -eu

export JAVA_OPTS="${JAVA_OPTS:-} -Xmx512m -Xms128m -Djava.security.egd=file:/dev/./urandom"

export JAVA_OPTS="${JAVA_OPTS} -Djavax.xml.soap.SAAJMetaFactory=com.sun.xml.messaging.saaj.soap.SAAJMetaFactoryImpl"

export STARTUP_CLASS=${STARTUP_CLASS:-"no.nav.foreldrepenger.los.web.server.jetty.JettyServer"}
export LOGBACK_CONFIG=${LOGBACK_CONFIG:-"./conf/logback.xml"}

exec java -cp ${CLASSPATH?} ${DEFAULT_JAVA_OPTS:-} ${JAVA_OPTS} -Dlogback.configurationFile=${LOGBACK_CONFIG?} -Dklient=${KLIENT:-"./klient"} -Dwebapp=${WEBAPP:-"./webapp"} -Di18n=${I18N:-"./i18n"} ${SERVER_PORT:-8080} $@
