package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fptilbake;

import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;

public interface FptilbakeOppgavetransisjonHåndterer {

    String SYSTEM = Fagsystem.FPTILBAKE.name();

    Oppgavetransisjon kanHåndtere();

    void håndter(FptilbakeOppgavehendelseHåndterer.FptilbakeData data);


    enum Oppgavetransisjon {
        SETT_PÅ_VENT,
        OPPRETT_OPPGAVE,
        LUKK_OPPGAVE,
        OPPDATER_OPPGAVE,
        OPPRETT_BESLUTTEROPPGAVE,
        OPPGAVE_TIL_NY_ENHET,
        RETUR_FRA_BESLUTTER_OPPGAVE
    }

}
