package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import no.nav.vedtak.felles.jpa.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Transaction
public class TilbakekrevingEventHandler extends FpEventHandler {
    private static final Logger log = LoggerFactory.getLogger(TilbakekrevingEventHandler.class);

    public TilbakekrevingEventHandler() {
    }

    @Inject
    public TilbakekrevingEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        super(oppgaveRepositoryProvider);
    }

    @Override
    public void prosesser(BehandlingProsessEventDto bpeDto){
        prosesser(bpeDto, null,false);
    }
    private void prosesser(BehandlingProsessEventDto bpeDto, Reservasjon reservasjon, boolean prosesserFraAdmin) {
        //String eksternRefId = bpeDto.getId();
        //EksternIdentifikator eksternId = getEksternIdentifikatorRespository().finnEllerOpprettEksternId(bpeDto.getFagsystem(), eksternRefId);


        //List<OppgaveEventLogg> pastOppgaveEvents = oppgaveRepository.hentEventer(behandlingId);

    }
}
