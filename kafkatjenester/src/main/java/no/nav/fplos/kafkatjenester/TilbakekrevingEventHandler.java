package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);

    private OppgaveRepository oppgaveRepository;

    public TilbakekrevingEventHandler() {
    }

    public TilbakekrevingEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
    }

    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }
    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        //String eksternId = bpeDto

        //List<OppgaveEventLogg> pastOppgaveEvents = oppgaveRepository.hentEventer(behandlingId);

    }
}
