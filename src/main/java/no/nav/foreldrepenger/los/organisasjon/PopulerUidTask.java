package no.nav.foreldrepenger.los.organisasjon;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;

@Dependent
@ProsessTask(value = "vedlikehold.populerUid", maxFailedRuns = 1)
public class PopulerUidTask implements ProsessTaskHandler {

    public static final String SBH_IDENT_KEY = "navident";

    private static final Logger LOG = LoggerFactory.getLogger(PopulerUidTask.class);
    private final OrganisasjonRepository organisasjonsRepository;
    private final AnsattTjeneste ansattTjeneste;

    private final EntityManager entityManager;

    @Inject
    public PopulerUidTask(OrganisasjonRepository organisasjonRepository, AnsattTjeneste ansattTjeneste, EntityManager entityManager) {
        this.organisasjonsRepository = organisasjonRepository;
        this.ansattTjeneste = ansattTjeneste;
        this.entityManager = entityManager;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        var ident = prosessTaskData.getPropertyValue(SBH_IDENT_KEY);
        var uid = ansattTjeneste.hentBrukerProfil(ident).uid();

        var saksbehandler = organisasjonsRepository.hentSaksbehandler(ident);
        if (!Objects.equals(uid, saksbehandler.getSaksbehandlerUuid())) {
            saksbehandler.setSaksbehandlerUuid(uid);
            entityManager.persist(saksbehandler);
            entityManager.flush();
        }
    }
}
