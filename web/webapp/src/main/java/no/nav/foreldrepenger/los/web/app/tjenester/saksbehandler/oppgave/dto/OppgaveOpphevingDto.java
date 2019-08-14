package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class OppgaveOpphevingDto implements AbacDto {

    @NotNull
    @Valid
    private OppgaveIdDto oppgaveId;

    @NotNull
    @Size(max = 1500)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String begrunnelse;

    public OppgaveOpphevingDto() {
    }

    public OppgaveOpphevingDto(OppgaveIdDto oppgaveId, String begrunnelse) {
        this.oppgaveId = oppgaveId;
        this.begrunnelse = begrunnelse;
    }

    public OppgaveIdDto getOppgaveId() {
        return oppgaveId;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    @Override
    public String toString() {
        return "FeatureToggleIdDto{" +
                "oppgaveId='" + oppgaveId + '\'' +
                "begrunnelse='" + begrunnelse + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
