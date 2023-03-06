# FPLOS
===============

[![Bygg og deploy](https://github.com/navikt/fplos/actions/workflows/build.yml/badge.svg?branch=master)](https://github.com/navikt/fplos/actions/workflows/build.yml)
[![Promote](https://github.com/navikt/fplos/actions/workflows/promote.yml/badge.svg?branch=master)](https://github.com/navikt/fplos/actions/workflows/promote.yml)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=ncloc)](https://sonarcloud.io/summary/new_code?id=navikt_fplos)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=coverage)](https://sonarcloud.io/summary/new_code?id=navikt_fplos)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=alert_status)](https://sonarcloud.io/dashboard?id=navikt_fplos)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=bugs)](https://sonarcloud.io/dashboard?id=navikt_fplos)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=navikt_fplos)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=navikt_fplos)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=navikt_fplos)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=navikt_fplos&metric=sqale_index)](https://sonarcloud.io/dashboard?id=navikt_fplos)

FPLOS håndterer oppgave- og ledelsesstyring på foreldrepengeområdet. Fp-sak og fp-tilbake produserer hendelser som representerer tilstand i behandlingsprosessen. FPLOS lytter til hendelsene for å dekke behovet for oppgavestyring og statistikk. 

Oppgavestyrere definerer kriterier som ligger til grunn for køer som fordeler oppgaver etter prioritet til saksbehandlere. 

https://confluence.adeo.no/display/TVF/FP-LOS

## Skisse

![Skisse av løsning](docs/skisse-løsning-v19.png)

## Bygge og kjøre docker lokalt
Greit for å undersøke om containerne fungerer. Har lagt med noen variabler i `docker.list` denne er ikke 100% og bør
trolig settes fra feks `application-local.properties`, men det er nok til å sjekke at basisen fungerer.

```
mvn -B -Dfile.encoding=UTF-8 -DinstallAtEnd=true -DdeployAtEnd=true  -DskipTests clean install

docker build -t fplos .

docker run -d --env-file=docker.list --name fplos fplos
```

## Kjøring lokalt

`no.nav.foreldrepenger.los.web.server.jetty.JettyDevServer` started i Intellij. Lokalt så går den mot Virtuell Tjenesteplattform. Denne må selvsagt kjøre på 
standard porter. Merk du trenger trolig sertifikater om applikasjonen bruker tjenester
på soap. Dette er pga WS-secure, etc.

### Sikkerhet
Det er mulig å kalle tjenesten med bruk av følgende tokens
- Azure CC
- Azure OBO med følgende rettigheter:
    - fpsak-saksbehandler
    - fpsak-veileder
    - fpsak-oppgavestyrer
    - fpsak-drift
- STS (fases ut)
