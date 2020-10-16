package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.foreldrepenger.domene.typer.Saksnummer;
import no.nav.foreldrepenger.loslager.aktør.Fødselsnummer;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
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
    private ForeldrepengerFagsakKlient fagsakKlient;


    @Inject
    public FagsakApplikasjonTjeneste(PersonTjeneste personTjeneste,
                                     ForeldrepengerFagsakKlient fagsakKlient) {
        this.personTjeneste = personTjeneste;
        this.fagsakKlient = fagsakKlient;
    }

    FagsakApplikasjonTjeneste() {
        //CDI runner
    }

    public List<FagsakDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }
        try {
            return erFødselsnummer(søkestreng)
                    ? hentSakerForFnr(new Fødselsnummer(søkestreng))
                    : hentFagsakForSaksnummer(new Saksnummer(søkestreng));
        } catch (ManglerTilgangException e) {
            // fpsak gir 403 både ved manglende tilgang og sak-ikke-funnet
            return Collections.emptyList();
        }
    }

    private List<FagsakDto> hentSakerForFnr(Fødselsnummer fnr) {
        try {
            return personTjeneste.hentPerson(fnr)
                    .map(Person::getFødselsnummer)
                    .map(fagsakKlient::getFagsakFraFnr)
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
        return fagsakKlient.getFagsakFraSaksnummer(saksnummer.getVerdi());
    }
}
