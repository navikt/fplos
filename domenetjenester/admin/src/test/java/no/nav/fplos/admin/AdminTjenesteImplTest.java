package no.nav.fplos.admin;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import org.junit.Rule;
import org.junit.Test;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.loslager.BehandlingId;
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

public class AdminTjenesteImplTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager entityManager = repoRule.getEntityManager();
    private final OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private final AdminRepository adminRepository = new AdminRepositoryImpl(entityManager);
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);
    private OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
    private AdminTjenesteImpl adminTjeneste = new AdminTjenesteImpl(adminRepository, foreldrepengerBehandlingRestKlient, organisasjonRepository);
    private AvdelingslederTjenesteImpl avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);

    private static String AVDELING_DRAMMEN_ENHET = "4806";

    private Oppgave førstegangOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medAktiv(true)
            .medBehandlingId(BehandlingId.random())
            .build();
    private Oppgave klageOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.KLAGE)
            .medAktiv(true)
            .medBehandlingId(BehandlingId.random())
            .build();
    private Oppgave innsynOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.INNSYN)
            .medAktiv(true)
            .medBehandlingId(BehandlingId.random())
            .build();

    private void leggeInnEtSettMedOppgaver(){
        oppgaveRepository.lagre(førstegangOppgave);
        oppgaveRepository.lagre(klageOppgave);
        oppgaveRepository.lagre(innsynOppgave);
    }

    @Test
    public void skalKunneOppretteNyAvdeling() {
        List<Avdeling> avdelingerFør = avdelingslederTjeneste.hentAvdelinger();
        var avdeling = new Avdeling("1234", "Nav på ny", Boolean.FALSE);
        adminTjeneste.opprettAvdeling(avdeling);
        var avdelingerDiff = avdelingslederTjeneste.hentAvdelinger().stream()
                .filter(e -> !avdelingerFør.contains(e))
                .collect(toList());
        assertThat(avdelingerDiff).containsExactly(avdeling);
        assertThat(avdelingerDiff.get(0).getAvdelingEnhet()).isEqualTo("1234");
        assertThat(avdelingerDiff.get(0).getNavn()).isEqualTo("Nav på ny");
        assertThat(avdelingerDiff.get(0).getKreverKode6()).isEqualTo(Boolean.FALSE);
    }

    @Test
    public void nårOpprettelseAvDuplikatAvdelingSkalDetKastesFeil() {
        var avdeling = new Avdeling("1234", "Nav på ny", Boolean.FALSE);
        adminTjeneste.opprettAvdeling(avdeling);
        assertThrows(IllegalArgumentException.class, () -> adminTjeneste.opprettAvdeling(avdeling));
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
        oppgaveRepository.lagre(new OppgaveEventLogg(førstegangOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, null));
        List<OppgaveEventLogg> oppgave = adminTjeneste.hentEventer(førstegangOppgave.getBehandlingId());
        assertThat(oppgave).isNotEmpty();
    }

    @Test
    public void testOppfriskOppgaveIkkeLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandlingDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave.getAktiv()).isTrue();
    }

    @Test
    public void testOppfriskOppgaveLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingRestKlient.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandlingAvsluttetDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave.getAktiv()).isFalse();
    }

    private BehandlingFpsak lagBehandlingDto(){
        return BehandlingFpsak.builder().medStatus("UTRED").build();
    }

    private BehandlingFpsak lagBehandlingAvsluttetDto(){
        return BehandlingFpsak.builder().medStatus("AVSLU").build();
    }
}
