package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@ApplicationScoped
public class FagsakApplikasjonTjeneste {

    private TpsTjeneste tpsTjeneste;
    private ForeldrepengerBehandlingRestKlient restKlient;


    private Predicate<String> predikatErFnr = søkestreng -> søkestreng.matches("\\d{11}");

    @Inject
    public FagsakApplikasjonTjeneste(TpsTjeneste tpsTjeneste,
                                     ForeldrepengerBehandlingRestKlient restKlient) {
        this.tpsTjeneste = tpsTjeneste;
        this.restKlient = restKlient;
    }

    FagsakApplikasjonTjeneste() {
        //CDI runner
    }

    public List<FagsakDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }
        return predikatErFnr.test(søkestreng)
                ? hentSakerForFnr(new PersonIdent(søkestreng))
                : hentFagsakForSaksnummer(new Saksnummer(søkestreng));
    }



    private List<FagsakDto> hentSakerForFnr(PersonIdent fnr) {
        try {
            return tpsTjeneste.hentBrukerForFnr(fnr)
                    .map(TpsPersonDto::getFnr)
                    .map(PersonIdent::getIdent)
                    .map(restKlient::getFagsakFraFnr)
                    .orElse(Collections.emptyList());
        } catch (IntegrasjonException e) {
            if (e.getMessage().contains("Finner ikke bruker med ident")) {
                // unødvendig feilmelding i frontend hvis vi ikke bytter ut denne med tom liste
                return Collections.emptyList();
            }
            throw e;
        }
    }

    private List<FagsakDto> hentFagsakForSaksnummer(Saksnummer saksnummer) {
        try {
            return restKlient.getFagsakFraSaksnummer(saksnummer.getVerdi());
        } catch (ManglerTilgangException e) {
            // fpsak returnerer 403 ved manglende tilgang og ingen resultat
            return Collections.emptyList();
        }
    }
}
