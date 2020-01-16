package no.nav.fplos.verdikjedetester;

import no.nav.foreldrepenger.dbstoette.UnittestRepositoryRule;
import no.nav.foreldrepenger.domene.typer.AktørId;
import no.nav.foreldrepenger.domene.typer.PersonIdent;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.AvdelingslederRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.AvdelingEnhetDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.dto.SaksbehandlerOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.oppgave.AvdelingslederOppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksbehandler.AvdelingslederSaksbehandlerRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.AvdelingslederSakslisteRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteBehandlingstypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteFagsakYtelseTypeDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteOgAvdelingDto;
import no.nav.foreldrepenger.los.web.app.tjenester.avdelingsleder.saksliste.dto.SakslisteSaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.fagsak.app.FagsakApplikasjonTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.OppgaveDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerBrukerIdentDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SaksbehandlerDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteDto;
import no.nav.foreldrepenger.los.web.app.tjenester.felles.dto.SakslisteIdDto;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.oppgave.OppgaveRestTjeneste;
import no.nav.foreldrepenger.los.web.app.tjenester.saksbehandler.saksliste.SaksbehandlerSakslisteRestTjeneste;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static no.nav.foreldrepenger.loslager.BaseEntitet.BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(CdiRunner.class)
public class VerdikjedetestAvdelingslederTest {

