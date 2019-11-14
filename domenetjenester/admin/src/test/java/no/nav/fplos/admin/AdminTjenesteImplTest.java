package no.nav.fplos.admin;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProviderImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import org.junit.Rule;
import org.junit.Test;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AdminTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepositoryProvider repositoryProvider = new OppgaveRepositoryProviderImpl(entityManager);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private FpsakEventHandler fpsakEventHandler = new FpsakEventHandler(repositoryProvider, foreldrepengerBehandlingRestKlient);
    private TilbakekrevingEventHandler tilbakekrevingEventHandler = new TilbakekrevingEventHandler(repositoryProvider);
    private KafkaReader kafkaReader = mock(KafkaReader.class);
    private AdminTjenesteImpl adminTjeneste = new AdminTjenesteImpl(repositoryProvider, foreldrepengerBehandlingRestKlient, fpsakEventHandler, tilbakekrevingEventHandler, kafkaReader);
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);

    private static String AVDELING_DRAMMEN_ENHET = "4806";

    private Oppgave førstegangOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(1L).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).medAktiv(true).build();
    private Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(2L).medBehandlingType(BehandlingType.KLAGE).medAktiv(true).build();
    private Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(3L).medBehandlingType(BehandlingType.INNSYN).medAktiv(true).build();
    private LocalDateTime aksjonspunktFrist = null;

    private void leggeInnEtSettMedOppgaver(){
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
    }

    @Test
    public void testHentOppgave(){
        leggeInnEtSettMedOppgaver();
        Oppgave oppgave = adminTjeneste.hentOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave).isNotNull();
        assertThat(oppgave.getId()).isEqualTo(førstegangOppgave.getId());
        assertThat(oppgave.getAktiv()).isEqualTo(førstegangOppgave.getAktiv());
    }

    @Test
    public void testHentEvent(){
        oppgaveRepository.lagre(new OppgaveEventLogg(førstegangOppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, null, førstegangOppgave.getBehandlingId()));
        List<OppgaveEventLogg> oppgave = adminTjeneste.hentEventer(førstegangOppgave.getBehandlingId());
        assertThat(oppgave).isNotEmpty();
    }

    @Test
    public void testOppfriskOppgaveIkkeLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any())).thenReturn(lagBehandlingDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave.getAktiv()).isTrue();
    }

    @Test
    public void testOppfriskOppgaveLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any())).thenReturn(lagBehandlingAvsluttetDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave.getAktiv()).isFalse();
    }

    @Test
    public void testLeggTilOppgaveEgenskapHvisUtlandssak(){
        oppgaveRepository.lagre(førstegangOppgave);
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(0);
        when(foreldrepengerBehandlingRestKlient.getBehandling(any())).thenReturn(lagBehandlingMedUtlandssakDto());
        adminTjeneste.oppdaterAktiveOppgaverMedInformasjonHvisUtlandssak();
        assertThat(repoRule.getRepository().hentAlle(OppgaveEgenskap.class)).hasSize(1);
    }

    private BehandlingFpsak lagBehandlingDto(){
        return BehandlingFpsak.builder().medStatus("UTRED").build();
    }

    private BehandlingFpsak lagBehandlingAvsluttetDto(){
        return BehandlingFpsak.builder().medStatus("AVSLU").build();
    }

    private BehandlingFpsak lagBehandlingMedUtlandssakDto() {
        return BehandlingFpsak.builder()
                .medErUtlandssak(true)
                .medAksjonspunkter(Collections.singletonList(new Aksjonspunkt
                        .Builder()
                        .medDefinisjon("5068")
                        .medStatus("OPPR")
                        .medFristTid(aksjonspunktFrist)
                        .medBegrunnelse("BOSATT_UTLAND")
                        .build()))
                .build();
    }
}
