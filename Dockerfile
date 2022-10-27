FROM ghcr.io/navikt/fp-baseimages/java:17-appdynamics

LABEL org.opencontainers.image.source=https://github.com/navikt/fplos
ENV TZ=Europe/Oslo

RUN mkdir /app/lib
RUN mkdir /app/conf

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 \
                -Djava.security.egd=file:/dev/./urandom \
                -Duser.timezone=Europe/Oslo \
                -Dklient=./klient \
                -Dlogback.configurationFile=conf/logback.xml"

# Config
COPY web/webapp/target/classes/logback*.xml /app/conf/

# Application
COPY web/klient/target/index.html /app/klient/
COPY web/klient/target/public/ /app/klient/public/

# Application Container (Jetty)
COPY web/webapp/target/lib/*.jar /app/lib/
COPY web/webapp/target/app.jar /app/
