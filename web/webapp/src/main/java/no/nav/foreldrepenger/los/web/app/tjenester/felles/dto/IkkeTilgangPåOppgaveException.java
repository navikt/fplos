package no.nav.foreldrepenger.los.web.app.tjenester.felles.dto;

public class IkkeTilgangPåOppgaveException extends RuntimeException {

    public IkkeTilgangPåOppgaveException(long oppgaveId, Throwable cause) {
        super("Ikke tilgang på oppgave " + oppgaveId, cause);
    }
}
