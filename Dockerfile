FROM navikt/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fplos
ENV TZ=Europe/Oslo

RUN mkdir /app/lib
RUN mkdir /app/conf

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 \
                -Djava.security.egd=file:/dev/./urandom \
                -Duser.timezone=Europe/Oslo \
                -Dklient=./klient \
                -Dlogback.configurationFile=conf/logback.xml"

# Import vault properties
COPY --chown=apprunner:root .scripts/03-import-appd.sh /init-scripts/03-import-appd.sh
COPY --chown=apprunner:root .scripts/05-import-users.sh /init-scripts/05-import-users.sh
COPY --chown=apprunner:root .scripts/08-remote-debug.sh /init-scripts/08-remote-debug.sh

# Config
COPY web/webapp/target/classes/logback*.xml /app/conf/

# Application
COPY web/klient/target/index.html /app/klient/
COPY web/klient/target/public/ /app/klient/public/

# Application Container (Jetty)
COPY web/webapp/target/lib/*.jar /app/lib/
COPY web/webapp/target/app.jar /app/
