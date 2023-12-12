FROM ghcr.io/navikt/fp-baseimages/java:21

LABEL org.opencontainers.image.source=https://github.com/navikt/fplos
ENV TZ=Europe/Oslo

RUN mkdir lib
RUN mkdir conf

ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 \
                -XX:+PrintCommandLineFlags \
                -Djava.security.egd=file:/dev/urandom \
                -Duser.timezone=Europe/Oslo \
                -Dlogback.configurationFile=conf/logback.xml"

# Config
COPY target/classes/logback*.xml ./conf/

# Application Container (Jetty)
COPY target/lib/*.jar ./lib/
COPY target/app.jar .
