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

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class GenerellOpprettOppgaveHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private GenerellOpprettOppgaveOppgavetransisjonHåndterer opprettOppgaveHåndterer;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        OppgaveTjeneste oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, mock(ReservasjonTjeneste.class));
        opprettOppgaveHåndterer = new GenerellOpprettOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste, køStatistikk);
    }

    @Test
    public void skalLagreOppgaveMedFelterFraBehandling() {
        var behandlingFpsak = behandlingFpsak();
        opprettOppgaveHåndterer.håndter(behandlingFpsak);
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
                .harSaksnummer(behandlingFpsak.getSaksnummer())
                .harOppgaveAvsluttet(null)
                .harBehandlingStatus(behandlingFpsak.getStatus())
                .harBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .harSystem("FPSAK")
                .harFagsakYtelseType(behandlingFpsak.getYtelseType());
    }

    @Test
    public void skalOppretteOppgaveEventLogg() {
        var behandlingFpsak = behandlingFpsak();
        opprettOppgaveHåndterer.håndter(behandlingFpsak);

        var oppgaveEventLogg = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.getBehandlendeEnhet()).isEqualTo(behandlingFpsak.getBehandlendeEnhetId());
        assertThat(oppgaveEventLogg.getBehandlingId()).isEqualTo(behandlingFpsak.getBehandlingId());
        assertThat(oppgaveEventLogg.getAndreKriterierType()).isNull();
        assertThat(oppgaveEventLogg.getFristTid()).isNull();
    }

}
