package no.nav.fplos.person;

import no.nav.foreldrepenger.loslager.aktør.GeografiskTilknytning;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Aktoer;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Bruker;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Diskresjonskoder;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Doedsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.Foedselsdato;
import no.nav.tjeneste.virksomhet.person.v3.informasjon.PersonIdent;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.GregorianCalendar;

@ApplicationScoped
public class TpsOversetter {

    TpsOversetter() {
        // for CDI proxy
    }

    public TpsPersonDto tilBrukerInfo(AktørId aktørId, Bruker person) {
        String navn = person.getPersonnavn().getSammensattNavn();

        LocalDate fødselsdato = null;
        Foedselsdato fødselsdatoJaxb = person.getFoedselsdato();
        if (fødselsdatoJaxb != null) {
            GregorianCalendar gregCal = fødselsdatoJaxb.getFoedselsdato().toGregorianCalendar();
            fødselsdato = gregCal.toZonedDateTime().toLocalDate();
        }
        LocalDate dødsdato = null;
        Doedsdato dødsdatoJaxb = person.getDoedsdato();
        if (dødsdatoJaxb != null) {
            GregorianCalendar gregCal = dødsdatoJaxb.getDoedsdato().toGregorianCalendar();
            dødsdato = gregCal.toZonedDateTime().toLocalDate();
        }
        Aktoer aktoer = person.getAktoer();
        PersonIdent pi = (PersonIdent) aktoer;
        String ident = pi.getIdent().getIdent();
        String kjønn = person.getKjoenn().getKjoenn().getValue();

        String diskresjonskode = person.getDiskresjonskode() == null ? null : person.getDiskresjonskode().getValue();

        return new TpsPersonDto.Builder()
            .medAktørId(aktørId)
            .medFnr(no.nav.foreldrepenger.domene.typer.PersonIdent.fra(ident))
            .medNavn(navn)
            .medFødselsdato(fødselsdato)
            .medDødsdato(dødsdato)
            .medNavBrukerKjønn(kjønn)
            .medDiskresjonsKode(diskresjonskode)
            .build();
    }

    GeografiskTilknytning tilGeografiskTilknytning(no.nav.tjeneste.virksomhet.person.v3.informasjon.GeografiskTilknytning geografiskTilknytning, Diskresjonskoder diskresjonskoder) {
        String geoTilkn = geografiskTilknytning != null ? geografiskTilknytning.getGeografiskTilknytning() : null;
        String diskKode = diskresjonskoder != null ? diskresjonskoder.getValue() : null;
        return new GeografiskTilknytning(geoTilkn, diskKode);
    }
}
