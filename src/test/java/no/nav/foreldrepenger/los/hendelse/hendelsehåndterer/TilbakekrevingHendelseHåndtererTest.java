package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.oppgave.TilbakekrevingOppgave;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.DBTestUtil;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.aktør.AktørId;
import no.nav.foreldrepenger.los.felles.BaseEntitet;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.tilbakekreving.TilbakekrevingHendelseHåndterer;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingsstatus;
import no.nav.vedtak.hendelser.behandling.Behandlingstype;
import no.nav.vedtak.hendelser.behandling.Kildesystem;
import no.nav.vedtak.hendelser.behandling.Ytelse;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

@ExtendWith(JpaExtension.class)
@ExtendWith(MockitoExtension.class)
class TilbakekrevingHendelseHåndtererTest {

    private EntityManager entityManager;
    private TilbakekrevingHendelseHåndterer handler;

    private final List<Aksjonspunkt> åpentAksjonspunkt = List.of(lagAp("5015", "OPPR"));
    private final List<Aksjonspunkt> manueltPåVentAksjonspunkt = List.of(lagAp("5015", "OPPR"), lagAp("7002", "OPPR"));
    private final List<Aksjonspunkt> åpentBeslutter = List.of(lagAp("5005", "OPPR"));
    private final List<Aksjonspunkt> avsluttetAksjonspunkt = List.of(lagAp("5015", "AVBR"));

    private static Aksjonspunkt lagAp(String kode, String status) {
        return Aksjonspunkt.builder().medDefinisjon(kode).medStatus(status).build();
    }

