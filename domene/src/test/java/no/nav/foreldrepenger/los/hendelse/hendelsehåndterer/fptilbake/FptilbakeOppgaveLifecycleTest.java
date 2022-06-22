package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake;

import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType.GJENAPNET;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType.LUKKET;
import static no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType.OPPRETTET;
import static no.nav.foreldrepenger.los.oppgave.util.OppgaveAssert.assertThatOppgave;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.EndreEnhetTransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.LukkOppgaveTransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OppdaterOppgaveTransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.OpprettOppgaveTransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.ReturFraBeslutterOppgaveTransisjon;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.håndterere.TilBeslutterOppgaveTransisjon;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import no.nav.foreldrepenger.extensions.JpaExtension;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake.FptilbakeOppgavehendelseHåndterer.FptilbakeData;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveHistorikk;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.TilbakekrevingHendelse;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.statistikk.kø.KøStatistikkTjeneste;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class FptilbakeOppgaveLifecycleTest {

    private EntityManager entityManager;
    private OpprettOppgaveTransisjon opprettOppgaveHandler;

    private static final List<Aksjonspunkt> åpentAksjonspunkt = List.of(new Aksjonspunkt("5015", "OPPR"));
    private static final List<Aksjonspunkt> manueltPåVentAksjonspunkt = List.of(new Aksjonspunkt("5015", "OPPR"), new Aksjonspunkt("7002", "OPPR"));
    private static final List<Aksjonspunkt> åpentBeslutter = List.of(new Aksjonspunkt("5005", "OPPR"));
    private static final List<Aksjonspunkt> avsluttetAksjonspunkt = List.of(new Aksjonspunkt("5015", "AVBR"));
    private EndreEnhetTransisjon endreEnhetHandler;
    private ReservasjonTjeneste reservasjonTjeneste;
    private OppdaterOppgaveTransisjon oppdaterOppgave;
    private LukkOppgaveTransisjon lukkOppgaveTransisjon;
    private TilBeslutterOppgaveTransisjon tilBeslutterTransisjon;
    private ReturFraBeslutterOppgaveTransisjon returFraBeslutterOppgaveTransisjon;


    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, new ReservasjonRepository(entityManager));
        var oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        var statistikkMock = mock(KøStatistikkTjeneste.class);
        opprettOppgaveHandler = new OpprettOppgaveTransisjon(statistikkMock, oppgaveTjeneste);
        endreEnhetHandler = new EndreEnhetTransisjon(statistikkMock, oppgaveTjeneste);
        oppdaterOppgave = new OppdaterOppgaveTransisjon(statistikkMock, oppgaveTjeneste, reservasjonTjeneste);
        lukkOppgaveTransisjon = new LukkOppgaveTransisjon(statistikkMock, oppgaveTjeneste);
        tilBeslutterTransisjon = new TilBeslutterOppgaveTransisjon(statistikkMock, oppgaveTjeneste);
        returFraBeslutterOppgaveTransisjon = new ReturFraBeslutterOppgaveTransisjon(oppgaveTjeneste, statistikkMock, reservasjonTjeneste);
    }

    @Test
    public void opprettOppgaveOgOppdaterVedAksjonspunktendring() {
        var hendelse = hendelse();
        opprettOppgaveHandler.håndter(fptilbakeData(hendelse));
        reservasjonTjeneste.reserverOppgave(DBTestUtil.hentUnik(entityManager, Oppgave.class).getId());
        oppdaterOppgave.håndter(fptilbakeData(hendelse));

        var oel = alle(OppgaveEventLogg.class).stream().map(OppgaveEventLogg::getEventType).toList();
        assertThat(oel).containsExactly(OPPRETTET, GJENAPNET);
        var oppgaver = alle(Oppgave.class);
        assertThatOppgave(oppgaver.get(0))
                .harAktiv(false)
                .harBehandlendeEnhet("1234");
        assertThatOppgave(oppgaver.get(1))
                .harAktiv(true)
                .harBehandlendeEnhet("1234");
        // gammel reservasjon er videreført ved oppdatering
        assertThat(oppgaver.get(0).getReservasjon().getReservertTil()).isBefore(LocalDateTime.now());
        assertThat(oppgaver.get(1).getReservasjon().getReservertTil()).isAfter(LocalDateTime.now());
    }

    @Test
    public void endrerEnhet() {
        var hendelse = hendelse();
        opprettOppgaveHandler.håndter(fptilbakeData(hendelse));
        reservasjonTjeneste.reserverOppgave(alle(Oppgave.class).get(0).getId());

        // ny enhet
        hendelse.setBehandlendeEnhet("5678");
        endreEnhetHandler.håndter(fptilbakeData(hendelse));

        var oel = alle(OppgaveEventLogg.class).stream().map(OppgaveEventLogg::getEventType).toList();
        assertThat(oel).containsExactly(OPPRETTET, LUKKET, OPPRETTET);
        var oppgaver = alle(Oppgave.class);
        assertThatOppgave(oppgaver.get(0))
                .harAktiv(false)
                .harBehandlendeEnhet("1234");
        assertThatOppgave(oppgaver.get(1))
                .harAktiv(true)
                .harBehandlendeEnhet("5678");
        assertThat(oppgaver.get(0).getReservasjon()).isNotNull();
        assertThat(oppgaver.get(1).getReservasjon()).isNull();
    }

    @Test
    public void oppretterBeslutterOppgaveOgReturnererFraBeslutter() {
        var hendelse = hendelse();
        opprettOppgaveHandler.håndter(fptilbakeData(hendelse));
        reservasjonTjeneste.reserverOppgave(alle(Oppgave.class).get(0));

        // til beslutter
        hendelse.setAksjonspunkter(åpentBeslutter);
        tilBeslutterTransisjon.håndter(fptilbakeData(hendelse));
        var oppgaver = alle(Oppgave.class);
        var beslutterOppgave = oppgaver.get(1);
        assertThatOppgave(beslutterOppgave).harAktiv(true);
        assertThat(beslutterOppgave.getReservasjon()).isNull();
        var oel = alle(OppgaveEventLogg.class).stream().map(OppgaveEventLogg::getEventType).toList();
        assertThat(oel).containsExactly(OPPRETTET, LUKKET, OPPRETTET);
        assertThat(alle(OppgaveEventLogg.class).get(2).getAndreKriterierType()).isEqualTo(AndreKriterierType.TIL_BESLUTTER);

        // retur fra beslutter
        hendelse.setAksjonspunkter(åpentAksjonspunkt);
        returFraBeslutterOppgaveTransisjon.håndter(fptilbakeData(hendelse));
        oppgaver = alle(Oppgave.class);
        var returnertOppgave = oppgaver.get(2);
        assertThat(returnertOppgave.getReservasjon().getReservertAv()).isEqualTo("ANSVARLIGSAKSB");
        assertThatOppgave(returnertOppgave).harAktiv(true);
    }

    @Test
    public void lukkOppgave() {
        var hendelse = hendelse();
        opprettOppgaveHandler.håndter(fptilbakeData(hendelse));

        // lukk oppgave
        hendelse.setAksjonspunkter(avsluttetAksjonspunkt);
        lukkOppgaveTransisjon.håndter(fptilbakeData(hendelse));

        var oppgaver = alle(Oppgave.class);
        assertThat(oppgaver).hasSize(1);
        assertThatOppgave(oppgaver.get(0)).harAktiv(false);
        var oel = alle(OppgaveEventLogg.class).stream().map(OppgaveEventLogg::getEventType).collect(Collectors.toList());
        assertThat(oel).containsExactly(OPPRETTET, LUKKET);
    }

    private static TilbakekrevingHendelse hendelse() {
        var hendelseEntitet = new TilbakekrevingHendelse();
        hendelseEntitet.setAksjonspunkter(åpentAksjonspunkt);
        hendelseEntitet.setFeilutbetaltBeløp(BigDecimal.valueOf(1000));
        hendelseEntitet.setAnsvarligSaksbehandler("ANSVARLIGSAKSB");
        hendelseEntitet.setHref("URL/");
        hendelseEntitet.setAktørId(AktørId.dummy().getId());
        hendelseEntitet.setBehandlingType(BehandlingType.TILBAKEBETALING);
        hendelseEntitet.setFagsystem(Fagsystem.FPTILBAKE);
        hendelseEntitet.setBehandlingId(BehandlingId.random());
        hendelseEntitet.setSaksnummer("1234");
        hendelseEntitet.setYtelseType(FagsakYtelseType.FORELDREPENGER);
        hendelseEntitet.setBehandlendeEnhet("1234");
        return hendelseEntitet;
    }


    private FptilbakeData fptilbakeData(TilbakekrevingHendelse hendelse) {
        var oppgaveLogg = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        return new FptilbakeData(hendelse, new OppgaveHistorikk(oppgaveLogg));
    }

    private <T extends BaseEntitet> List<T> alle(Class<T> clazz) {
        return DBTestUtil.hentAlle(entityManager, clazz);
    }

}