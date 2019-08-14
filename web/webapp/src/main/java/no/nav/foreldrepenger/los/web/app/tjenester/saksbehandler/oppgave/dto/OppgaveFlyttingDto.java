package no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;
import no.nav.vedtak.util.InputValideringRegex;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class OppgaveFlyttingDto implements AbacDto {

    @NotNull
    @Valid
    private OppgaveIdDto oppgaveId;


    @NotNull
    @Valid
    private SaksbehandlerBrukerIdentDto brukerIdent;

    @NotNull
    @Size(max = 1500)
    @Pattern(regexp = InputValideringRegex.FRITEKST)
    private String begrunnelse;

    public OppgaveFlyttingDto() {
    }

    public OppgaveFlyttingDto(OppgaveIdDto oppgaveId, SaksbehandlerBrukerIdentDto brukerIdent, String begrunnelse) {
        this.oppgaveId = oppgaveId;
        this.brukerIdent = brukerIdent;
        this.begrunnelse = begrunnelse;
    }

    public OppgaveIdDto getOppgaveId() {
        return oppgaveId;
    }

    public SaksbehandlerBrukerIdentDto getBrukerIdent() {
        return brukerIdent;
    }

    public String getBegrunnelse() {
        return begrunnelse;
    }

    @Override
    public String toString() {
        return "FeatureToggleIdDto{" +
                "oppgaveId='" + oppgaveId + '\'' +
                "brukerident='" + brukerIdent + '\'' +
                "begrunnelse='" + begrunnelse + '\'' +
                '}';
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett();
    }
}
