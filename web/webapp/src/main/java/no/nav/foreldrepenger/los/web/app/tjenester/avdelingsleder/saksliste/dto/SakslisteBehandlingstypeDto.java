package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.web.app.validering.ValidKodeverk;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class SakslisteBehandlingstypeDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @ValidKodeverk
    private BehandlingType behandlingType;

    private boolean checked;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteBehandlingstypeDto() {
    }

    public SakslisteBehandlingstypeDto(SakslisteIdDto sakslisteId, BehandlingType behandlingType, boolean checked, AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.behandlingType = behandlingType;
        this.checked = checked;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public BehandlingType getBehandlingType() {
        return behandlingType;
    }

    public boolean isChecked() {
        return checked;
    }

    public AvdelingEnhetDto getAvdelingEnhet(){
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "<id=" + sakslisteId + //$NON-NLS-1$
                ", behandlingType=" + behandlingType + //$NON-NLS-1$
                ", checked=" + checked + //$NON-NLS-1$
                ">";
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett().leggTilOppgavestyringEnhet(avdelingEnhet.getAvdelingEnhet());
    }
}
