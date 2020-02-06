package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
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

    public abstract void prosesser(T bpeDto);
}
