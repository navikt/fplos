package no.nav.foreldrepenger.los.risikovurdering;

import no.nav.foreldrepenger.los.risikovurdering.json.KontrollSerialiseringUtil;
import no.nav.foreldrepenger.los.risikovurdering.json.KontrollresultatMapper;
import no.nav.foreldrepenger.los.risikovurdering.modell.KontrollresultatWrapper;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskData;
import no.nav.vedtak.felles.prosesstask.api.ProsessTaskHandler;
import no.nav.vedtak.kontroll.v1.KontrollResultatV1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

@ProsessTask(LesKontrollresultatTask.TASKTYPE)
public class LesKontrollresultatTask implements ProsessTaskHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(LesKontrollresultatTask.class);

    public static final String TASKTYPE = "risiko.klassifisering.resultat";

    private RisikovurderingTjeneste risikovurderingTjeneste;

    public LesKontrollresultatTask() {
    }

    @Inject
    public LesKontrollresultatTask(RisikovurderingTjeneste risikovurderingTjeneste) {
        this.risikovurderingTjeneste = risikovurderingTjeneste;
    }

    @Override
    public void doTask(ProsessTaskData prosessTaskData) {
        String payload = prosessTaskData.getPayloadAsString();
        try {
            KontrollResultatV1 kontraktResultat = KontrollSerialiseringUtil.deserialiser(payload, KontrollResultatV1.class);
            evaluerKontrollresultat(kontraktResultat);
        } catch (Exception e) {
            LOGGER.warn("Klarte ikke behandle risikoklassifiseringresultat", e);
        }
    }

    private void evaluerKontrollresultat(KontrollResultatV1 kontraktResultat) {
        KontrollresultatWrapper resultatWrapper = KontrollresultatMapper.fraKontrakt(kontraktResultat);
        risikovurderingTjeneste.lagreKontrollresultat(resultatWrapper);
    }

}