    @BeforeEach
    void setUp(EntityManager entityManager) {
        this.entityManager = entityManager;
        var oppgaveRepository = new OppgaveRepository(entityManager);
        var oppgaveEgenskapHandler = new OppgaveEgenskapHåndterer(oppgaveRepository, Mockito.mock(Beskyttelsesbehov.class));
        var oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, Mockito.mock(ReservasjonTjeneste.class));
        handler = new TilbakekrevingHendelseHåndterer(oppgaveEgenskapHandler, oppgaveRepository, oppgaveTjeneste, Mockito.mock(ReservasjonTjeneste.class));
    }

    @Test
    void skalOppretteOppgave() {
        var event = hendelse(åpentAksjonspunkt, BehandlingId.random());
        handler.håndterBehandling(event);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(1);
    }

    @Test
    void skalVidereføreOppgaveVedNyAktivEvent() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(førsteEvent);

        var andreEvent = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(andreEvent);

        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(true);
        sjekkOppgaveEventAntallEr(1);
    }

    @Test
    void skalLukkeGamleOppgaverVedOvergangMellomBeslutterOgSaksbehandlerOppgaver() {
        var behandlingId = BehandlingId.random();
        var førsteEventOppgave = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(førsteEventOppgave);

        var andreEventBeslutterOppgave = hendelse(åpentBeslutter, behandlingId);
        handler.håndterBehandling(andreEventBeslutterOppgave);

        var tredjeEventOppgave = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(tredjeEventOppgave);

        sjekkAntallOppgaver(3);
        sjekkKunEnAktivOppgave();
        sjekkOppgaveEventAntallEr(5);
    }

    @Test
    void skalAvslutteOppgaveVedAvsluttedeAksjonspunkt() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        var andreEvent = hendelse(avsluttetAksjonspunkt, behandlingId);
        handler.håndterBehandling(førsteEvent);
        handler.håndterBehandling(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    void skalLukkeOppgaveVedÅpentManueltTilVentAksjonspunkt() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        var andreEvent = hendelse(manueltPåVentAksjonspunkt, behandlingId);
        handler.håndterBehandling(førsteEvent);
        handler.håndterBehandling(andreEvent);
        sjekkAntallOppgaver(1);
        sjekkAktivOppgaveEksisterer(false);
        sjekkOppgaveEventAntallEr(2);
    }

    @Test
    void skalOppretteTilBeslutterEgenskapVedAksjonspunkt5005() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentBeslutter, behandlingId);
        var andreEventUtenBeslutter = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(førsteEvent);

        sjekkAktivOppgaveEksisterer(true);
        sjekkBeslutterEgenskapMedAktivstatus(true);

        handler.håndterBehandling(andreEventUtenBeslutter);
        sjekkBeslutterEgenskapMedAktivstatus(false);
    }

    @Test
    void skalOppretteTilBeslutterEgenskapVedReturTilForeslå() {
        var behandlingId = BehandlingId.random();
        var førsteEvent = hendelse(åpentAksjonspunkt, behandlingId);
        var andreEventBeslutter = hendelse(åpentBeslutter, behandlingId);
        var tredjeAp = List.of(lagAp("5005", "OPPR"), lagAp("5004", "OPPR"));
        var tredjeEventReturTilForeslå = hendelse(tredjeAp, behandlingId);
        handler.håndterBehandling(førsteEvent);

        sjekkAktivOppgaveEksisterer(true);
        sjekkBeslutterEgenskapMedAktivstatus(false);

        handler.håndterBehandling(andreEventBeslutter);
        sjekkBeslutterEgenskapMedAktivstatus(true);

        handler.håndterBehandling(tredjeEventReturTilForeslå);
        sjekkBeslutterEgenskapMedAktivstatus(false);
    }

    @Test
    void skalLukkeOppgaveVedReturFraTilBehandler() {
        var behandlingId = BehandlingId.random();
        var saksbehandler = hendelse(åpentAksjonspunkt, behandlingId);
        var tilBeslutter = hendelse(åpentBeslutter, behandlingId);
        handler.håndterBehandling(saksbehandler);
        handler.håndterBehandling(tilBeslutter);
        handler.håndterBehandling(saksbehandler);

        var oppgaveEventer = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class)
                                       .stream()
                                       .sorted(Comparator.comparing(OppgaveEventLogg::getOpprettetTidspunkt))
                                       .toList();

        verifiserOppgaveEvent(oppgaveEventer.get(0), OppgaveEventType.OPPRETTET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(1), OppgaveEventType.LUKKET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(2), OppgaveEventType.OPPRETTET, AndreKriterierType.TIL_BESLUTTER);
        verifiserOppgaveEvent(oppgaveEventer.get(3), OppgaveEventType.LUKKET, null);
        verifiserOppgaveEvent(oppgaveEventer.get(4), OppgaveEventType.OPPRETTET, null);
    }

    @Test
    void skalHaEgenskapUtlandVedFagsakMarkering() {
        var behandlingId = BehandlingId.random();
        var fagsakEgenskaper = new LosFagsakEgenskaperDto(List.of("BOSATT_UTLAND"), null);
        var hendelse = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(hendelse, fagsakEgenskaper);
        var oppgaveEgenskaper = DBTestUtil.hentUnik(entityManager, OppgaveEgenskap.class);
        assertThat(oppgaveEgenskaper.getAndreKriterierType()).isEqualTo(AndreKriterierType.UTLANDSSAK);
    }

    @Test
    void skalSetteTilbakekrevingsEgenskapPåOppgaveIExpandFaseTFP6398() {
        // aktuell test for expand fase av TFP-6398
        var behandlingId = BehandlingId.random();
        var saksbehandler = hendelse(åpentAksjonspunkt, behandlingId);

        handler.håndterBehandling(saksbehandler);
        var oppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class);

        // sjekker at vi setter tilbakebetalingsfeltene på både Oppgave og TilbakekrevingOppgave
        assertThat(oppgave)
            .matches(o -> o.getFeilutbetalingBelop().equals(BigDecimal.valueOf(500)), "Oppgave har nytt felt feilutbetalingBelop satt")
            .matches(o -> o.getFeilutbetalingStart() != null, "Oppgave har nytt felt feilutbetalingStart satt")
            .matches(o -> o instanceof TilbakekrevingOppgave tbk
                    && tbk.getBelop().equals(BigDecimal.valueOf(500)) && tbk.getFeilutbetalingstart() != null, "TilbakekrevingOppgave har fortsatt feltet satt");


        // simulerer eksisterende oppgaver opprettet før expand fase som ikke har disse feltene satt på Oppgave
        entityManager.createNativeQuery("update oppgave set feilutbetaling_belop = null where id = :oppgaveId")
            .setParameter("oppgaveId", oppgave.getId()).executeUpdate();
        entityManager.createNativeQuery("update oppgave set feilutbetaling_start = null where id = :oppgaveId")
            .setParameter("oppgaveId", oppgave.getId()).executeUpdate();
        entityManager.flush();
        entityManager.refresh(oppgave);
        assertThat(oppgave.getFeilutbetalingStart()).isNull();

        handler.håndterBehandling(saksbehandler); // skal gjenåpne/oppdatere eksisterende oppgave

        var oppdatertOppgave = DBTestUtil.hentUnik(entityManager, Oppgave.class); // vi forventer oppdatert oppgave, henter på nytt
        assertThat(oppdatertOppgave).matches(o -> o.getFeilutbetalingBelop() != null && o.getFeilutbetalingStart() != null,
                "Oppgave har nye felt satt etter ny hendelse")
            .matches(o -> o instanceof TilbakekrevingOppgave tbk && tbk.getBelop().equals(BigDecimal.valueOf(500)) && tbk.getFeilutbetalingstart() != null,
                "TilbakekrevingOppgave har fortsatt feltene");
    }

    @Test
    void skalFåEgenskapEøsSakNårMarkertSomEøs() {
        var behandlingId = BehandlingId.random();
        var fpsakEgenskaper = new LosFagsakEgenskaperDto(List.of("EØS_BOSATT_NORGE"), null);
        var hendelse = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(hendelse, fpsakEgenskaper);
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        var kriterieTyper = oppgaveEgenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType);
        assertThat(kriterieTyper).contains(AndreKriterierType.EØS_SAK);
    }

    @Test
    void skalIkkeFåEgenskapEøsSakNårIkkeEøsMarkert() {
        var behandlingId = BehandlingId.random();
        var fpsakEgenskaper = new LosFagsakEgenskaperDto(List.of("BOSATT_UTLAND"), null);
        var hendelse = hendelse(åpentAksjonspunkt, behandlingId);
        handler.håndterBehandling(hendelse, fpsakEgenskaper);
        var oppgaveEgenskaper = DBTestUtil.hentAlle(entityManager, OppgaveEgenskap.class);
        var kriterieTyper = oppgaveEgenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType);
        assertThat(kriterieTyper).isNotEmpty().doesNotContain(AndreKriterierType.EØS_SAK);
    }

    private void verifiserOppgaveEvent(OppgaveEventLogg event, OppgaveEventType type, AndreKriterierType kriterierType) {
        assertThat(event.getEventType()).isEqualTo(type);
        assertThat(event.getAndreKriterierType()).isEqualTo(kriterierType);
    }

    private void sjekkBeslutterEgenskapMedAktivstatus(boolean status) {
        var oppgaver = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var aktivOppgave = oppgaver.stream().filter(Oppgave::getAktiv).findFirst().orElse(null);
        assertThat(aktivOppgave).isNotNull();
        var egenskaper = aktivOppgave.getOppgaveEgenskaper();
        if (status) {
            assertThat(egenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType).collect(Collectors.toSet())).contains(AndreKriterierType.TIL_BESLUTTER);
        } else {
            assertThat(egenskaper.stream().map(OppgaveEgenskap::getAndreKriterierType).collect(Collectors.toSet())).doesNotContain(AndreKriterierType.TIL_BESLUTTER);
        }
    }

    private void sjekkAntallOppgaver(int antall) {
        assertThat(DBTestUtil.hentAlle(entityManager, Oppgave.class)).hasSize(antall);
    }

    private void sjekkAktivOppgaveEksisterer(boolean aktiv) {
        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var sisteOppgave = oppgave.stream().max(Comparator.comparing(BaseEntitet::getOpprettetTidspunkt));
        assertTrue(sisteOppgave.isPresent());
        Assertions.assertThat(sisteOppgave.map(Oppgave::getAktiv).orElse(false)).isEqualTo(aktiv);
        var antallAktive = (int) oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(aktiv ? 1 : 0);
    }

    private void sjekkKunEnAktivOppgave() {
        var oppgave = DBTestUtil.hentAlle(entityManager, Oppgave.class);
        var antallAktive = oppgave.stream().filter(Oppgave::getAktiv).count();
        assertThat(antallAktive).isEqualTo(1L);
    }

    private void sjekkOppgaveEventAntallEr(int antall) {
        var eventer = DBTestUtil.hentAlle(entityManager, OppgaveEventLogg.class);
        assertThat(eventer).hasSize(antall);
    }

    private static LosBehandlingDto hendelse(List<Aksjonspunkt> aksjonspunkter, BehandlingId behandlingId) {
        var ap = aksjonspunkter.stream()
            .map(a -> new LosBehandlingDto.LosAksjonspunktDto(
                a.getDefinisjonKode(),
                "OPPR".equals(a.getStatusKode())
                    ? Aksjonspunktstatus.OPPRETTET
                    : Aksjonspunktstatus.AVBRUTT,
                null))
            .collect(Collectors.toList());
        return new LosBehandlingDto(behandlingId.toUUID(), Kildesystem.FPTILBAKE, "123", Ytelse.FORELDREPENGER,
            new no.nav.vedtak.hendelser.behandling.AktørId(AktørId.dummy().getId()), Behandlingstype.TILBAKEBETALING, Behandlingsstatus.OPPRETTET,
            LocalDateTime.now(), "0300", null, "saksbehandler", ap, List.of(),
            false, false, List.of(), null, List.of(),
            new LosBehandlingDto.LosTilbakeDto(BigDecimal.valueOf(500), LocalDate.now()));
    }

}
