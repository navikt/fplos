package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.hendelse.hendelseoppretter.hendelse.Fagsystem;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public interface FpsakOppgavetransisjonHåndterer {

    String SYSTEM = Fagsystem.FPSAK.name();

    void håndter(BehandlingId behandlingId, LosBehandlingDto behandling);

    Oppgavetransisjon kanHåndtere();


    enum Oppgavetransisjon {
        IKKE_RELEVANT, LUKK_OPPGAVE, SETT_PÅ_VENT, OPPDATER_OPPGAVE, OPPRETT_BESLUTTEROPPGAVE,
        OPPRETT_PAPIRSØKNADOPPGAVE, RETUR_FRA_BESLUTTER_OPPGAVE, GJENÅPNE_OPPGAVE, OPPRETT_OPPGAVE
    }

}
