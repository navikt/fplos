package no.nav.foreldrepenger.los.web.app.tjenester.admin.dto;

import java.util.Objects;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

public record OppgaveKriterieTypeDto(@NotNull @Pattern(regexp = InputValideringRegex.FRITEKST) String oppgaveEgenskap) implements AbacDto {

    public OppgaveKriterieTypeDto {
        Objects.requireNonNull(oppgaveEgenskap, "oppgaveEgenskap");
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
