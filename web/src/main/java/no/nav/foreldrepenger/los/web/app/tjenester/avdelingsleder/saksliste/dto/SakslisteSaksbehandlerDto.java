package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakslisteSaksbehandlerDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @Valid
    private SaksbehandlerBrukerIdentDto brukerIdent;

    private boolean checked;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteSaksbehandlerDto() {
    }

    public SakslisteSaksbehandlerDto(SakslisteIdDto sakslisteId,
                                     SaksbehandlerBrukerIdentDto brukerIdent,
                                     boolean checked,
                                     AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.brukerIdent = brukerIdent;
        this.checked = checked;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public SaksbehandlerBrukerIdentDto getBrukerIdent() {
        return brukerIdent;
    }

    public boolean isChecked() {
        return checked;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "<id=" + sakslisteId + //$NON-NLS-1$
                ", brukerIdent=" + brukerIdent + //$NON-NLS-1$
                ", checked=" + checked + //$NON-NLS-1$
                ">";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof SakslisteSaksbehandlerDto)) {
            return false;
        }

        var sakslisteBehandlingstypeDto = (SakslisteSaksbehandlerDto) o;
        return sakslisteId.getVerdi().equals(sakslisteBehandlingstypeDto.sakslisteId.getVerdi())
                && brukerIdent.getVerdi().equals(sakslisteBehandlingstypeDto.brukerIdent.getVerdi());
    }

    @Override
    public int hashCode() {
        return 31 * sakslisteId.hashCode();
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett()
                .leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
