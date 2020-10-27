package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.behandling.ResourceLink;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.AktoerInfoDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakMedPersonDto;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.PersonDto;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@ApplicationScoped
public class FagsakApplikasjonTjeneste {

    private static final Logger log = LoggerFactory.getLogger(FagsakApplikasjonTjeneste.class);
    private ForeldrepengerFagsakKlient fagsakKlient;


    @Inject
    public FagsakApplikasjonTjeneste(ForeldrepengerFagsakKlient fagsakKlient) {
        this.fagsakKlient = fagsakKlient;
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
            var personDto = fagsaker.stream().findAny()
                    .map(FagsakDto::getLinks)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(rl -> rl.getRel().equals("sak-aktoer-person"))
                    .map(ResourceLink::getHref)
                    .peek(h -> log.info(h.getQuery()))
                    .map(fagsakKlient::hentAktoerInfo)
                    .map(AktoerInfoDto::getPerson)
                    .findFirst().orElse(null);
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

    private static FagsakMedPersonDto map(FagsakDto fagsakDto, PersonDto personDto) {
        return new FagsakMedPersonDto(fagsakDto.getSaksnummer(), fagsakDto.getSakstype(),
                fagsakDto.getStatus(), personDto, fagsakDto.getBarnFodt());
    }

}
