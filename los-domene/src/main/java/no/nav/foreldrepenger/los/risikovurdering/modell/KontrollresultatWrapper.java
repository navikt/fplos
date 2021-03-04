package no.nav.foreldrepenger.los.risikovurdering.modell;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;

import java.util.Objects;

public class KontrollresultatWrapper {

    private BehandlingId behandlingId;

    private Kontrollresultat kontrollresultatkode;

    public KontrollresultatWrapper(BehandlingId behandlingId, Kontrollresultat kontrollresultatkode) {
        Objects.requireNonNull(behandlingId, "behandlingUuid");
        Objects.requireNonNull(kontrollresultatkode, "kontrollresultatKode");
        this.behandlingId = behandlingId;
        this.kontrollresultatkode = kontrollresultatkode;
    }

    public BehandlingId getBehandlingId() {
        return behandlingId;
    }

    public Kontrollresultat getKontrollresultatkode() {
        return kontrollresultatkode;
    }
}
