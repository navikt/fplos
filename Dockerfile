FROM navikt/java:11-appdynamics
ENV APPD_ENABLED=true

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
#COPY web/webapp/target/webapp/ /app/webapp/
COPY web/klient/target/index.html /app/klient/
COPY web/klient/target/public/ /app/klient/public/

# Application Start Command
COPY run-java.sh /
RUN chmod +x /run-java.sh

# Upload heapdump to s3
COPY s3upload-init.sh /init-scripts/
COPY s3upload.sh /
RUN chmod +x /s3upload.sh