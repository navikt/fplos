package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import no.nav.vedtak.hendelser.behandling.Aksjonspunktstatus;
import no.nav.vedtak.hendelser.behandling.Aksjonspunkttype;
import no.nav.vedtak.hendelser.behandling.los.LosBehandlingDto;

public record Aksjonspunkt(String definisjonKode, Aksjonspunkttype type, Aksjonspunktstatus status, LocalDateTime fristTid) {
    private static final String MANUELT_SATT_PÅ_VENT_KODE = "7001";

    private static final String VENT_TIDLIG = "7008";
    private static final String VENT_KØ = "7011";
    private static final String VENT_MANUELL = "7001";
    private static final String VENT_SØKNAD = "7013";
    private static final Set<String> VENT_KOMPLETT = Set.of("7003", "7030");
    private static final Set<String> VENT_KLAGE = Set.of("7033", "7039", "7040");

    private static final String KONTROLLER_TERMINBEKREFTELSE_KODE = "5001";
    private static final String AUTOMATISK_MARKERING_SOM_UTLAND = "5068";
    private static final String ARBEID_INNTEKT = "5085";
    private static final List<String> VURDER_FORMKRAV_GRUPPE = List.of("5082");
    private static final List<String> RELEVANT_NÆRING = List.of("5039", "5049", "5058", "5046", "5051", "5089", "5082", "5035");

    public LocalDateTime getFristTid() {
        return fristTid;
    }

    public String getDefinisjonKode() {
        return definisjonKode;
    }

    public boolean erPåVent() {
        return Aksjonspunkttype.VENT.equals(type()) && erAktiv();
    }

    public boolean erManueltPåVent() {
        return MANUELT_SATT_PÅ_VENT_KODE.equals(definisjonKode) && erAktiv();
    }

    public boolean erAktiv() {
        return Aksjonspunktstatus.OPPRETTET.equals(status);
    }

    public boolean erAvbrutt() {
        return Aksjonspunktstatus.AVBRUTT.equals(status);
    }

    public boolean erTilBeslutter() {
        return Aksjonspunkttype.BESLUTTER.equals(type()) && erAktiv();
    }

    public boolean erReturnertFraBeslutter() {
        return Aksjonspunkttype.BESLUTTER.equals(type()) && erAvbrutt();
    }

    public boolean erRegistrerPapirSøknad() {
        return Aksjonspunkttype.PAPIRSØKNAD.equals(type()) && erAktiv();
    }

    public boolean skalVurdereInnhentingAvSED() {
        return erAktiv() && AUTOMATISK_MARKERING_SOM_UTLAND.equals(definisjonKode);
    }

    public boolean skalKontrollereTerminbekreftelse() {
        return erAktiv() && KONTROLLER_TERMINBEKREFTELSE_KODE.equals(definisjonKode);
    }

    public boolean skalVurdereArbeidInntekt() {
        return erAktiv() && ARBEID_INNTEKT.equals(definisjonKode);
    }

    public boolean skalVurdereNæring() {
        return erAktiv() && RELEVANT_NÆRING.contains(definisjonKode);
    }

    public boolean erVurderFormkrav() {
        return erAktiv() && VURDER_FORMKRAV_GRUPPE.contains(definisjonKode);
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
