package no.nav.foreldrepenger.los.statistikk;


import java.io.Serializable;

import jakarta.persistence.Embeddable;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;

@Embeddable
public record EnhetYtelseBehandlingType(String behandlendeEnhet, Long tidsstempel, FagsakYtelseType fagsakYtelseType, BehandlingType behandlingType) implements Serializable {

}
