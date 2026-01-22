package no.nav.foreldrepenger.los.beskyttelsesbehov;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.server.abac.RutingKlient;
import no.nav.vedtak.felles.integrasjon.ruting.RutingResultat;

@Dependent
public class Beskyttelsesbehov {

    private final RutingKlient rutingKlient;

    @Inject
    public Beskyttelsesbehov(RutingKlient rutingKlient) {
        this.rutingKlient = rutingKlient;
    }

    public Set<AndreKriterierType> getBeskyttelsesKriterier(Saksnummer saksnummer) {
        var ruting = rutingKlient.finnRutingEgenskaper(saksnummer.getVerdi());
        var harKode6 = ruting.contains(RutingResultat.STRENGTFORTROLIG);
        var harKode7 = ruting.contains(RutingResultat.FORTROLIG);
        return harKode7 && !harKode6 ? Set.of(AndreKriterierType.KODE7_SAK) : Set.of();
    }
}
