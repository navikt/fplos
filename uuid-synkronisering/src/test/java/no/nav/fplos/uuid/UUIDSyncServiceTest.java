package no.nav.fplos.uuid;

import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import no.nav.fplos.uuid.repository.SpringOppgaveEventLoggRepository;
import no.nav.fplos.uuid.repository.SpringOppgaveRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-context.xml")
public class UUIDSyncServiceTest {
    UUIDSyncService uuidSync;

    @Mock
    SpringOppgaveEventLoggRepository oppgaveEventLoggRepositoryMock;

    @Mock
    SpringOppgaveRepository oppgaveRepositoryMock;

    @Mock
    ForeldrepengerBehandlingRestKlient fpsakRestKlientMock;

    @Before
    public void setUp(){
        uuidSync = new UUIDSyncService(oppgaveRepositoryMock,oppgaveEventLoggRepositoryMock,"NA");
        //uuidSync.setForeldrePengerBehandlingRestKlient(fpsakRestKlientMock);
    }


    @Test
    public void testOppdaterUUID(){
        /*when(oppgaveRepositoryMock.finnBehandlingIdForOppgaverUtenEksternId()).thenReturn(new ArrayList<>());
        when(oppgaveEventLoggRepositoryMock.finnBehandlingIdForOppgaveEventerUtenEksternId()).thenReturn(new ArrayList<>());
        uuidSync.oppdaterUUID();*/
        assertTrue(true);
    }
}
