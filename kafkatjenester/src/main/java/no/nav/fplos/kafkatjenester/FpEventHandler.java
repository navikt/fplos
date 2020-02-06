package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FpEventHandler <T extends BehandlingProsessEventDto> {

    private static final Logger log = LoggerFactory.getLogger(FpEventHandler.class);

    private OppgaveRepository oppgaveRepository;

    protected FpEventHandler() {
    }

    protected FpEventHandler(OppgaveRepository oppgaveRepository) {
        this.oppgaveRepository = oppgaveRepository;
    }

    protected OppgaveRepository getOppgaveRepository() {
        return oppgaveRepository;
    }

    protected void loggEvent(UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
    }

    protected Oppgave opprettOppgave(Oppgave oppgave) {
        return oppgaveRepository.opprettOppgave(oppgave);
    }

    protected void loggEvent(UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet, LocalDateTime frist) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet, frist));
    }

    protected void avsluttOppgaveForEksternId(UUID externId) {
        oppgaveRepository.avsluttOppgaveForEksternId(externId);
    }


    /*protected void avsluttOppgaveOgLoggEventVedEksternId(BehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime fristTid){
        Optional<EksternIdentifikator> eksternId = getEksternIdentifikatorRespository().finnIdentifikator(bpeDto.getFagsystem(), bpeDto.getId());
        if(eksternId.isPresent()) {
            avsluttOppgaveForEksternId(eksternId.get().getId());
            loggEvent(eksternId.get().getId(), eventType, AndreKriterierType.UKJENT, bpeDto.getBehandlendeEnhet(), fristTid);
        } else {
            String message = "Fant ikke eksternId som indikerer at der ikke finnes noen oppgave som kan avsluttes.";
            log.warn( message +"Prosesshendelsen hadde ekstern referanse id {} for fagsystemet {}", bpeDto.getId(), bpeDto.getFagsystem() );
            //throw new RuntimeException(message);
        }
    }*/

    protected void avsluttOppgaveOgLoggEvent(UUID eksternId, BehandlingProsessEventDto bpeDto, OppgaveEventType eventType, LocalDateTime frist){
        avsluttOppgaveForEksternId(eksternId);
        loggEvent(eksternId, eventType, null, bpeDto.getBehandlendeEnhet(), frist);
    }

    protected void reserverOppgaveFraTidligereReservasjon(boolean reserverOppgave,
                                                        Reservasjon reservasjon,
                                                        Long oppgaveId) {
        if (reserverOppgave && reservasjon != null) {
            oppgaveRepository.reserverOppgaveFraTidligereReservasjon(oppgaveId, reservasjon);
        }
    }

    protected List<OppgaveEventLogg> hentEventerVedEksternId(UUID eksternId) {
        if (eksternId != null) {
            return getOppgaveRepository().hentEventerForEksternId(eksternId);
        } else return new ArrayList<>();
    }

    protected Oppgave gjenåpneOppgaveVedEksternId(UUID eksternId) {
        return oppgaveRepository.gjenåpneOppgaveForEksternId(eksternId);
    }

    public abstract void prosesser(T bpeDto);
}
