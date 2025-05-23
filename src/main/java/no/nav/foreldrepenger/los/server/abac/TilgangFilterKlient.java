package no.nav.foreldrepenger.los.server.abac;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import jakarta.enterprise.context.Dependent;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.vedtak.felles.integrasjon.rest.FpApplication;
import no.nav.vedtak.felles.integrasjon.rest.RestClientConfig;
import no.nav.vedtak.felles.integrasjon.rest.TokenFlow;
import no.nav.vedtak.felles.integrasjon.tilgangfilter.AbstractTilgangFilterKlient;

@Dependent
@RestClientConfig(tokenConfig = TokenFlow.AZUREAD_CC, application = FpApplication.FPTILGANG)
public class TilgangFilterKlient extends AbstractTilgangFilterKlient {

    protected TilgangFilterKlient() {
        super();
    }

    public Set<Saksnummer> tilgangFilterSaker(UUID ansattOid, List<Oppgave> oppgaver) {
        var saksnummer = oppgaver.stream().map(Oppgave::getSaksnummer).map(Saksnummer::getVerdi).collect(Collectors.toSet());
        var resultatHarTilgang = super.filterSaksnummer(ansattOid, saksnummer);
        return saksnummer.stream()
            .filter(resultatHarTilgang::contains)
            .map(Saksnummer::new)
            .collect(Collectors.toSet());
    }

}
