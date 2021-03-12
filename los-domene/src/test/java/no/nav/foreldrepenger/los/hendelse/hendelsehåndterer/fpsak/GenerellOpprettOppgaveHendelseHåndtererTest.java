package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.førsteUttaksDag;
import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class GenerellOpprettOppgaveHendelseHåndtererTest {
    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
    }

    @Test
    public void skalLagreOppgaveMedFelterFraBehandling() {
        var behandlingFpsak = behandlingFpsak();

        var opprettOppgaveHåndterer = new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        opprettOppgaveHåndterer.håndter();

        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThatOppgave(oppgave)
                .harBehandlingOpprettet(behandlingFpsak.getBehandlingOpprettet())
                .harAktiv(true)
                .harBehandlingId(behandlingFpsak.getBehandlingId())
                .harBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .harBehandlingsfrist(behandlingFpsak.getBehandlingstidFrist())
                .harAktørId(new AktørId(behandlingFpsak.getAktørId()))
                .harFørsteStønadsdag(førsteUttaksDag())
                .harHref(null)
                .harSaksnummer(Long.valueOf(behandlingFpsak.getSaksnummer()))
                .harOppgaveAvsluttet(null)
                .harBehandlingStatus(BehandlingStatus.fraKode(behandlingFpsak.getStatus()))
                .harBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .harSystem("FPSAK")
                .harFagsakYtelseType(behandlingFpsak.getYtelseType());
    }

    @Test
    public void skalOppretteOppgaveEventLogg() {
        var behandlingFpsak = behandlingFpsak();
        new GenerellOpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer,oppgaveStatistikk, behandlingFpsak).håndter();

        var oppgaveEventLogg = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.getBehandlendeEnhet()).isEqualTo(behandlingFpsak.getBehandlendeEnhetId());
        assertThat(oppgaveEventLogg.getBehandlingId()).isEqualTo(behandlingFpsak.getBehandlingId());
        assertThat(oppgaveEventLogg.getAndreKriterierType()).isNull();
        assertThat(oppgaveEventLogg.getFristTid()).isNull();
    }

}
