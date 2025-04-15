package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.server.abac.ForeldrepengerPipKlient;
import no.nav.foreldrepenger.los.server.abac.RutingKlient;
import no.nav.vedtak.felles.integrasjon.ruting.RutingResultat;

@Dependent
public class Beskyttelsesbehov {

    private final ForeldrepengerPipKlient pipKlient;
    private final RutingKlient rutingKlient;

    @Inject
    public Beskyttelsesbehov(ForeldrepengerPipKlient pipKlient, RutingKlient rutingKlient) {
        this.pipKlient = pipKlient;
        this.rutingKlient = rutingKlient;
    }

    public Set<AndreKriterierType> getBeskyttelsesKriterier(Oppgave oppgave) {
        var aktører = pipKlient.hentPipdataForSak(oppgave.getSaksnummer());
        var ruting = rutingKlient.finnRutingEgenskaper(aktører);
        var harKode6 = ruting.contains(RutingResultat.STRENGTFORTROLIG);
        var harKode7 = ruting.contains(RutingResultat.FORTROLIG);
        return harKode7 && !harKode6 ? Set.of(AndreKriterierType.KODE7_SAK) : Set.of();
    }
}
