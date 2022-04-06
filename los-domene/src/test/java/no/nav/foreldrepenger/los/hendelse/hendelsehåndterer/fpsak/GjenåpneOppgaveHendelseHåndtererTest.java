package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.dbstøtte.DBTestUtil;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GjenåpneOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class GjenåpneOppgaveHendelseHåndtererTest {
    private final KøStatistikkTjeneste køStatistikk = mock(KøStatistikkTjeneste.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private ReservasjonTjeneste reservasjonTjeneste;
    private BehandlingFpsak behandlingFpsak;
    private Oppgave kopiAvEksisterendeOppgave;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepository(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));

        behandlingFpsak = behandlingFpsak();
        var eksisterendeOppgave = Oppgave.builder()
                .dummyOppgave("1111")
                .medAktiv(false)
                .medBehandlingId(behandlingFpsak.getBehandlingId()).build();
        kopiAvEksisterendeOppgave = new Oppgave();
        kopiAvEksisterendeOppgave.avstemMed(eksisterendeOppgave);
        oppgaveRepository.lagre(eksisterendeOppgave);
    }

    @Test
    public void skalGjenåpneEksisterendeOppgave() {
        new GjenåpneOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak, reservasjonTjeneste).håndter();

        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThat(oppgave.getAktiv()).isTrue();
        assertThat(oppgave.getBehandlendeEnhet()).isNotEqualTo(kopiAvEksisterendeOppgave.getBehandlendeEnhet());
        assertThatOppgave(oppgave)
                .harBehandlingOpprettet(behandlingFpsak.getBehandlingOpprettet())
                .harAktørId(new AktørId(behandlingFpsak.getAktørId()))
                .harBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId());
    }

    @Test
    public void skalOppretteOppgaveEventLogg() {
        new GjenåpneOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, køStatistikk, behandlingFpsak, reservasjonTjeneste).håndter();

        var oel = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oel.getBehandlingId()).isEqualTo(behandlingFpsak.getBehandlingId());
        assertThat(oel.getAndreKriterierType()).isNull();
        assertThat(oel.getBehandlendeEnhet()).isEqualTo(behandlingFpsak.getBehandlendeEnhetId());
        assertThat(oel.getEventType()).isEqualTo(OppgaveEventType.GJENAPNET);
    }

}
