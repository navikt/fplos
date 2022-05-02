FROM navikt/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fplos
ENV APPD_ENABLED=true
ENV TZ=Europe/Oslo

RUN mkdir /app/lib
RUN mkdir /app/conf

# Config
COPY web/webapp/target/classes/logback*.xml /app/conf/

# Application Container (Jetty)
COPY web/webapp/target/app.jar /app/
COPY web/webapp/target/lib/*.jar /app/lib/

# Application
COPY web/klient/target/index.html /app/klient/
COPY web/klient/target/public/ /app/klient/public/

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
                -Djava.security.egd=file:/dev/./urandom \
                -Duser.timezone=Europe/Oslo \
                -Dklient=./klient \
                -Dlogback.configurationFile=conf/logback.xml"

# Export vault properties
COPY .scripts/03-import-appd.sh /init-scripts/03-import-appd.sh
COPY .scripts/05-import-users.sh /init-scripts/05-import-users.sh
