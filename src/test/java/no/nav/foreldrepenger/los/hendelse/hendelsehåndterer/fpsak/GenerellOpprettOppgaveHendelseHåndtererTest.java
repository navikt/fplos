package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.behandlingFpsak;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.OppgaveTestUtil.førsteUttaksDag;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import no.nav.foreldrepenger.los.domene.typer.Fagsystem;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.Beskyttelsesbehov;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.håndterere.GenerellOpprettOppgaveOppgavetransisjonHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;


@ExtendWith(MockitoExtension.class)
@ExtendWith(JpaExtension.class)
class GenerellOpprettOppgaveHendelseHåndtererTest {
    private EntityManager entityManager;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private OppgaveTjeneste oppgaveTjeneste;
    private GenerellOpprettOppgaveOppgavetransisjonHåndterer opprettOppgaveHåndterer;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, Mockito.mock(ReservasjonTjeneste.class));
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(Mockito.mock(Beskyttelsesbehov.class));
        opprettOppgaveHåndterer = new GenerellOpprettOppgaveOppgavetransisjonHåndterer(oppgaveTjeneste, oppgaveEgenskapHåndterer, Mockito.mock(ReservasjonTjeneste.class));
    }

    @Test
    void skalLagreOppgaveMedFelterFraBehandling() {
        var behandlingFpsak = behandlingFpsak();
        var behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        opprettOppgaveHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        OppgaveAssert.assertThatOppgave(oppgave).harBehandlingOpprettet(behandlingFpsak.opprettetTidspunkt())
                     .harAktiv(true)
                     .harBehandlingId(behandlingId)
                     .harBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                     .harAktørId(new AktørId(behandlingFpsak.aktørId().getAktørId()))
                     .harFørsteStønadsdag(førsteUttaksDag())
                     .harSaksnummer(new Saksnummer(behandlingFpsak.saksnummer()))
                     .harOppgaveAvsluttet(null)
                     .harBehandlendeEnhet(behandlingFpsak.behandlendeEnhetId())
                     .harSystem(Fagsystem.FPSAK)
                     .harFagsakYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    @Test
    void skalOppretteOppgaveEventLogg() {
        var behandlingFpsak = behandlingFpsak();
        var behandlingId = new BehandlingId(behandlingFpsak.behandlingUuid());
        opprettOppgaveHåndterer.håndter(behandlingId, behandlingFpsak, new OppgaveHistorikk(List.of()));

        var oppgaveEventLogg = DBTestUtil.hentUnik(entityManager, OppgaveEventLogg.class);
        assertThat(oppgaveEventLogg.getEventType()).isEqualTo(OppgaveEventType.OPPRETTET);
        assertThat(oppgaveEventLogg.getBehandlendeEnhet()).isEqualTo(behandlingFpsak.behandlendeEnhetId());
        assertThat(oppgaveEventLogg.getBehandlingId().toUUID()).isEqualTo(behandlingFpsak.behandlingUuid());
        assertThat(oppgaveEventLogg.getAndreKriterierType()).isNull();
        assertThat(oppgaveEventLogg.getFristTid()).isNull();
    }

}
