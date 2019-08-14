package no.nav.foreldrepenger.loslager.aktør;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;

public class TpsPersonDto {
    private AktørId aktørId;
    private String navn;
    private PersonIdent fnr;
    private LocalDate fødselsdato;
    private String kjønn;
    private LocalDate dødsdato;
    private String diskresjonskode;

    private TpsPersonDto() {
    }

    public AktørId getAktørId() {
        return aktørId;
    }

    /** NB: Denne skal ikke lagres. Brukes kun for utveksling til/fra grensesnitt. */
    public PersonIdent getFnr() {
        return fnr;
    }
    
    public PersonIdent getPersonIdent() {
        return fnr;
    }

    public String getNavn() {
        return navn;
    }

    public LocalDate getFødselsdato() {
        return fødselsdato;
    }

    public int getAlder() {
        return (int) ChronoUnit.YEARS.between(fødselsdato, LocalDate.now());
    }

    public boolean erKvinne() {
        return kjønn.equals(NavBrukerKjønn.K.name());
    }

    public LocalDate getDødsdato() {
        return dødsdato;
    }

    public String getDiskresjonskode() {
        return diskresjonskode;
    }

    public static class Builder {
        private TpsPersonDto personinfoMal;

        public Builder() {
            personinfoMal = new TpsPersonDto();
        }

        public Builder medAktørId(AktørId aktørId) {
            personinfoMal.aktørId = aktørId;
            return this;
        }

        public Builder medNavn(String navn) {
            personinfoMal.navn = navn;
            return this;
        }

        public Builder medFnr(PersonIdent fnr) {
            personinfoMal.fnr = fnr;
            return this;
        }

        public Builder medFødselsdato(LocalDate fødselsdato) {
            personinfoMal.fødselsdato = fødselsdato;
            return this;
        }

        public Builder medNavBrukerKjønn(String kjønn) {
            personinfoMal.kjønn = kjønn;
            return this;
        }

        public Builder medDødsdato(LocalDate dødsdato) {
            personinfoMal.dødsdato = dødsdato;
            return this;
        }

        public Builder medDiskresjonsKode(String diskresjonsKode) {
            personinfoMal.diskresjonskode = diskresjonsKode;
            return this;
        }

        public TpsPersonDto build() {
            requireNonNull(personinfoMal.aktørId, "Navbruker må ha aktørId"); //$NON-NLS-1$
            requireNonNull(personinfoMal.fnr, "Navbruker må ha fødselsnummer"); //$NON-NLS-1$
            requireNonNull(personinfoMal.navn, "Navbruker må ha navn"); //$NON-NLS-1$
            requireNonNull(personinfoMal.fødselsdato, "Navbruker må ha fødselsdato"); //$NON-NLS-1$
            requireNonNull(personinfoMal.kjønn, "Navbruker må ha kjønn"); //$NON-NLS-1$
            return personinfoMal;
        }

    }
}
