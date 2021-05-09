package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.klient.fpsak.ForeldrepengerFagsakKlient;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.FagsakMedPersonDto;
import no.nav.foreldrepenger.los.klient.fpsak.dto.fagsak.PersonDto;
import no.nav.foreldrepenger.los.klient.person.PersonTjeneste;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;

@ApplicationScoped
public class FagsakApplikasjonTjeneste {

    private static final Logger LOG = LoggerFactory.getLogger(FagsakApplikasjonTjeneste.class);
    private ForeldrepengerFagsakKlient fagsakKlient;
    private PersonTjeneste personTjeneste;


    @Inject
    public FagsakApplikasjonTjeneste(ForeldrepengerFagsakKlient fagsakKlient, PersonTjeneste personTjeneste) {
        this.fagsakKlient = fagsakKlient;
        this.personTjeneste = personTjeneste;
    }

    FagsakApplikasjonTjeneste() {
        //CDI runner
    }

    public List<FagsakMedPersonDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }
        var brukSøkeStreng = søkestreng;
        if (søkestreng.length() == 11) {
            brukSøkeStreng = personTjeneste.hentAktørIdForPersonIdent(søkestreng).map(AktørId::getId).orElse(søkestreng);
        }
        try {
            var fagsaker = fagsakKlient.finnFagsaker(brukSøkeStreng);
            if (fagsaker.isEmpty()) {
                return Collections.emptyList();
            }
            var personDto = personDtoFra(fagsaker);
            return fagsaker.stream().map(fs -> map(fs, personDto)).collect(toList());
        } catch (ManglerTilgangException e) {
            // fpsak gir 403 både ved manglende tilgang og sak-ikke-funnet
            return Collections.emptyList();
        } catch (IntegrasjonException e) {
            if (e.getMessage().contains("Finner ikke bruker med ident")) {
                // fant ikke bruker.
                LOG.info("Fant ikke bruker", e);
                return Collections.emptyList();
            }
            throw e;
        }
    }

    private Person personDtoFra(List<FagsakDto> fagsaker) {
        return fagsaker.stream().findAny()
                .flatMap(f -> personTjeneste.hentPerson(new AktørId(f.aktørId()), f.saksnummer()))
                .orElse(null);
    }

    private static FagsakMedPersonDto map(FagsakDto fagsakDto, Person person) {
        var personDto = new PersonDto(person);
        return new FagsakMedPersonDto(fagsakDto.saksnummer(),
                FagsakYtelseType.fraKode(fagsakDto.fagsakYtelseType().getKode()),
                fagsakDto.status(), personDto, fagsakDto.barnFødt());
    }

}
