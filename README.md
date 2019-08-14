# FPLOS

FPLOS er en enkeltstående app, fpsak skal ikke ha noe kunnskap om hva/hvordan oppgaver blir del ut. All
kommunikasjon fra FPSAK til FPLOS skal skje via hendelseskøer.

https://confluence.adeo.no/display/MODNAV/Tekniske+skisser


## Bygge og kjøre docker lokalt
Greit for å undersøke om containerne fungerer. Har lagt med noen variabler i `docker.list` denne er ikke 100% og bør
trolig settes fra feks `es-vtp.properties`, men det er nok til å sjekke at basisen fungerer.
```
mvn -B -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true  -DskipTests clean install

docker build -t fplos .

docker run -d --env-file=docker.list --name fplos fplos
```




## Kjøring lokalt

`yarn dev` i mappend `web/klient/` for å starte frontend


`no.nav.foreldrepenger.los.web.server.jetty.JettyDevServer` started i Intellij. Med
flagget `--vtp` så går den mot Virtuell Tjenesteplattform. Denne må selvsagt kjøre på 
standard porter. Merk du trenger trolig sertifikater om applikasjonen bruker tjenester
på soap. Dette er pga WS-secure, etc.
