package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record BehandlingDto(Long id,
                            UUID uuid,
                            Long versjon,
                            BehandlingType type,
                            BehandlingStatus status,
                            Long fagsakId,
                            LocalDateTime opprettet,
                            LocalDateTime avsluttet,
                            LocalDateTime endret,
                            String endretAvBrukernavn,
                            String behandlendeEnhetId,
                            String behandlendeEnhetNavn,
                            boolean erAktivPapirsoknad,
                            boolean erAktivPapirsøknad,
                            LocalDate behandlingsfristTid,
                            List<BehandlingÅrsakDto> behandlingÅrsaker,
                            List<BehandlingÅrsakDto> behandlingArsaker,
                            List<ResourceLink> links,
                            String ansvarligSaksbehandler) {

    @Override
    public List<ResourceLink> links() {
        return nullsafe(links);
    }

    @Override
    public List<BehandlingÅrsakDto> behandlingÅrsaker() {
        if (behandlingArsaker == null) {
            return nullsafe(behandlingÅrsaker);
        }
        return behandlingArsaker;
    }

    private static <T> List<T> nullsafe(List<T> links) {
        return Collections.unmodifiableList(Optional.ofNullable(links).orElse(List.of()));
    }
}
