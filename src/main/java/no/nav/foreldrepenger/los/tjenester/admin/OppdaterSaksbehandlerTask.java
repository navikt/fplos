package no.nav.foreldrepenger.los.tjenester.admin;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.los.organisasjon.OrganisasjonRepository;
import no.nav.foreldrepenger.los.organisasjon.ansatt.RefreshAnsattInfoKlient;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.oppdater.saksbehandler", maxFailedRuns = 1)
public class OppdaterSaksbehandlerTask implements ProsessTaskHandler {

    public static final String IDENT = "ident";

    private static final Logger LOG = LoggerFactory.getLogger(OppdaterSaksbehandlerTask.class);
    private final OrganisasjonRepository organisasjonsRepository;
    private final RefreshAnsattInfoKlient refreshAnsattInfoKlient;


    @Inject
    public OppdaterSaksbehandlerTask(OrganisasjonRepository organisasjonRepository, RefreshAnsattInfoKlient refreshAnsattInfoKlient) {
        this.organisasjonsRepository = organisasjonRepository;
        this.refreshAnsattInfoKlient = refreshAnsattInfoKlient;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var saksbehandler = Optional.ofNullable(prosessTaskData.getPropertyValue(IDENT))
            .flatMap(organisasjonsRepository::hentSaksbehandlerHvisEksisterer)
            .orElseThrow();

        var oppdatering = refreshAnsattInfoKlient.refreshAnsattInfoForIdent(saksbehandler.getSaksbehandlerIdent());
        oppdatering.ifPresent(bp -> {
            saksbehandler.setNavn(bp.navn());
            saksbehandler.setAnsattVedEnhet(bp.ansattAvdeling());
            saksbehandler.setSaksbehandlerUuid(bp.uid());
            organisasjonsRepository.persistFlush(saksbehandler);
        });
        if (oppdatering.isPresent()) {
            LOG.info("Oppdaterte saksbehandler {} med ny info fra FPTilgang.", saksbehandler.getSaksbehandlerIdent());
        } else {
            LOG.warn("Finner ikke saksbehandler {} i FPTilgang ved oppdatering.", saksbehandler.getSaksbehandlerIdent());
        }
    }
}
