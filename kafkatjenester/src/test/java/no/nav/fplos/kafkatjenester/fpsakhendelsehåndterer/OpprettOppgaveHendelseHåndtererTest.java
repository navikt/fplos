package no.nav.fplos.kafkatjenester.fpsakhendelsehåndterer;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.Lazy;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHåndterer;
import no.nav.fplos.oppgavestatistikk.OppgaveStatistikk;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static no.nav.foreldrepenger.los.testutils.oppgave.OppgaveAssert.assertThatOppgave;
import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class OpprettOppgaveHendelseHåndtererTest {
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
    public void opprettOppgave() {
        var behandlingId = BehandlingId.random();
        var behandlingstidFrist = LocalDate.now().plusDays(10);
        var behandlingOpprettet = LocalDateTime.now();
        var aktørId = AktørId.dummy();
        var behandlingFpsak = BehandlingFpsak.builder()
                .medBehandlingOpprettet(behandlingOpprettet)
                .medBehandlendeEnhetId("4406")
                .medBehandlingId(behandlingId)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medFørsteUttaksdag(new Lazy<>(OpprettOppgaveHendelseHåndtererTest::førsteUttaksDag))
                .medErBerørtBehandling(false)
                .medErEndringssøknad(false)
                .medBehandlingstidFrist(behandlingstidFrist)
                .medAksjonspunkter(new Lazy<>(this::aksjonspunkter))
                .medStatus("OPPRE")
                .build();
        behandlingFpsak.setSaksnummer("1234");
        behandlingFpsak.setAktørId(aktørId.getId());
        behandlingFpsak.setYtelseType(FagsakYtelseType.FORELDREPENGER);

        var opprettOppgaveHåndterer = new OpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer, oppgaveStatistikk, behandlingFpsak);
        opprettOppgaveHåndterer.håndter();

        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        assertThatOppgave(oppgave)
                .harBehandlingOpprettet(behandlingFpsak.getBehandlingOpprettet())
                .harAktiv(true)
                .harBehandlingId(behandlingId)
                .harBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .harBehandlingsfrist(behandlingstidFrist.atStartOfDay())
                .harAktørId(aktørId)
                .harFørsteStønadsdag(førsteUttaksDag())
                .harHref(null)
                .harSaksnummer(Long.valueOf(behandlingFpsak.getSaksnummer()))
                .harOppgaveAvsluttet(null)
                .harBehandlingStatus(BehandlingStatus.fraKode(behandlingFpsak.getStatus()))
                .harBehandlendeEnhet(behandlingFpsak.getBehandlendeEnhetId())
                .harSystem("FPSAK")
                .harFagsakYtelseType(behandlingFpsak.getYtelseType());
    }

    private static LocalDate førsteUttaksDag() {
        return LocalDate.of(2021, 3, 1);
    }

    private List<Aksjonspunkt> aksjonspunkter() {
        var aksjonspunkt = Aksjonspunkt.builder()
                .medDefinisjon("1111")
                .medBegrunnelse("Testbegrunnelse")
                .medFristTid(null)
                .medStatus("OPPR")
                .build();
        return List.of(aksjonspunkt);
    }

}
