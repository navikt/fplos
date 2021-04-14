FROM navikt/java:16-appdynamics

ENV APP_NAME=fplos
ENV APPD_ENABLED=true
ENV APPDYNAMICS_CONTROLLER_HOST_NAME=appdynamics.adeo.no
ENV APPDYNAMICS_CONTROLLER_PORT=443
ENV APPDYNAMICS_CONTROLLER_SSL_ENABLED=true
ENV TZ=Europe/Oslo

RUN mkdir /app/lib
RUN mkdir /app/webapp
RUN mkdir /app/conf

# Config
COPY web/webapp/target/classes/logback.xml /app/conf/
COPY web/webapp/target/classes/jetty/jaspi-conf.xml /app/conf/

# Application Container (Jetty)
COPY web/webapp/target/app.jar /app/
COPY web/webapp/target/lib/*.jar /app/lib/

# Application
COPY web/klient/target/index.html /app/klient/
COPY web/klient/target/public/ /app/klient/public/

COPY export-vault-secrets.sh /init-scripts/

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom -Duser.timezone=Europe/Oslo -Dklient=./klient -Dlogback.configurationFile=./conf/logback.xml --illegal-access=permit"
