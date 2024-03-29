package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.hendelse.behandlinghendelse.AktørPipKlient;
import no.nav.foreldrepenger.los.persontjeneste.PersonTjeneste;
import no.nav.foreldrepenger.los.oppgave.Oppgave;

@Dependent
public class Beskyttelsesbehov {

    private final AktørPipKlient pipKlient;
    private final PersonTjeneste personTjeneste;

    @Inject
    public Beskyttelsesbehov(AktørPipKlient pipKlient, PersonTjeneste personTjeneste) {
        this.pipKlient = pipKlient;
        this.personTjeneste = personTjeneste;
    }

    public Set<AndreKriterierType> getBeskyttelsesKriterier(Oppgave oppgave) {
        var aktører = pipKlient.hentAktørIderSomString(new Saksnummer(String.valueOf(oppgave.getFagsakSaksnummer())));
        return personTjeneste.harNoenKode7MenIngenHarKode6(oppgave.getFagsakYtelseType(), aktører) ? Set.of(AndreKriterierType.KODE7_SAK) : Set.of();
    }
}
