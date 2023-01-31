package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;

public class FpsakAksjonspunktWrapper {

    private final List<Aksjonspunkt> aksjonspunkt;
    private final List<AndreKriterierType> kriterier = new ArrayList<>();

    public FpsakAksjonspunktWrapper(List<Aksjonspunkt> aksjonspunkt) {
        this.aksjonspunkt = aksjonspunkt;
        if (finn(Aksjonspunkt::erTilBeslutter)) {
            kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (finn(Aksjonspunkt::erRegistrerPapirSøknad)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (erUtenlandssak()) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (finn(Aksjonspunkt::erVurderFormkrav)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }
    }

    public List<AndreKriterierType> getKriterier() {
        return kriterier;
    }

    private boolean erUtenlandssak() {
        var overstyrtTilNasjonalsak = finn(Aksjonspunkt::erManueltOverstyrtTilNasjonalSak);
        var overstyrtTilUtenlandssak = finn(Aksjonspunkt::erManueltOverstyrtTilUtenlandssak);
        var skalVurdereInnhentingAvSED = finn(Aksjonspunkt::skalVurdereInnhentingAvSED);
        if (overstyrtTilNasjonalsak) {
            return false;
        } else if (overstyrtTilUtenlandssak) {
            return true;
        } else return skalVurdereInnhentingAvSED;
    }

    private boolean finn(Predicate<Aksjonspunkt> predicate) {
        return safeStream(aksjonspunkt).anyMatch(predicate);
    }

}
