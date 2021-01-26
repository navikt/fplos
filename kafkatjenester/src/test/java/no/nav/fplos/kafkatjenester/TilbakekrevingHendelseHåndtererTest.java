package no.nav.fplos.kafkatjenester;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.loslager.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.vedtak.felles.testutilities.db.Repository;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class TilbakekrevingHendelseHåndtererTest {

    private Repository repository;
    private TilbakekrevingHendelseHåndterer handler;

    private final List<Aksjonspunkt> åpentAksjonspunkt = List.of(new Aksjonspunkt("5015", "OPPR"));
    private final List<Aksjonspunkt> manueltPåVentAksjonspunkt = List.of(new Aksjonspunkt("5015", "OPPR"), new Aksjonspunkt("7002", "OPPR"));
    private final List<Aksjonspunkt> åpentBeslutter = List.of(new Aksjonspunkt("5005", "OPPR"));
    private final List<Aksjonspunkt> avsluttetAksjonspunkt = List.of(new Aksjonspunkt("5015", "AVBR"));

    @BeforeEach
    void setUp(EntityManager entityManager) {
        repository = new Repository(entityManager);
        var oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
        handler = new TilbakekrevingHendelseHåndterer(oppgaveEgenskapHandler, oppgaveRepository);
    }

    @Test
    public void skalOppretteOppgave() {
        var event = hendelse(åpentAksjonspunkt, BehandlingId.random());
        handler.håndter(event);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(1);
    }

    @Test
    public void skalVidereføreOppgaveVedNyAktivEvent() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndter(førsteEvent);

        var andreEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndter(andreEvent);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalLukkeAlleOppgaver() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndter(førsteEvent);

        var andreEvent = hendelse(åpentBeslutter, behandlingId);
        handler.håndter(andreEvent);

        var tredjeEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndter(hendelse(åpentAksjonspunkt, behandlingId));

        sjekkAntallOppgaver(3);
        sjekkKunEnAktivOppgave();
        sjekkOppgaveEventAntallEr(5);
    }

    @Test
    public void skalAvslutteOppgaveVedAvsluttedeAksjonspunkt() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        var andreEvent = hendelse(avsluttetAksjonspunkt, behandlingId);
        handler.håndter(førsteEvent);
        handler.håndter(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalLukkeOppgaveVedÅpentManueltTilVentAksjonspunkt() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        var andreEvent = hendelse(manueltPåVentAksjonspunkt, behandlingId);
        handler.håndter(førsteEvent);
        handler.håndter(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    public void skalOppretteTilBeslutterEgenskapVedAksjonspunkt5005() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentBeslutter, behandlingId);
        var andreEventUtenBeslutter = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndter(førsteEvent);

        sjekkAktivOppgaveEksisterer(true);
        verifiserAktivBeslutterEgenskap();

        handler.håndter(andreEventUtenBeslutter);
        verifiserInaktivBeslutterEgenskap();
    }

    @Test
    public void skalLukkeOppgaveVedReturFraTilBehandler() {
        var behandlingId = BehandlingId.random();
        var saksbehandler = hendelse(åpentAksjonspunkt, behandlingId);
        var tilBeslutter = hendelse(åpentBeslutter, behandlingId);
        handler.håndter(saksbehandler);
        handler.håndter(tilBeslutter);
        handler.håndter(saksbehandler);

        List<OppgaveEventLogg> oppgaveEventer = repository.hentAlle(OppgaveEventLogg.class).stream()
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
        List<OppgaveEgenskap> egenskaper = repository.hentAlle(OppgaveEgenskap.class);
        assertThat(egenskaper.get(0).getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);
        assertThat(egenskaper.get(0).getAktiv()).isEqualTo(status);
    }

    private void sjekkAntallOppgaver(int antall) {
        assertThat(repository.hentAlle(Oppgave.class)).hasSize(antall);
    }

    private void sjekkAktivOppgaveEksisterer(boolean aktiv) {
        List<Oppgave> oppgave = repository.hentAlle(Oppgave.class);
        assertThat(oppgave.get(0).getAktiv()).isEqualTo(aktiv);
        int antallAktive = (int) oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(aktiv ? 1 : 0);
    }

    private void sjekkKunEnAktivOppgave() {
        List<Oppgave> oppgave = repository.hentAlle(Oppgave.class);
        long antallAktive = oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(1L);
    }

    private void sjekkOppgaveEventAntallEr(int antall) {
        var eventer = repository.hentAlle(OppgaveEventLogg.class);
        assertThat(eventer).hasSize(antall);
    }

    private static TilbakekrevingHendelse hendelse(List<no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt> aksjonspunkter, BehandlingId behandlingId) {
        var tilbakekrevingHendelse = basisHendelse(aksjonspunkter, behandlingId);
        tilbakekrevingHendelse.setFeilutbetaltBeløp(BigDecimal.valueOf(500));
        tilbakekrevingHendelse.setFørsteFeilutbetalingDato(LocalDate.now());
        return tilbakekrevingHendelse;
    }

    private static TilbakekrevingHendelse basisHendelse(List<no.nav.foreldrepenger.loslager.hendelse.Aksjonspunkt> aksjonspunkter, BehandlingId behandlingId) {
        var tilbakekrevingHendelse = new TilbakekrevingHendelse();
        tilbakekrevingHendelse.setAksjonspunkter(aksjonspunkter);
        tilbakekrevingHendelse.setFagsystem(no.nav.foreldrepenger.loslager.hendelse.Fagsystem.FPTILBAKE);
        tilbakekrevingHendelse.setBehandlingId(behandlingId);
        tilbakekrevingHendelse.setSaksnummer("123");
        tilbakekrevingHendelse.setBehandlendeEnhet("0300");
        tilbakekrevingHendelse.setAktørId("345");
        tilbakekrevingHendelse.setBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD);
        tilbakekrevingHendelse.setBehandlingOpprettetTidspunkt(LocalDateTime.now());
        tilbakekrevingHendelse.setYtelseType(FagsakYtelseType.FORELDREPENGER);

        return tilbakekrevingHendelse;
    }
}
