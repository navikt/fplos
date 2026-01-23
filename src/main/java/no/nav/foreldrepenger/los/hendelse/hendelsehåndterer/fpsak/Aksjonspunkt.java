package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.time.LocalDateTime;
import java.util.Set;

import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public record Aksjonspunkt(String definisjonKode, Aksjonspunkttype type, Aksjonspunktstatus status, LocalDateTime fristTid) {

    private static final String VENT_TIDLIG = "7008";
    private static final String VENT_KØ = "7011";
    private static final String VENT_MANUELL = "7001";
    private static final String VENT_SØKNAD = "7013";
    private static final Set<String> VENT_KOMPLETT = Set.of("7003", "7030");
    private static final Set<String> VENT_KLAGE = Set.of("7033", "7039", "7040");

    public boolean erPåVent() {
        return Aksjonspunkttype.VENT.equals(type()) && erAktiv();
    }

    public boolean erAktiv() {
        return Aksjonspunktstatus.OPPRETTET.equals(status);
    }

    public boolean erTilBeslutter() {
        return Aksjonspunkttype.BESLUTTER.equals(type()) && erAktiv();
    }

    public boolean erRegistrerPapirSøknad() {
        return Aksjonspunkttype.PAPIRSØKNAD.equals(type()) && erAktiv();
    }

    public boolean erVentTidlig() {
        return erAktiv() && VENT_TIDLIG.equals(definisjonKode);
    }

    public boolean erVentKø() {
        return erAktiv() && VENT_KØ.equals(definisjonKode);
    }

    public boolean erVentKomplett() {
        return erAktiv() && VENT_KOMPLETT.contains(definisjonKode);
    }

    public boolean erVentKlage() {
        return erAktiv() && VENT_KLAGE.contains(definisjonKode);
    }

    public boolean erVentManuell() {
        return erAktiv() && VENT_MANUELL.contains(definisjonKode);
    }

    public boolean erVentSøknad() {
        return erAktiv() && VENT_SØKNAD.contains(definisjonKode);
    }

    public static Aksjonspunkt aksjonspunktFra(LosBehandlingDto.LosAksjonspunktDto aksjonspunktDto) {
        return new Aksjonspunkt(aksjonspunktDto.definisjon(), aksjonspunktDto.type(), aksjonspunktDto.status(), aksjonspunktDto.fristTid());
    }


}
