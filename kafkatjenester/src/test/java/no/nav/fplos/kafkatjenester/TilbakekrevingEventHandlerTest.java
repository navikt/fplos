package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class TilbakekrevingEventHandlerTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private TilbakekrevingEventHandler handler = new TilbakekrevingEventHandler(oppgaveRepository);
    private static Long behandlingId = 1000000L;

    private Map<String, String> åpentAksjonspunkt = new HashMap<>() {{
        put("5015", "OPPR");
    }};
    private Map<String, String> manueltPåVentAksjonspunkt = new HashMap<>() {{
        put("5015", "OPPR");
        put("7002", "OPPR");
    }};
    private Map<String, String> avsluttetAksjonspunkt = new HashMap<>() {{ put("5015", "AVBR"); }};


    @Test
    public void skalOppretteOppgave() {
        TilbakebetalingBehandlingProsessEventDto event = eventFra(åpentAksjonspunkt);
        handler.prosesser(event);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(1);
    }

    @Test
    public void skalVidereføreOppgaveVedNyAktivEvent() {
        TilbakebetalingBehandlingProsessEventDto førsteEvent = eventFra(åpentAksjonspunkt);
        handler.prosesser(førsteEvent);

        var andreEvent = eventFra(åpentAksjonspunkt);
        handler.prosesser(andreEvent);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalAvslutteOppgaveVedAvsluttedeAksjonspunkt() {
        var førsteEvent = eventFra(åpentAksjonspunkt);
        var andreEvent = eventFra(avsluttetAksjonspunkt);
        handler.prosesser(førsteEvent);
        handler.prosesser(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalLukkeOppgaveVedÅpentManueltTilVentAksjonspunkt() {
        var førsteEvent = eventFra(åpentAksjonspunkt);
        var andreEvent = eventFra(manueltPåVentAksjonspunkt);
        handler.prosesser(førsteEvent);
        handler.prosesser(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    private void sjekkAntallOppgaver(int antall) {
        assertThat(repoRule.getRepository().hentAlle(Oppgave.class)).hasSize(antall);
    }

    private void sjekkAktivOppgaveEksisterer(boolean aktiv) {
        List<Oppgave> oppgave = repoRule.getRepository().hentAlle(Oppgave.class);
        assertThat(oppgave.get(0).getAktiv()).isEqualTo(aktiv);
        int antallAktive = (int) oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(aktiv ? 1 : 0);
    }

    private void sjekkOppgaveEventAntallEr(int antall) {
        var eventer = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        assertThat(eventer).hasSize(antall);
    }

    private static TilbakebetalingBehandlingProsessEventDto eventFra(Map<String, String> aksjonspunktmap) {
        return basisEventFra(aksjonspunktmap)
                .medFeilutbetaltBeløp(BigDecimal.valueOf(500))
                .medFørsteFeilutbetaling(LocalDate.now())
                .build();
    }

    private static TilbakebetalingBehandlingProsessEventDto.Builder basisEventFra(Map<String, String> aksjonspunktmap) {
        return TilbakebetalingBehandlingProsessEventDto.builder()
                .medFagsystem(Fagsystem.FPSAK)
                .medEksternId(UUID.nameUUIDFromBytes(behandlingId.toString().getBytes()))
                .medSaksnummer("135701264")
                .medBehandlendeEnhet("0300")
                .medAktørId("9000000030703")
                .medEventTid(LocalDateTime.now())
                .medEventHendelse(EventHendelse.AKSJONSPUNKT_OPPRETTET)
                .medBehandlingStatus("STATUS")
                .medBehandlingSteg("STEG")
                .medYtelseTypeKode(FagsakYtelseType.FORELDREPENGER.getKode())
                .medBehandlingTypeKode(BehandlingType.FØRSTEGANGSSØKNAD.getKode())
                .medOpprettetBehandling(LocalDateTime.now())
                .medAksjonspunktKoderMedStatusListe(aksjonspunktmap);
    }
}
