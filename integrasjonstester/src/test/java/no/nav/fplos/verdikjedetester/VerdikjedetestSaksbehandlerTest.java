package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.dto.OppgaveOpphevingDto;
import no.nav.foreldrepenger.loslager.aktør.TpsPersonDto;
import no.nav.foreldrepenger.loslager.oppgave.BehandlingType;
import no.nav.foreldrepenger.loslager.oppgave.FagsakYtelseType;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryImpl;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepository;
import no.nav.foreldrepenger.loslager.repository.OrganisasjonRepositoryImpl;
import no.nav.fplos.ansatt.AnsattTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederSaksbehandlerTjenesteImpl;
import no.nav.fplos.avdelingsleder.AvdelingslederTjeneste;
import no.nav.fplos.avdelingsleder.AvdelingslederTjenesteImpl;
import no.nav.fplos.domene.organisasjonsinformasjon.organisasjonressursenhet.impl.OrganisasjonRessursEnhetTjenesteImpl;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;
import no.nav.fplos.foreldrepengerbehandling.BehandlingFpsak;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.kafkatjenester.AksjonspunktMeldingConsumer;
import no.nav.fplos.kafkatjenester.FpsakEventHandler;
import no.nav.fplos.kafkatjenester.KafkaReader;
import no.nav.fplos.kafkatjenester.TilbakekrevingEventHandler;
import no.nav.fplos.oppgave.OppgaveTjenesteImpl;
import no.nav.fplos.person.api.TpsTjeneste;
import no.nav.fplos.verdikjedetester.mock.MeldingsTestInfo;
import no.nav.fplos.verdikjedetester.mock.MockEventKafkaMessages;
import no.nav.fplos.verdikjedetester.mock.MockKafkaMessages;
import no.nav.vedtak.felles.testutilities.cdi.CdiRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.temporal.ChronoUnit.MINUTES;
import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class VerdikjedetestSaksbehandlerTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = mock(AvdelingslederTjeneste.class);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
    private OppgaveRestTjeneste oppgaveRestTjeneste = new OppgaveRestTjeneste(new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste), fagsakApplikasjonTjeneste);
    private AvdelingslederRestTjeneste avdelingslederRestTjeneste = new AvdelingslederRestTjeneste(new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository));
    private AvdelingslederSakslisteRestTjeneste avdelingslederSakslisteRestTjeneste = new AvdelingslederSakslisteRestTjeneste(
            new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository), new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste));
    private AvdelingslederSaksbehandlerRestTjeneste avdelingslederSaksbehandlerRestTjeneste =
            new AvdelingslederSaksbehandlerRestTjeneste(new AvdelingslederSaksbehandlerTjenesteImpl(oppgaveRepository, organisasjonRepository, new OrganisasjonRessursEnhetTjenesteImpl()));
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);

    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;
    private AvdelingDto avdelingDrammen = null;
    private static final String AVDELING_DRAMMEN = "4806";
    private static final String SAKSBEHANDLER_IDENT = BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    SakslisteIdDto sakslisteDrammenFPFørstegangsIdDto;

    @Before
    public void before(){
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient),
                new TilbakekrevingEventHandler(oppgaveRepository), oppgaveRepository);
        avdelingDrammen = avdelingslederRestTjeneste.hentAvdelinger().stream()
                .filter(avdeling -> AVDELING_DRAMMEN.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        sakslisteDrammenFPFørstegangsIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.lagreFagsakYtelseType(new SakslisteFagsakYtelseTypeDto(sakslisteDrammenFPFørstegangsIdDto, FagsakYtelseType.FORELDREPENGER, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteDrammenFPFørstegangsIdDto, BehandlingType.FØRSTEGANGSSØKNAD, true, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteDrammenFPFørstegangsIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT),true, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        lagEnkeltMockResultatForTpsTjenesten();
        MockKafkaMessages.clearMessages();
        MockEventKafkaMessages.clearMessages();
    }

    @Test
    public void saksbehandlerRutingReservasjon(){
        Map<Long, MeldingsTestInfo> melding = MockKafkaMessages.defaultførstegangsbehandlingMelding;
        MockKafkaMessages.sendNyeOppgaver(melding);
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgaveDto = verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        verifiserAtReservertErTom();

        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));

        verifiserAtSakslisteErTom(sakslisteDrammenFPFørstegangsIdDto);
        assertThat(verifiserAtErReservert(melding).getStatus().getReservertTilTidspunkt().until(LocalDateTime.now().plusHours(2), MINUTES)).isLessThan(2L);

        oppgaveRestTjeneste.forlengOppgaveReservasjon(new OppgaveIdDto(oppgaveDto.getId()));

        assertThat(verifiserAtErReservert(melding).getStatus().getReservertTilTidspunkt().until(LocalDateTime.now().plusHours(24), MINUTES)).isLessThan(2L);

        oppgaveRestTjeneste.opphevOppgaveReservasjon(new OppgaveOpphevingDto(new OppgaveIdDto(oppgaveDto.getId()),"Begrunnelse"));

        verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        verifiserAtReservertErTom();
    }

    @Test
    public void saksbehandlerForskjelligeReservasjonslister() {
        Map<Long, MeldingsTestInfo> melding = MockKafkaMessages.defaultførstegangsbehandlingMelding;
        MockKafkaMessages.sendNyeOppgaver(melding);
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());
        kafkaReader.hentOgLagreMeldingene();

        OppgaveDto oppgaveDto = verifiserFinnesISaksliste(melding, sakslisteDrammenFPFørstegangsIdDto);
        oppgaveRestTjeneste.reserverOppgave(new OppgaveIdDto(oppgaveDto.getId()));

        verifiserAtErReservert(melding);

        //Forandrer alle reservasjonene til ANNEN_IDENT
        entityManager.createNativeQuery("UPDATE RESERVASJON r set r.RESERVERT_AV ='ANNEN_IDENT'").executeUpdate();

        verifiserAtReservertErTom();
        verifiserAtSakslisteErTom(sakslisteDrammenFPFørstegangsIdDto);
    }

    @Test
    public void sakbehandlerBytteListe() {
        //Prepare
        SakslisteIdDto sakslisteInnsynIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteInnsynIdDto, BehandlingType.INNSYN, true, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());

        //Act
        MockKafkaMessages.sendNyeOppgaver(MockKafkaMessages.førstegangsbehandlingMeldinger);
        MockKafkaMessages.sendNyeOppgaver(MockKafkaMessages.innsynMeldinger);
        kafkaReader.hentOgLagreMeldingene();

        verifiserFinnesISaksliste(MockKafkaMessages.førstegangsbehandlingMeldinger, sakslisteDrammenFPFørstegangsIdDto);
        verifiserFinnesISaksliste(MockKafkaMessages.innsynMeldinger, sakslisteInnsynIdDto);
    }

    private void verifiserAtReservertErTom() {
        List<OppgaveDto> reserverteOppgaver = oppgaveRestTjeneste.getReserverteOppgaver();
        assertThat(verifiserAtEksaktFinnes(MockKafkaMessages.tomtMap, reserverteOppgaver)).isNull();
    }

    private void verifiserAtSakslisteErTom(SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        assertThat(verifiserAtEksaktFinnes(MockKafkaMessages.tomtMap, oppgaverTilBehandling)).isNull();
    }

    private OppgaveDto verifiserAtErReservert(Map<Long, MeldingsTestInfo> meldinger) {
        List<OppgaveDto> reserverteOppgaver = oppgaveRestTjeneste.getReserverteOppgaver();
        return verifiserAtEksaktFinnes(meldinger, reserverteOppgaver);
    }

    private OppgaveDto verifiserFinnesISaksliste(Map<Long, MeldingsTestInfo> meldinger, SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        return verifiserAtEksaktFinnes(meldinger, oppgaverTilBehandling);
    }

    private OppgaveDto verifiserAtEksaktFinnes(Map<Long, MeldingsTestInfo> meldinger, List<OppgaveDto> oppgaverTilBehandling) {
        assertThat(oppgaverTilBehandling).withFailMessage("Oppgavene til behandling har ikke samme antall som meldingene").hasSize(meldinger.size());
        for (OppgaveDto oppgave :oppgaverTilBehandling) {
            assertThat(meldinger.get(oppgave.getBehandlingId())).withFailMessage("Finner ikke oppgaven med behandlingId:"+oppgave.getBehandlingId()+" i settet funnet i databasen").isNotNull();
            meldinger.get(oppgave.getBehandlingId()).sammenligne(oppgave);
        }
        if (oppgaverTilBehandling.isEmpty())return null;
        return oppgaverTilBehandling.get(0);
    }


    private void lagEnkeltMockResultatForTpsTjenesten() {
        TpsPersonDto personDto = new TpsPersonDto.Builder()
                .medAktørId(new AktørId(3L))
                .medFnr(PersonIdent.fra("12345678901"))
                .medNavn("Test")
                .medFødselsdato(LocalDate.now())
                .medNavBrukerKjønn("NA")
                .build();
        when(tpsTjeneste.hentBrukerForAktør(new AktørId(3L))).thenReturn(Optional.of(personDto));
    }

    private BehandlingFpsak lagBehandlingDto(){
        Aksjonspunkt aksjonspunkt = Aksjonspunkt.builder().medStatus(Aksjonspunkt.STATUSKODE_AKTIV).build();
        return BehandlingFpsak.builder().medBehandlendeEnhetNavn("NAV").medStatus("-").medAksjonspunkter(Collections.singletonList(aksjonspunkt)).medBehandlingstidFrist(LocalDate.now()).build();
    }
}
