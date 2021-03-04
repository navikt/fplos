package no.nav.foreldrepenger.los.risikovurdering.json;


import no.nav.foreldrepenger.los.domene.typer.BehandlingId;
import no.nav.foreldrepenger.los.risikovurdering.modell.Kontrollresultat;
import no.nav.foreldrepenger.los.risikovurdering.modell.KontrollresultatWrapper;
import no.nav.vedtak.feil.Feil;
import no.nav.vedtak.feil.FeilFactory;
import no.nav.vedtak.feil.deklarasjon.DeklarerteFeil;
import no.nav.vedtak.feil.deklarasjon.TekniskFeil;
import no.nav.vedtak.kontroll.v1.KontrollResultatV1;

import static no.nav.vedtak.feil.LogLevel.WARN;

public class KontrollresultatMapper {

    public KontrollresultatMapper() {
    }

    public static KontrollresultatWrapper fraKontrakt(KontrollResultatV1 kontraktResultat) {
        if (kontraktResultat.getKontrollResultatkode() == null || kontraktResultat.getKontrollResultatkode().getKode() == null) {
            throw Feilene.FACTORY.manglerKontrollresultatkode().toException();
        }
        String kode = kontraktResultat.getKontrollResultatkode().getKode();
        Kontrollresultat kontrollresultat = finnKontrollresultat(kode);
        BehandlingId behandlingId = new BehandlingId(kontraktResultat.getBehandlingUuid());
        return new KontrollresultatWrapper(behandlingId, kontrollresultat);
    }

    private static Kontrollresultat finnKontrollresultat(String kode) {
        if (kode == null) {
            return null;
        }
        Kontrollresultat kontrollresultat = Kontrollresultat.fraKode(kode);
        if (kontrollresultat == null || Kontrollresultat.UDEFINERT.equals(kontrollresultat)) {
            throw Feilene.FACTORY.udefinertKontrollresultat().toException();
        }
        return kontrollresultat;
    }

    interface Feilene extends DeklarerteFeil {
        Feilene FACTORY = FeilFactory.create(Feilene.class);

        @TekniskFeil(feilkode = "FP-42517", feilmelding = "Mangler kontrollresultatkode på kontrollresultat", logLevel = WARN)
        Feil manglerKontrollresultatkode();

        @TekniskFeil(feilkode = "FP-42518", feilmelding = "Udefinert kontrollresultat", logLevel = WARN)
        Feil udefinertKontrollresultat();
    }

}