    @Rule
    public UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private EntityManager entityManager = repoRule.getEntityManager();
    private OppgaveRepository oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
    private OrganisasjonRepository organisasjonRepository = new OrganisasjonRepositoryImpl(entityManager);
    private TpsTjeneste tpsTjeneste = mock(TpsTjeneste.class);
    private AvdelingslederTjeneste avdelingslederTjeneste = new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository);
    private AnsattTjeneste ansattTjeneste = mock(AnsattTjeneste.class);
    private FagsakApplikasjonTjeneste fagsakApplikasjonTjeneste = mock(FagsakApplikasjonTjeneste.class);
    private OppgaveRestTjeneste oppgaveRestTjeneste = new OppgaveRestTjeneste(new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste), fagsakApplikasjonTjeneste);
    private AvdelingslederRestTjeneste avdelingslederRestTjeneste = new AvdelingslederRestTjeneste(new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository));
    private AvdelingslederSakslisteRestTjeneste avdelingslederSakslisteRestTjeneste = new AvdelingslederSakslisteRestTjeneste(
            new AvdelingslederTjenesteImpl(oppgaveRepository, organisasjonRepository), new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste));
    private AvdelingslederSaksbehandlerRestTjeneste avdelingslederSaksbehandlerRestTjeneste =
            new AvdelingslederSaksbehandlerRestTjeneste(new AvdelingslederSaksbehandlerTjenesteImpl(oppgaveRepository, organisasjonRepository, new OrganisasjonRessursEnhetTjenesteImpl()));
    private SaksbehandlerSakslisteRestTjeneste saksbehandlerSakslisteRestTjeneste =
            new SaksbehandlerSakslisteRestTjeneste(new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste));
    private AvdelingslederOppgaveRestTjeneste avdelingslederOppgaveRestTjeneste =
            new AvdelingslederOppgaveRestTjeneste(new OppgaveTjenesteImpl(oppgaveRepository, organisasjonRepository, tpsTjeneste, avdelingslederTjeneste, ansattTjeneste));
    private ForeldrepengerBehandlingRestKlient foreldrepengerBehandlingRestKlient = mock(ForeldrepengerBehandlingRestKlient.class);

    @Inject
    private AksjonspunktMeldingConsumer meldingConsumer;
    private KafkaReader kafkaReader = null;
    private AvdelingDto avdelingDrammen = null;
    private AvdelingDto avdelingStord = null;
    private String AVDELING_DRAMMEN = "4806";
    private String AVDELING_STORD = "4842";
    private String SAKSBEHANDLER_IDENT = BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    private String SAKSBEHANDLER_IDENT_2 = "IDENT2";

    @Before
    public void before(){
        kafkaReader = new KafkaReader(meldingConsumer,
                new FpsakEventHandler(oppgaveRepository, foreldrepengerBehandlingRestKlient),
                new TilbakekrevingEventHandler(oppgaveRepository),
                oppgaveRepository);
        entityManager.flush();
        avdelingDrammen = avdelingslederRestTjeneste.hentAvdelinger().stream()
                .filter(avdeling -> AVDELING_DRAMMEN.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();
        oppgaveRepository.hentAlleLister(avdelingDrammen.getId()).forEach(liste -> entityManager.remove(liste));
        lagEnkeltMockResultatForTpsTjenesten();
        MockKafkaMessages.clearMessages();
        MockEventKafkaMessages.clearMessages();
    }

    @Test
    public void avdelingsederLeggeTilRedigereSlett() {
        Map<Long, MeldingsTestInfo> samledeMeldinger = new HashMap<>();
        samledeMeldinger.putAll(MockKafkaMessages.førstegangsbehandlingMeldinger);
        samledeMeldinger.putAll(MockKafkaMessages.innsynMeldinger);
        MockKafkaMessages.sendNyeOppgaver(samledeMeldinger);
        when(foreldrepengerBehandlingRestKlient.getBehandling(anyLong())).thenReturn(lagBehandlingDto());
        kafkaReader.hentOgLagreMeldingene();

        SakslisteIdDto sakslisteIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));

        verifiserFinnesISaksliste(samledeMeldinger,sakslisteIdDto);

        //Setter begrensing slik at Innsyn ikke skal komme med i lista.
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteIdDto, BehandlingType.FØRSTEGANGSSØKNAD, true,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        verifiserFinnesISaksliste(MockKafkaMessages.førstegangsbehandlingMeldinger,sakslisteIdDto);

        //Setter begrensing slik at Foreldrepenger ikke skal komme med i lista. (ingen igjen)
        avdelingslederSakslisteRestTjeneste.lagreFagsakYtelseType(new SakslisteFagsakYtelseTypeDto(sakslisteIdDto, FagsakYtelseType.ENGANGSTØNAD, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        verifiserAtSakslisteErTom(sakslisteIdDto);

        //Fjerner begrensing slik at Foreldrepenger skal komme med i lista igjen.
        avdelingslederSakslisteRestTjeneste.lagreFagsakYtelseType(new SakslisteFagsakYtelseTypeDto(sakslisteIdDto, null, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        verifiserFinnesISaksliste(MockKafkaMessages.førstegangsbehandlingMeldinger,sakslisteIdDto);

        //Fjerner begrensing slik at Innsyn skal komme med i lista igjen.
        avdelingslederSakslisteRestTjeneste.lagreBehandlingstype(new SakslisteBehandlingstypeDto(sakslisteIdDto, BehandlingType.FØRSTEGANGSSØKNAD, false,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        verifiserFinnesISaksliste(samledeMeldinger,sakslisteIdDto);

        avdelingslederSakslisteRestTjeneste.slettSaksliste(new SakslisteOgAvdelingDto(sakslisteIdDto, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        assertThat(avdelingslederSakslisteRestTjeneste.hentAvdelingensSakslister(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()))).isEmpty();
    }

    @Test
    public void avdelingslederKnytteFjerne(){
        SakslisteIdDto sakslisteIdDto = avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));

        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT),true,  new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT_2), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT_2),true,new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        List<SakslisteDto> sakslisteDtos = saksbehandlerSakslisteRestTjeneste.hentSakslister();
        assertThat(sakslisteDtos).hasSize(1);
        assertThat(sakslisteDtos.get(0).getSaksbehandlerIdenter()).hasSize(2);

        when(ansattTjeneste.hentAnsattNavn(SAKSBEHANDLER_IDENT)).thenReturn("Navn 1");
        when(ansattTjeneste.hentAnsattNavn(SAKSBEHANDLER_IDENT_2)).thenReturn("Navn 2");
        when(ansattTjeneste.hentAvdelingerNavnForAnsatt(SAKSBEHANDLER_IDENT)).thenReturn(Collections.emptyList());
        when(ansattTjeneste.hentAvdelingerNavnForAnsatt(SAKSBEHANDLER_IDENT_2)).thenReturn(Collections.emptyList());

        List<SaksbehandlerDto> saksbehandleriSaksliste = saksbehandlerSakslisteRestTjeneste.hentSakslistensSaksbehandlere(sakslisteIdDto);
        assertThat(saksbehandleriSaksliste).hasSize(2);

        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT_2),false, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSakslisteRestTjeneste.leggSaksbehandlerTilSaksliste(new SakslisteSaksbehandlerDto(sakslisteIdDto, new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT),false, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        List<SakslisteDto> nySakslisteDtos = saksbehandlerSakslisteRestTjeneste.hentSakslister();
        assertThat(nySakslisteDtos).hasSize(0);

        List<SaksbehandlerDto> nySaksbehandleriSaksliste = saksbehandlerSakslisteRestTjeneste.hentSakslistensSaksbehandlere(sakslisteIdDto);
        assertThat(nySaksbehandleriSaksliste).hasSize(0);

    }

    @Test
    public void avdelingslederLeggeTilSaksbehandlerSlettSaksbehandler(){
        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSaksbehandlerRestTjeneste.leggTilNySaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT_2), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        assertThat(avdelingslederSaksbehandlerRestTjeneste.hentAvdelingensSaksbehandlere(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()))).hasSize(2);

        avdelingslederSaksbehandlerRestTjeneste.slettSaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));
        avdelingslederSaksbehandlerRestTjeneste.slettSaksbehandler(new SaksbehandlerOgAvdelingDto(new SaksbehandlerBrukerIdentDto(SAKSBEHANDLER_IDENT_2), new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet())));

        assertThat(avdelingslederSaksbehandlerRestTjeneste.hentAvdelingensSaksbehandlere(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()))).hasSize(0);
    }

    @Test
    public void avdelingslederOppgavefiltreringTilknyttetAvdeling(){
        avdelingStord = avdelingslederRestTjeneste.hentAvdelinger().stream()
                .filter(avdeling -> AVDELING_STORD.equals(avdeling.getAvdelingEnhet())).findFirst().orElseThrow();

        avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        avdelingslederSakslisteRestTjeneste.opprettNySaksliste(new AvdelingEnhetDto(avdelingStord.getAvdelingEnhet()));

        assertThat(avdelingslederSakslisteRestTjeneste.hentAvdelingensSakslister(new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()))).hasSize(2);
        assertThat(avdelingslederSakslisteRestTjeneste.hentAvdelingensSakslister(new AvdelingEnhetDto(avdelingStord.getAvdelingEnhet()))).hasSize(1);
    }

    private void verifiserAtSakslisteErTom(SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        assertThat(verifiserAtEksaktFinnes(sakslisteIdDtoForSaksb, MockKafkaMessages.tomtMap, oppgaverTilBehandling)).isNull();
    }

    private OppgaveDto verifiserFinnesISaksliste(Map<Long, MeldingsTestInfo> meldinger, SakslisteIdDto sakslisteIdDtoForSaksb) {
        List<OppgaveDto> oppgaverTilBehandling = oppgaveRestTjeneste.getOppgaverTilBehandling(sakslisteIdDtoForSaksb);
        return verifiserAtEksaktFinnes(sakslisteIdDtoForSaksb, meldinger, oppgaverTilBehandling);
    }

    private OppgaveDto verifiserAtEksaktFinnes(SakslisteIdDto sakslisteIdDto, Map<Long, MeldingsTestInfo> meldinger, List<OppgaveDto> oppgaverTilBehandling) {
        assertThat(oppgaverTilBehandling).withFailMessage("Oppgavene til behandling antall '" + oppgaverTilBehandling.size() +
                "' har ikke samme antall som meldingene antall '" + Math.min(meldinger.size(), 3) + "'")
                .hasSize(Math.min(meldinger.size(), 3));
        Integer antallOppgaverForSaksliste = avdelingslederOppgaveRestTjeneste.hentAntallOppgaverForSaksliste(sakslisteIdDto, new AvdelingEnhetDto(avdelingDrammen.getAvdelingEnhet()));
        assertThat(antallOppgaverForSaksliste.intValue()).withFailMessage("AntallOppgaverForSaksliste gir antallet " +
                antallOppgaverForSaksliste + " mens antall fra meldinger er " + meldinger.size())
                .isEqualTo(meldinger.size());
        for (OppgaveDto oppgave : oppgaverTilBehandling) {
            assertThat(meldinger.get(oppgave.getBehandlingId())).withFailMessage("Finner ikke oppgaven med behandlingId:" + oppgave.getBehandlingId() + " i settet funnet i databasen").isNotNull();
            meldinger.get(oppgave.getBehandlingId()).sammenligne(oppgave);
        }
        if (oppgaverTilBehandling.isEmpty()) return null;
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
        return BehandlingFpsak.builder()
                .medAksjonspunkter(List.of(new Aksjonspunkt.Builder().medDefinisjon(Aksjonspunkt.TIL_BESLUTTER_KODE).medStatus(Aksjonspunkt.STATUSKODE_AKTIV).build()))
                .medBehandlendeEnhetNavn("NAV")
                .medStatus("-")
                .build();
    }
}
