package no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.FplosAbacAttributtType;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.felles.util.validering.ValidKodeliste;
import no.nav.vedtak.sikkerhet.abac.AbacDataAttributter;
import no.nav.vedtak.sikkerhet.abac.AbacDto;

public class SakslisteAndreKriterierDto implements AbacDto {

    @NotNull
    @Valid
    private SakslisteIdDto sakslisteId;

    @NotNull
    @ValidKodeliste
    private AndreKriterierType andreKriterierType;

    private boolean checked;

    private boolean inkluder = true;

    @NotNull
    @Valid
    private AvdelingEnhetDto avdelingEnhet;

    public SakslisteAndreKriterierDto() {
    }

    public SakslisteAndreKriterierDto(SakslisteIdDto sakslisteId,
                                      AndreKriterierType andreKriterierType,
                                      boolean checked,
                                      boolean inkluder,
                                      AvdelingEnhetDto avdelingEnhet) {
        this.sakslisteId = sakslisteId;
        this.andreKriterierType = andreKriterierType;
        this.checked = checked;
        this.inkluder = inkluder;
        this.avdelingEnhet = avdelingEnhet;
    }

    public Long getSakslisteId() {
        return sakslisteId.getVerdi();
    }

    public AndreKriterierType getAndreKriterierType() {
        return andreKriterierType;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean isInkluder() {
        return inkluder;
    }

    public AvdelingEnhetDto getAvdelingEnhet() {
        return avdelingEnhet;
    }

    @Override
    public String toString() {
        return "<id=" + sakslisteId + //$NON-NLS-1$
                ", andreKriterierType=" + andreKriterierType + //$NON-NLS-1$
                ", checked=" + checked + //$NON-NLS-1$
                ">";
    }

    @Override
    public AbacDataAttributter abacAttributter() {
        return AbacDataAttributter.opprett()
                .leggTil(FplosAbacAttributtType.OPPGAVESTYRING_ENHET, avdelingEnhet.getAvdelingEnhet());

    }
}
