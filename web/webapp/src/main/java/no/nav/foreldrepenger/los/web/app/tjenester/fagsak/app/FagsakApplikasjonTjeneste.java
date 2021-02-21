package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.loslager.aktør.Person;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakMedPersonDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.PersonDto;
import no.nav.fplos.person.PersonTjeneste;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;

@ApplicationScoped
public class FagsakApplikasjonTjeneste {

    private static final Logger log = LoggerFactory.getLogger(FagsakApplikasjonTjeneste.class);
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
        try {
            var fagsaker = fagsakKlient.finnFagsaker(søkestreng);
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
                log.info("Fant ikke bruker", e);
                return Collections.emptyList();
            }
            throw e;
        }
    }

    private Person personDtoFra(List<FagsakDto> fagsaker) {
        return fagsaker.stream().findAny()
                .map(FagsakDto::getAktoerId)
                .flatMap(a -> personTjeneste.hentPerson(new AktørId(a)))
                .orElse(null);
    }

    private static FagsakMedPersonDto map(FagsakDto fagsakDto, Person person) {
        var personDto = new PersonDto(person);
        var sakstype = FagsakYtelseType.fraKode(fagsakDto.getSakstype().getKode());
        return new FagsakMedPersonDto(fagsakDto.getSaksnummer(), sakstype,
                fagsakDto.getStatus(), personDto, fagsakDto.getBarnFodt());
    }

}
