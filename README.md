# FPLOS

FPLOS håndterer oppgave- og ledelsesstyring på foreldrepengeområdet. Fp-sak og fp-tilbake produserer hendelser som representerer tilstand i behandlingsprosessen. FPLOS lytter til hendelsene for å dekke behovet for oppgavestyring og statistikk. 

Oppgavestyrere definerer kriterier som ligger til grunn for køer som fordeler oppgaver etter prioritet til saksbehandlere. 

https://confluence.adeo.no/display/TVF/FP-LOS

## Skisse

![Skisse av løsning](skisse-løsning-v19.png)

## Bygge og kjøre docker lokalt
Greit for å undersøke om containerne fungerer. Har lagt med noen variabler i `docker.list` denne er ikke 100% og bør
trolig settes fra feks `application-local.properties`, men det er nok til å sjekke at basisen fungerer.
```
mvn -B -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true  -DskipTests clean install

docker build -t fplos .

docker run -d --env-file=docker.list --name fplos fplos
```




## Kjøring lokalt

`yarn dev` i mappend `web/klient/` for å starte frontend


`no.nav.foreldrepenger.los.web.server.jetty.JettyDevServer` started i Intellij. Lokalt så går den mot Virtuell Tjenesteplattform. Denne må selvsagt kjøre på 
standard porter. Merk du trenger trolig sertifikater om applikasjonen bruker tjenester
på soap. Dette er pga WS-secure, etc.
