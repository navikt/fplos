package no.nav.foreldrepenger.los.klient.fpsak.dto.behandling;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
                            LocalDate behandlingsfristTid,
                            @JsonProperty("behandlingArsaker") List<BehandlingÅrsakDto> behandlingÅrsaker,
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
