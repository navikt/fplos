package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.jsonoppgave.JsonOppgave;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import static java.time.temporal.ChronoUnit.MILLIS;

@ApplicationScoped
public class JsonOppgaveHandler {

    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient;

    private final static String NFP_ENHET_DRAMMEN = "4806";

    public JsonOppgaveHandler(){
        //to make poroxyable
    }

    @Inject
    public JsonOppgaveHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider,
                                ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient){
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        this.foreldrepengerBehandlingRestKlient = foreldrepengerBehandlingRestKlient;
    }

    void prosesser(JsonOppgave jsonOppgave) {
        BehandlingType behandlingType = jsonOppgave.getBehandlingType() != null
                && jsonOppgave.getBehandlingType().equals(BehandlingType.INNSYN.getKode())
                ? BehandlingType.INNSYN
                : BehandlingType.FØRSTEGANGSSØKNAD;

        BehandlingFpsak behandlingFpsak = foreldrepengerBehandlingRestKlient.getBehandling(jsonOppgave.getBehandlingId());

        Oppgave.Builder builder = Oppgave.builder()
                .medBehandlingId(jsonOppgave.getBehandlingId())
                .medFagsakSaksnummer(jsonOppgave.getFagsakId())
                .medAktorId(3L)
                .medBehandlendeEnhet(NFP_ENHET_DRAMMEN)
                .medAktiv(jsonOppgave.getAktiv())
                .medBehandlingType(behandlingType)
                .medFagsakYtelseType(FagsakYtelseType.FORELDREPENGER)
                .medForsteStonadsdag(behandlingFpsak.getFørsteUttaksdag())
                .medBehandlingsfrist(jsonOppgave.getBehandlingsfrist().truncatedTo(MILLIS));

        oppgaveRepository.lagre(builder.build());
    }

}
