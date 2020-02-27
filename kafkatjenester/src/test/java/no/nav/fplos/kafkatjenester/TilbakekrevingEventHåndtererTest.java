package no.nav.fplos.kafkatjenester;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.vedtak.felles.integrasjon.kafka.EventHendelse;
import no.nav.vedtak.felles.integrasjon.kafka.Fagsystem;
import no.nav.vedtak.felles.integrasjon.kafka.TilbakebetalingBehandlingProsessEventDto;

public class TilbakekrevingEventHåndtererTest {
    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private TilbakekrevingEventHåndterer handler = new TilbakekrevingEventHåndterer(oppgaveRepository, oppgaveEgenskapHandler);
    private static Long behandlingId = 1000000L;

    private Map<String, String> åpentAksjonspunkt = new HashMap<>() {{
        put("5015", "OPPR");
    }};
    private Map<String, String> manueltPåVentAksjonspunkt = new HashMap<>() {{
        put("5015", "OPPR");
        put("7002", "OPPR");
    }};
    private Map<String, String> åpentBeslutter = new HashMap<>() {{
        put("5005", "OPPR");
    }};
    private Map<String, String> avsluttetAksjonspunkt = new HashMap<>() {{ put("5015", "AVBR"); }};


    @Test
    public void skalOppretteOppgave() {
        TilbakebetalingBehandlingProsessEventDto event = eventFra(åpentAksjonspunkt);
        handler.håndterEvent(event);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(1);
    }

    @Test
    public void skalVidereføreOppgaveVedNyAktivEvent() {
        TilbakebetalingBehandlingProsessEventDto førsteEvent = eventFra(åpentAksjonspunkt);
        handler.håndterEvent(førsteEvent);

        var andreEvent = eventFra(åpentAksjonspunkt);
        handler.håndterEvent(andreEvent);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalAvslutteOppgaveVedAvsluttedeAksjonspunkt() {
        var førsteEvent = eventFra(åpentAksjonspunkt);
        var andreEvent = eventFra(avsluttetAksjonspunkt);
        handler.håndterEvent(førsteEvent);
        handler.håndterEvent(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalLukkeOppgaveVedÅpentManueltTilVentAksjonspunkt() {
        var førsteEvent = eventFra(åpentAksjonspunkt);
        var andreEvent = eventFra(manueltPåVentAksjonspunkt);
        handler.håndterEvent(førsteEvent);
        handler.håndterEvent(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalOppretteTilBeslutterEgenskapVedAksjonspunkt5005() {
        var førsteEvent = eventFra(åpentBeslutter);
        var andreEventUtenBeslutter = eventFra(åpentAksjonspunkt);
        handler.håndterEvent(førsteEvent);

        sjekkAktivOppgaveEksisterer(true);
        verifiserAktivBeslutterEgenskap();

        handler.håndterEvent(andreEventUtenBeslutter);
        verifiserInaktivBeslutterEgenskap();
    }

    @Test
    public void skalLukkeOppgaveVedReturFraTilBehandler() {
        var saksbehandler = eventFra(åpentAksjonspunkt);
        var tilBeslutter = eventFra(åpentBeslutter);
        handler.håndterEvent(saksbehandler);
        handler.håndterEvent(tilBeslutter);
        handler.håndterEvent(saksbehandler);

        List<OppgaveEventLogg> oppgaveEventer = repoRule.getRepository().hentAlle(OppgaveEventLogg.class).stream()
                .sorted(Comparator.comparing(OppgaveEventLogg::getOpprettetTidspunkt))
                .collect(Collectors.toList());

        verifiserOppgaveEvent(oppgaveEventer.get(0), OppgaveEventType.OPPRETTET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(1), OppgaveEventType.LUKKET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(2), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER);
        verifiserOppgaveEvent(oppgaveEventer.get(3), OppgaveEventType.LUKKET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(4), OppgaveEventType.OPPRETTET, null);
    }

    private void verifiserOppgaveEvent(OppgaveEventLogg event, OppgaveEventType type, AndreKriterierType kriterierType) {
        assertThat(event.getEventType()).isEqualTo(type);
        assertThat(event.getAndreKriterierType()).isEqualTo(kriterierType);
    }

    private void verifiserAktivBeslutterEgenskap() {
        sjekkBeslutterEgenskapMedAktivstatus(true);
    }

    private void verifiserInaktivBeslutterEgenskap() {
        sjekkBeslutterEgenskapMedAktivstatus(false);
    }

    private void sjekkBeslutterEgenskapMedAktivstatus(boolean status) {
        List<OppgaveEgenskap> egenskaper = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        assertThat(egenskaper.get(0).getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);
        assertThat(egenskaper.get(0).getAktiv()).isEqualTo(status);
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
