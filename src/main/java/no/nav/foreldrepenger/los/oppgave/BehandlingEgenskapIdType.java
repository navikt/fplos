package no.nav.foreldrepenger.los.oppgave;


import java.io.Serializable;
import java.util.UUID;

import jakarta.persistence.Embeddable;

@Embeddable
public record BehandlingEgenskapIdType(UUID behandlingId, AndreKriterierType andreKriterierType) implements Serializable {

}
