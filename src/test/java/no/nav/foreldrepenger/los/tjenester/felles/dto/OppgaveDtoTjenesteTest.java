package no.nav.foreldrepenger.los.tjenester.felles.dto;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import no.nav.foreldrepenger.los.oppgave.BehandlingTjeneste;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import jakarta.persistence.EntityManager;
import no.nav.foreldrepenger.los.JpaExtension;
import no.nav.foreldrepenger.los.domene.typer.Fagsystem;
import no.nav.foreldrepenger.los.domene.typer.aktør.Fødselsnummer;
import no.nav.foreldrepenger.los.domene.typer.aktør.Person;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveTjeneste;
import no.nav.foreldrepenger.los.oppgavekø.OppgaveKøTjeneste;
import no.nav.foreldrepenger.los.organisasjon.ansatt.AnsattTjeneste;
import no.nav.foreldrepenger.los.persontjeneste.PersonTjeneste;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonRepository;
import no.nav.foreldrepenger.los.reservasjon.ReservasjonTjeneste;
import no.nav.foreldrepenger.los.server.abac.TilgangFilterKlient;

@ExtendWith(JpaExtension.class)
class OppgaveDtoTjenesteTest {

    private final TilgangFilterKlient tilgangFilterklient = mock(TilgangFilterKlient.class);
    private final PersonTjeneste personTjeneste = mock(PersonTjeneste.class);

    private OppgaveRepository oppgaveRepository;
    private OppgaveTjeneste oppgaveTjeneste;
    private OppgaveDtoTjeneste oppgaveDtoTjeneste;
    private ReservasjonTjeneste reservasjonTjeneste;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        var ansattTjeneste = mock(AnsattTjeneste.class);
        var reservasjonStatusDtoTjeneste = new ReservasjonStatusDtoTjeneste(ansattTjeneste);
        var reservasjonRepository = new ReservasjonRepository(entityManager);
        this.oppgaveRepository = new OppgaveRepository(entityManager);
        this.reservasjonTjeneste = new ReservasjonTjeneste(oppgaveRepository, reservasjonRepository, new BehandlingTjeneste(oppgaveRepository));
        this.oppgaveTjeneste = new OppgaveTjeneste(oppgaveRepository, reservasjonTjeneste);
        this.oppgaveDtoTjeneste = new OppgaveDtoTjeneste(oppgaveTjeneste, reservasjonTjeneste, personTjeneste, reservasjonStatusDtoTjeneste, mock(
            OppgaveKøTjeneste.class), tilgangFilterklient);
    }

    /*
    // TODO: fiks test før merging
    @Test
    void skalHenteSisteReserverteOppgaverMedStatus() {
        // Testen kjører i bunn relativt komplisert native query for å hente siste reserverte oppgaveId-referanser med et par datafelter brukt i
        // utledning av status i ReservasjonTjeneste. Tilgangskontroll og mapping til DTO skjer i OppgaveDtoTjeneste.

        when(tilgangFilterklient.tilgangFilterSaker(anyList())).thenAnswer(invocation -> {
            List<Oppgave> oppgaver = invocation.getArgument(0);
            return oppgaver.stream().map(Oppgave::getSaksnummer).collect(Collectors.toSet());
        });

        when(personTjeneste.hentPerson(any(), any(), any())).thenReturn(
            Optional.of(new Person(new Fødselsnummer("1233456789"), "Navn Navnesen")));

        var oppgave = Oppgave.builder()
            .dummyOppgave(AVDELING_DRAMMEN_ENHET)
            .medSystem(Fagsystem.FPSAK)
            .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
            .build();
        oppgave.leggTilOppgaveEgenskap(OppgaveEgenskap.builder()
            .medAndreKriterierType(AndreKriterierType.TIL_BESLUTTER)
            .medSisteSaksbehandlerForTotrinn("IDENT")
            .build());
        oppgaveRepository.lagre(oppgave);
        oppgaveRepository.lagre(new OppgaveEventLogg(oppgave.getBehandlingId(), OppgaveEventType.OPPRETTET,
            AndreKriterierType.TIL_BESLUTTER, oppgave.getBehandlendeEnhet()));
        reservasjonTjeneste.reserverOppgave(oppgave);

        var sisteReserverteEtterReservasjon = oppgaveDtoTjeneste.getSaksbehandlersSisteReserverteOppgaver(false);
        assertThat(sisteReserverteEtterReservasjon)
            .hasSize(1)
            .first().matches(dto -> dto.getOppgaveBehandlingStatus() == OppgaveBehandlingStatus.TIL_BESLUTTER);

        oppgaveTjeneste.avsluttOppgaveMedEventLogg(oppgave, OppgaveEventType.LUKKET);

        var sisteReserverte = oppgaveDtoTjeneste.getSaksbehandlersSisteReserverteOppgaver(false);
        assertThat(sisteReserverte)
            .hasSize(1)
            .first().matches(dto -> dto.getOppgaveBehandlingStatus() == OppgaveBehandlingStatus.FERDIG);
    }
     */

}
