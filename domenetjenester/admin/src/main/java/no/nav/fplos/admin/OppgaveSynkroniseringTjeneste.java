package no.nav.fplos.admin;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.foreldrepenger.loslager.oppgave.Oppgave;
import no.nav.foreldrepenger.loslager.oppgave.OppgaveEgenskap;
import no.nav.foreldrepenger.loslager.repository.OppgaveRepository;
import no.nav.fplos.foreldrepengerbehandling.ForeldrepengerBehandlingRestKlient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.List;

import static no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType.BERØRT_BEHANDLING;

@RequestScoped
public class OppgaveSynkroniseringTjeneste {
    private static final Logger log = LoggerFactory.getLogger(OppgaveSynkroniseringTjeneste.class);
    private OppgaveRepository oppgaveRepository;
    private ForeldrepengerBehandlingRestKlient fpsakKlient;

    OppgaveSynkroniseringTjeneste() {
        // for CDI proxy
    }

    @Inject
    public OppgaveSynkroniseringTjeneste(ForeldrepengerBehandlingRestKlient fpsakKlient,
                                         OppgaveRepository oppgaveRepository) {
        this.fpsakKlient = fpsakKlient;
        this.oppgaveRepository = oppgaveRepository;
    }

    public void leggTilBerørtBehandlingEgenskap() {
        List<Oppgave> oppgaver = oppgaveRepository.hentOppgaverForSynkronisering();
        log.info("Hentet {} oppgaver for synkronisering", oppgaver.size());
        for (var oppgave : oppgaver) {
            try {
                lagreAktivEgenskap(oppgave);
            } catch (RuntimeException e) {
                log.info("Feil under synkronisering for oppgaveId {} med message {} ", oppgave.getId(), e.getMessage());
            }
        }
    }

    private void lagreAktivEgenskap(Oppgave oppgave) {
        List<OppgaveEgenskap> egenskaper = oppgaveRepository.hentOppgaveEgenskaper(oppgave.getId());
        if (eksistererAktivEgenskap(egenskaper, BERØRT_BEHANDLING)) {
            return; // that was easy
        }
        var behandling = fpsakKlient.getBehandling(oppgave.getBehandlingId());
        if (oppgave.getAktiv() && behandling.erBerørtBehandling()) {
            var aktivEgenskap = lagAktivEgenskap(oppgave, egenskaper);
            lagre(aktivEgenskap);
        }
    }

    @Transactional
    private void lagre(OppgaveEgenskap egenskap) {
        oppgaveRepository.lagre(egenskap);
    }

    private static OppgaveEgenskap lagAktivEgenskap(Oppgave oppgave, List<OppgaveEgenskap> eksisterendeEgenskaper) {
        OppgaveEgenskap egenskap = eksisterendeEgenskaper.stream()
                .filter(oe -> oe.getAndreKriterierType().equals(BERØRT_BEHANDLING))
                .findAny()
                .orElseGet(() -> new OppgaveEgenskap(oppgave, BERØRT_BEHANDLING));
        egenskap.aktiverOppgaveEgenskap();
        return egenskap;
    }

    private static boolean eksistererAktivEgenskap(List<OppgaveEgenskap> oppgaver, AndreKriterierType type) {
        return oppgaver.stream()
                .filter(OppgaveEgenskap::getAktiv)
                .map(OppgaveEgenskap::getAndreKriterierType)
                .anyMatch(k -> k.equals(type));
    }
}
