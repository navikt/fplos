package no.nav.fplos.admin;

import static java.util.stream.Collectors.toList;
import static no.nav.foreldrepenger.loslager.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.loslager.BehandlingId;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.AdminRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingKlient;
import no.nav.vedtak.felles.testutilities.db.EntityManagerAwareTest;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class AdminTjenesteImplTest extends EntityManagerAwareTest {

    private final ForeldrepengerBehandlingKlient foreldrepengerBehandlingKlient = mock(ForeldrepengerBehandlingKlient.class);

    private OppgaveRepository oppgaveRepository;
    private AdminTjenesteImpl adminTjeneste;
    private AvdelingslederTjenesteImpl avdelingslederTjeneste;

    @BeforeEach
    void setUp() {
        var entityManager = getEntityManager();
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var adminRepository = new AdminRepositoryImpl(entityManager);
        var organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
        adminTjeneste = new AdminTjenesteImpl(adminRepository, foreldrepengerBehandlingKlient, organisasjonRepository);
        avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);
    }

    private final Oppgave førstegangOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .medAktiv(true)
            .medBehandlingId(BehandlingId.random())
            .build();
    private final Oppgave klageOppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medBehandlingType(BehandlingType.KLAGE)
            .medAktiv(true)
            .medBehandlingId(BehandlingId.random())
            .build();
    private final Oppgave innsynOppgave = Oppgave.builder()
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
        when(foreldrepengerBehandlingKlient.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandlingDto());
        Oppgave oppgave = adminTjeneste.synkroniserOppgave(førstegangOppgave.getBehandlingId());
        assertThat(oppgave.getAktiv()).isTrue();
    }

    @Test
    public void testOppfriskOppgaveLukket(){
        leggeInnEtSettMedOppgaver();
        when(foreldrepengerBehandlingKlient.getBehandling(any(BehandlingId.class))).thenReturn(lagBehandlingAvsluttetDto());
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
