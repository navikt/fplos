package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.nøkkeltall.åpnebehandlinger.dto.NøkkeltallBehandlingVentestatusDto;

@ApplicationScoped
public class NøkkeltallBehandlingerVentestatus {

    private static final Logger LOG = LoggerFactory.getLogger(NøkkeltallBehandlingerVentestatus.class);

    private FpsakKlient fpsakRestKlient;
    private Map<String, List<NøkkeltallBehandlingVentestatusDto>> enhetStatistikkMap;
    private LocalDateTime nesteOppdateringEtter;

    public NøkkeltallBehandlingerVentestatus() {
    }

    @Inject
    public NøkkeltallBehandlingerVentestatus(FpsakKlient fpsakRestKlient) {
        this.fpsakRestKlient = fpsakRestKlient;
    }

    public List<NøkkeltallBehandlingVentestatusDto> hentBehandlingVentestatusNøkkeltall(String avdeling) {
        if (enhetStatistikkMap == null || LocalDateTime.now().isAfter(nesteOppdateringEtter)) {
            enhetStatistikkMap = fpsakRestKlient.hentBehandlingVentestatusNøkkeltall()
                    .stream()
                    .collect(Collectors.groupingBy(NøkkeltallBehandlingVentestatusDto::behandlendeEnhet,
                            Collectors.toUnmodifiableList()));
            nesteOppdateringEtter = LocalDateTime.now().plusMinutes(45);
            if (LOG.isInfoEnabled()) {
                LOG.info("Hentet statistikk fra fpsak, neste hentes etter {}. Antall unike uttaksmåneder per enhet: {}", nesteOppdateringEtter,
                        antallFørsteUttakMånederPerEnhet());
            }
        }
        var resultat = enhetStatistikkMap.get(avdeling);
        return resultat != null ? resultat : Collections.emptyList();
    }

    private String antallFørsteUttakMånederPerEnhet() {
        return enhetStatistikkMap.keySet().stream()
                .map(key -> key + "=" + enhetStatistikkMap.get(key).stream()
                        .map(NøkkeltallBehandlingVentestatusDto::førsteUttakMåned)
                        .distinct()
                        .count())
                .collect(Collectors.joining(", ", "{", "}"));
    }
}
