package no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.fpsakklient.FpsakKlient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingFørsteUttakDto;
import no.nav.foreldrepenger.los.tjenester.avdelingsleder.nøkkeltall.dto.NøkkeltallBehandlingVentefristUtløperDto;

@ApplicationScoped
public class NøkkeltallBehandlingerVentestatus {

    private static final Logger LOG = LoggerFactory.getLogger(NøkkeltallBehandlingerVentestatus.class);

    private FpsakKlient fpsakRestKlient;
    private Map<String, List<NøkkeltallBehandlingFørsteUttakDto>> enhetStatistikkMapUttaksdato;
    private Map<String, List<NøkkeltallBehandlingVentefristUtløperDto>> enhetStatistikkMapVentefrist;
    private LocalDateTime nesteFørsteUttakOppdateringEtter;
    private LocalDateTime nesteVentefristOppdateringEtter;

    public NøkkeltallBehandlingerVentestatus() {
    }

    @Inject
    public NøkkeltallBehandlingerVentestatus(FpsakKlient fpsakRestKlient) {
        this.fpsakRestKlient = fpsakRestKlient;
    }

    public List<NøkkeltallBehandlingFørsteUttakDto> hentBehandlingVentestatusNøkkeltall(String avdeling) {
        if (enhetStatistikkMapUttaksdato == null || LocalDateTime.now().isAfter(nesteFørsteUttakOppdateringEtter)) {
            enhetStatistikkMapUttaksdato = fpsakRestKlient.hentBehandlingFørsteUttakNøkkeltall()
                .stream()
                .collect(Collectors.groupingBy(NøkkeltallBehandlingFørsteUttakDto::behandlendeEnhet, Collectors.toUnmodifiableList()));
            nesteFørsteUttakOppdateringEtter = LocalDateTime.now().plusMinutes(45);
            if (LOG.isInfoEnabled()) {
                LOG.info("Hentet statistikk fra fpsak, neste hentes etter {}. Antall unike uttaksmåneder per enhet: {}",
                    nesteFørsteUttakOppdateringEtter,
                    antallFørsteUttakMånederPerEnhet());
            }
        }
        var resultat = enhetStatistikkMapUttaksdato.get(avdeling);
        return resultat != null ? resultat : Collections.emptyList();
    }

    public List<NøkkeltallBehandlingVentefristUtløperDto> hentVentefristNøkkeltall(String avdeling) {
        if (enhetStatistikkMapVentefrist == null || LocalDateTime.now().isAfter(nesteVentefristOppdateringEtter)) {
            enhetStatistikkMapVentefrist = fpsakRestKlient.hentVentefristerNøkkeltall()
                .stream()
                .collect(Collectors.groupingBy(NøkkeltallBehandlingVentefristUtløperDto::behandlendeEnhet, Collectors.toUnmodifiableList()));
            nesteVentefristOppdateringEtter = LocalDateTime.now().plusMinutes(30);
            if (LOG.isInfoEnabled()) {
                LOG.info("Hentet friststatistikk fra fpsak, neste hentes etter {}.", nesteVentefristOppdateringEtter);
            }
        }
        var resultat = enhetStatistikkMapVentefrist.get(avdeling);
        return resultat != null ? resultat : Collections.emptyList();
    }

    private String antallFørsteUttakMånederPerEnhet() {
        return enhetStatistikkMapUttaksdato.keySet()
            .stream()
            .map(key -> key + "=" + enhetStatistikkMapUttaksdato.get(key).stream().map(NøkkeltallBehandlingFørsteUttakDto::førsteUttakMåned).distinct().count())
            .collect(Collectors.joining(", ", "{", "}"));
    }
}
