package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.los.klient.fpsak.dto.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

public record BehandlingDto(UUID uuid,
                            BehandlingType type,
                            BehandlingStatus status,
                            LocalDateTime opprettet,
                            String behandlendeEnhetId,
                            LocalDate behandlingsfristTid,
                            List<AksjonspunktDto> aksjonspunktene,
                            List<BehandlingÅrsakDto> behandlingÅrsaker,
                            List<ResourceLink> links,
                            String ansvarligSaksbehandler) {

    @Override
    public List<ResourceLink> links() {
        return nullsafe(links);
    }

    @Override
    public List<BehandlingÅrsakDto> behandlingÅrsaker() {
        return nullsafe(behandlingÅrsaker);
    }

    private static <T> List<T> nullsafe(List<T> links) {
        return Collections.unmodifiableList(Optional.ofNullable(links).orElse(List.of()));
    }
}
