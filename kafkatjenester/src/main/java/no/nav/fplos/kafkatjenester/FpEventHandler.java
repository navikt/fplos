package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventLogg;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEventType;
import no.nav.foreldrepenger.loslager.oppgave.Reservasjon;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepositoryProvider;
import no.nav.fplos.kodeverk.KodeverkRepository;
import no.nav.vedtak.felles.integrasjon.kafka.BehandlingProsessEventDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FpEventHandler {

    private static final Logger log = LoggerFactory.getLogger(FpEventHandler.class);

    private KodeverkRepository kodeverkRepository;
    private OppgaveRepository oppgaveRepository;

    protected FpEventHandler() {
    }

    protected FpEventHandler(OppgaveRepositoryProvider oppgaveRepositoryProvider) {
        this.oppgaveRepository = oppgaveRepositoryProvider.getOppgaveRepository();
        this.kodeverkRepository = oppgaveRepositoryProvider.getKodeverkRepository();
    }

    protected OppgaveRepository getOppgaveRepository() {
        return oppgaveRepository;
    }

    protected KodeverkRepository getKodeverkRepository() {
        return kodeverkRepository;
    }

    protected void loggEvent(UUID eksternId, OppgaveEventType oppgaveEventType, AndreKriterierType andreKriterierType, String behandlendeEnhet) {
        oppgaveRepository.lagre(new OppgaveEventLogg(eksternId, oppgaveEventType, andreKriterierType, behandlendeEnhet));
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
    protected void reserverOppgaveFraTidligereReservasjon(boolean reserverOppgave,
                                                          Reservasjon reservasjon,
                                                          Oppgave oppgave) {
        if (reserverOppgave && reservasjon != null) {
            getOppgaveRepository().reserverOppgaveFraTidligereReservasjon(oppgave.getId(), reservasjon);
        }
    }

    protected List<OppgaveEventLogg> hentEventerVedEksternId(UUID eksternId) {
        if (eksternId != null) {
            return getOppgaveRepository().hentEventerForEksternId(eksternId);
        } else return new ArrayList<>();
    }

    /*
        protected Oppgave gjenåpneOppgaveVedEksternId(String fagsystem, String eksternRefId) {
            Optional<EksternIdentifikator> eksternId = eksternIdentifikatorRespository.finnIdentifikator(fagsystem, eksternRefId);
            if(eksternId.isPresent()){
                return oppgaveRepository.gjenåpneOppgaveForEksternId(eksternId.get().getId());
            } else {
                log.debug("Fant ikke eksternId som indikerer at der ikke finnes eksisterende oppgaver som kan gjenåpnes");
                return null;
            }
        }
    */
    public abstract void prosesser(BehandlingProsessEventDto bpeDto);
}
