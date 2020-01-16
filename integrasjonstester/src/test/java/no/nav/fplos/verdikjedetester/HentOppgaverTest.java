package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.EventmottakFeillogg;
import no.nav.foreldrepenger.loslager.oppgave.KøSortering;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveFiltrering;
import no.nav.foreldrepenger.loslager.organisasjon.Avdeling;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import no.nav.fplos.oppgave.OppgaveTjeneste;
import no.nav.fplos.oppgave.OppgaveTjenesteImpl;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.fplos.verdikjedetester.mock.MockKafkaMessages;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class HentOppgaverTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(repoRule.getEntityManager());
    private OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(repoRule.getEntityManager());
    private EntityManager entityManager = repoRule.getEntityManager();
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = mock(AvdelingslederTjeneste.class);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private OppgaveTjeneste oppgaveTjeneste = new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste);
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
    private OppgaveRestTjeneste oppgaveRestTjeneste = new OppgaveRestTjeneste(oppgaveTjeneste, fagsakApplikasjonTjeneste);
    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;
    private Avdeling avdelingDrammen = null;
    private OppgaveFiltrering oppgaveFiltrering;
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);

    @Before
    public void before(){
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient),
                new TilbakekrevingEventHandler(oppgaveRepository),oppgaveRepository);
        List<Avdeling> avdelings = repoRule.getRepository().hentAlle(Avdeling.class);
        avdelingDrammen = avdelings.stream().filter(avdeling -> Avdeling.AVDELING_DRAMMEN_ENHET.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        oppgaveFiltrering = OppgaveFiltrering.builder().medNavn("FRIST").medSortering(KøSortering.BEHANDLINGSFRIST).medAvdeling(avdelingDrammen).build();
        entityManager.persist(oppgaveFiltrering);
        entityManager.flush();

        TpsPersonDto personDto = new TpsPersonDto.Builder()
                .medAktørId(new AktørId(3L))
                .medFnr(PersonIdent.fra("12345678901"))
                .medNavn("Test")
                .medFødselsdato(LocalDate.now())
                .medNavBrukerKjønn("NA")
                .build();
        when(tpsTjeneste.hentBrukerForAktør(new AktørId(3L))).thenReturn(Optional.of(personDto));
        MockKafkaMessages.clearMessages();
    }

    @Ignore
    @Test
    public void hentNyeOppgaver(){

        MockKafkaMessages.sendNyeOppgaver(MockKafkaMessages.førstegangsbehandlingMeldinger);
        kafkaReader.hentOgLagreMeldingene();

        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(new SakslisteIdDto(oppgaveFiltrering.getId()));

        assertThat(oppgaverTilBehandling).hasSize(3);
        assertThat(oppgaverTilBehandling)
                .extracting(OppgaveDto::getBehandlingId)
                .containsAll(MockKafkaMessages.førstegangsbehandlingMeldinger.values().stream()
                        .map(m -> m.getBehandlingId())
                        .collect(Collectors.toList()));
        oppgaverTilBehandling.forEach(oppgave -> MockKafkaMessages.førstegangsbehandlingMeldinger.get(oppgave.getBehandlingId())
                .sammenligne(oppgave));

        MockKafkaMessages.clearMessages();
        MockKafkaMessages.sendAvsluttetFørstegangsbehandlingOppgave(1L);
        kafkaReader.hentOgLagreMeldingene();

        List<OppgaveDto> oppgaverEtterAvsluttet= oppgaveRestTjeneste.getOppgaverTilBehandling(new SakslisteIdDto(oppgaveFiltrering.getId()));
        assertThat(oppgaverEtterAvsluttet).hasSize(MockKafkaMessages.førstegangsbehandlingMeldinger.size()-1);
    }

    @Ignore
    @Test
    public void testOppgaveBlirReservertOgReservasjonenFrigjortEtterAvsluttetOppgave() {
        MockKafkaMessages.sendNyeOppgaver(MockKafkaMessages.førstegangsbehandlingMeldinger);
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgave = oppgaveRestTjeneste.getOppgaverTilBehandling(new SakslisteIdDto(oppgaveFiltrering.getId())).get(0);
        oppgaveTjeneste.reserverOppgave(oppgave.getId());
        List<OppgaveDto> oppgaverEtteReservasjon = oppgaveRestTjeneste.getOppgaverTilBehandling(new SakslisteIdDto(oppgaveFiltrering.getId()));
        assertThat(oppgaverEtteReservasjon).extracting(OppgaveDto::getBehandlingId).doesNotContain(oppgave.getBehandlingId());

        List<OppgaveDto> reservasjons = oppgaveRestTjeneste.getReserverteOppgaver();
        assertThat(reservasjons).hasSize(1);
        assertThat(reservasjons.get(0).getBehandlingId()).isEqualTo(oppgave.getBehandlingId());

        MockKafkaMessages.clearMessages();
        MockKafkaMessages.sendAvsluttetFørstegangsbehandlingOppgave(oppgave.getId());
        kafkaReader.hentOgLagreMeldingene();

        List<OppgaveDto> reservasjonsEtterAvsluttetOppgave = oppgaveRestTjeneste.getReserverteOppgaver();
        assertThat(reservasjonsEtterAvsluttetOppgave).isEmpty();
    }

    @Test
    public void testMelding() {
        String string = "{\"fagsystem\":\"FPSAK\",\"behandlingId\":1076755,\"saksnummer\":\"135702047\",\"aktørId\":\"1000104491938\",\"eventHendelse\":\"aksjonspunkt_opprettet\",\"behandlinStatus\":\"UTRED\",\"behandlingSteg\":\"SØKNADSFRIST_FP\",\"behandlendeEnhet\":\"4802\",\"ytelseTypeKode\":\"FP\",\"behandlingTypeKode\":\"BT-002\",\"opprettetBehandling\":\"2018-12-03T15:01:16\",\"aksjonspunktKoderMedStatusListe\":{\"5020\":\"UTFO\",\"7010\":\"UTFO\",\"5043\":\"OPPR\",\"7003\":\"UTFO\",\"5027\":\"UTFO\"}}";
        MockKafkaMessages.sendEvent(string);
        kafkaReader.hentOgLagreMeldingene();
        List<Oppgave> oppgaves = repoRule.getRepository().hentAlle(Oppgave.class);
        List<OppgaveEgenskap> oppgaveEgenskaps = repoRule.getRepository().hentAlle(OppgaveEgenskap.class);
        List<OppgaveEventLogg> oppgaveEventLoggs = repoRule.getRepository().hentAlle(OppgaveEventLogg.class);
        List<EventmottakFeillogg> eventmottakFeilloggs = repoRule.getRepository().hentAlle(EventmottakFeillogg.class);
    }
}
