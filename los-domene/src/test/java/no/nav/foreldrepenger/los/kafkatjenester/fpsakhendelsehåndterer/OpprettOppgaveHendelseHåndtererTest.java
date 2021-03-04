package no.nav.foreldrepenger.los.kafkatjenester.fpsakhendelsehåndterer;

import no.nav.foreldrepenger.dbstoette.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.OppgaveEgenskapHåndterer;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.ny_fpsakhendelsehåndterer.OpprettOppgaveHendelseHåndterer;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.oppgaveegenskap.AktuelleOppgaveEgenskaperTjeneste;
import no.nav.foreldrepenger.los.risikovurdering.RisikovurderingTjeneste;
import no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert;
import no.nav.foreldrepenger.los.oppgave.BehandlingStatus;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.statistikk.statistikk_ny.OppgaveStatistikk;
import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.klient.fpsak.BehandlingFpsak;
import no.nav.foreldrepenger.los.klient.fpsak.Lazy;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.mock;


@ExtendWith(MockitoExtension.class)
@ExtendWith(EntityManagerFPLosAwareExtension.class)
class OpprettOppgaveHendelseHåndtererTest {
    private final OppgaveStatistikk oppgaveStatistikk = mock(OppgaveStatistikk.class);
    private EntityManager entityManager;
    private OppgaveRepository oppgaveRepository;
    private OppgaveEgenskapHåndterer oppgaveEgenskapHåndterer;
    private AktuelleOppgaveEgenskaperTjeneste aktuelleOppgaveEgenskapTjeneste;

    @BeforeEach
    private void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        oppgaveEgenskapHåndterer = new OppgaveEgenskapHåndterer(oppgaveRepository);
        aktuelleOppgaveEgenskapTjeneste = new AktuelleOppgaveEgenskaperTjeneste(mock(RisikovurderingTjeneste.class));
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

        var opprettOppgaveHåndterer = new OpprettOppgaveHendelseHåndterer(oppgaveRepository, oppgaveEgenskapHåndterer,
                oppgaveStatistikk, aktuelleOppgaveEgenskapTjeneste, behandlingFpsak);
        opprettOppgaveHåndterer.håndter();

        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);
        OppgaveAssert.assertThatOppgave(oppgave)
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
