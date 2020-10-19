package no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app;

import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerFagsakKlient;
import no.nav.fplos.foreldrepengerbehandling.dto.fagsak.FagsakDto;
import no.nav.vedtak.exception.IntegrasjonException;
import no.nav.vedtak.exception.ManglerTilgangException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

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

    public List<FagsakDto> hentSaker(String søkestreng) {
        if (!søkestreng.matches("\\d+")) {
            return Collections.emptyList();
        }
        try {
            return fagsakKlient.finnFagsaker(søkestreng);
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

}
