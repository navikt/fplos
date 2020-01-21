# FPLOS

FPLOS håndterer oppgave- og ledelsesstyring på foreldrepengeområdet. Fpsak produserer hendelser som representerer state i behandlingsprosessen. FPLOS benytter dette for å opprette, kategoriesere og lukke oppgaver.

Oppgavestyrere definerer kø-kriterier som ligger til grunn for fordeling av oppgaver etter prioritet til saksbehandlere. 

https://confluence.adeo.no/display/TVF/FPLOS


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
