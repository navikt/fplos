package no.nav.foreldrepenger.los.admin;

import static no.nav.foreldrepenger.los.organisasjon.Avdeling.AVDELING_DRAMMEN_ENHET;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import javax.persistence.EntityManager;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.domene.typer.Saksnummer;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepository;
import no.nav.foreldrepenger.los.oppgave.OppgaveRepositoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import no.nav.foreldrepenger.extensions.EntityManagerFPLosAwareExtension;
import no.nav.foreldrepenger.los.oppgave.BehandlingType;
import no.nav.foreldrepenger.los.oppgave.Oppgave;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventLogg;
import no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.oppgaveeventlogg.OppgaveEventType;

@ExtendWith(EntityManagerFPLosAwareExtension.class)
public class AdminTjenesteImplTest {

    private OppgaveRepository oppgaveRepository;
    private AdminTjenesteImpl adminTjeneste;

    @BeforeEach
    void setUp(EntityManager entityManager) {
        oppgaveRepository = new OppgaveRepositoryImpl(entityManager);
        var adminRepository = new AdminRepositoryImpl(entityManager);
        adminTjeneste = new AdminTjenesteImpl(adminRepository);
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
            .medAktiv(false)
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
        oppgaveRepository.lagre(innsynOppgave);
        oppgaveRepository.lagre(klageOppgave);
    }

    @Test
    public void testHentOppgave(){
        leggeInnEtSettMedOppgaver();
        List<Oppgave> oppgave = adminTjeneste.hentOppgaver(new Saksnummer("3478293"));
        assertThat(oppgave).containsExactly(innsynOppgave, førstegangOppgave, klageOppgave);
    }

    @Test
    public void testHentEvent(){
        oppgaveRepository.lagre(new OppgaveEventLogg(førstegangOppgave.getBehandlingId(), OppgaveEventType.OPPRETTET, null, null));
        List<OppgaveEventLogg> oppgave = adminTjeneste.hentEventer(førstegangOppgave.getBehandlingId());
        assertThat(oppgave).isNotEmpty();
    }

}
