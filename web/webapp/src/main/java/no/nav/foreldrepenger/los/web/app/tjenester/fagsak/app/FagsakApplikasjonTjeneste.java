package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.person.PersonTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static no.nav.foreldrepenger.loslager.aktør.Fødselsnummer.erFødselsnummer;

@ApplicationScoped
public class FagsakApplikasjonTjeneste {

    private PersonTjeneste personTjeneste;
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingKlient;


    @Inject
    public FagsakApplikasjonTjeneste(PersonTjeneste personTjeneste,
                                     ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingKlient) {
        this.personTjeneste = personTjeneste;
        this.foreldrepengerBehandlingKlient = foreldrepengerBehandlingKlient;
    }

    FagsakApplikasjonTjeneste() {
        //CDI runner
    }

    public List<FagsakDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }
        return erFødselsnummer(søkestreng)
                ? hentSakerForFnr(new Fødselsnummer(søkestreng))
                : hentFagsakForSaksnummer(new Saksnummer(søkestreng));
    }

    private List<FagsakDto> hentSakerForFnr(Fødselsnummer fnr) {
        try {
            return personTjeneste.hentPerson(fnr)
                    .map(Person::getFødselsnummer)
                    .map(foreldrepengerBehandlingKlient::getFagsakFraFnr)
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
            return foreldrepengerBehandlingKlient.getFagsakFraSaksnummer(saksnummer.getVerdi());
        } catch (ManglerTilgangException e) {
            // fpsak returnerer 403 ved manglende tilgang og ingen resultat
            return Collections.emptyList();
        }
    }
}
