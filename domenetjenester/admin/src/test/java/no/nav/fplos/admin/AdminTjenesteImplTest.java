package no.nav.fplos.admin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import javax.persistence.EntityManager;

import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.repository.AdminRepository;
import no.nav.foreldrepenger.loslager.repository.AdminRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.ForeldrepengerEventHåndterer;
import no.nav.fplos.kafkatjenester.OppgaveEgenskapHandler;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHåndterer;

public class AdminTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private final AdminRepository adminRepository = new AdminRepositoryImpl(entityManager);
    private final OppgaveEgenskapHandler oppgaveEgenskapHandler = new OppgaveEgenskapHandler(oppgaveRepository);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private ForeldrepengerEventHåndterer foreldrepengerEventHåndterer = new ForeldrepengerEventHåndterer(oppgaveRepository, foreldrepengerBehandlingRestKlient, oppgaveEgenskapHandler);
    private TilbakekrevingEventHåndterer tilbakekrevingEventHandler = new TilbakekrevingEventHåndterer(oppgaveRepository);
    private AdminTjenesteImpl adminTjeneste = new AdminTjenesteImpl(adminRepository, foreldrepengerBehandlingRestKlient, foreldrepengerEventHåndterer, tilbakekrevingEventHandler);

    private static String AVDELING_DRAMMEN_ENHET = "4806";

    private Oppgave førstegangOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(1L).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).medAktiv(true).medEksternId(UUID.nameUUIDFromBytes("1".getBytes())).build();
    private Oppgave klageOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(2L).medBehandlingType(BehandlingType.KLAGE).medAktiv(true).medEksternId(UUID.nameUUIDFromBytes("2".getBytes())).build();
    private Oppgave innsynOppgave = Oppgave.builder().dummyOppgave(AVDELING_DRAMMEN_ENHET).medBehandlingId(3L).medBehandlingType(BehandlingType.INNSYN).medAktiv(true).medEksternId(UUID.nameUUIDFromBytes("3".getBytes())).build();

    private void leggeInnEtSettMedOppgaver(){
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
    }

    @Test
    public void testHentOppgave(){
        leggeInnEtSettMedOppgaver();
        Oppgave oppgave = adminTjeneste.hentOppgave(førstegangOppgave.getEksternId());
        assertThat(oppgave).isNotNull();
        assertThat(oppgave.getId()).isEqualTo(førstegangOppgave.getId());
        assertThat(oppgave.getAktiv()).isEqualTo(førstegangOppgave.getAktiv());
    }

    @Test
    public void testHentEvent(){
        oppgaveRepository.lagre(new OppgaveEventLogg(førstegangOppgave.getEksternId(), OppgaveEventType.OPPRETTET, null, null, førstegangOppgave.getBehandlingId()));
        List<OppgaveEventLogg> oppgave = adminTjeneste.hentEventer(førstegangOppgave.getEksternId());
        assertThat(oppgave).isNotEmpty();
    }

    @Test
    public void testOppfriskOppgaveIkkeLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any(UUID.class))).thenReturn(lagBehandlingDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getEksternId());
        assertThat(oppgave.getAktiv()).isTrue();
    }

    @Test
    public void testOppfriskOppgaveLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any(UUID.class))).thenReturn(lagBehandlingAvsluttetDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getEksternId());
        assertThat(oppgave.getAktiv()).isFalse();
    }

    private BehandlingFpsak lagBehandlingDto(){
        return BehandlingFpsak.builder().medStatus("UTRED").build();
    }

    private BehandlingFpsak lagBehandlingAvsluttetDto(){
        return BehandlingFpsak.builder().medStatus("AVSLU").build();
    }
}
