package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;

import java.util.ArrayList;
import java.util.List;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

public class FpsakAksjonspunkt {

    private final List<Aksjonspunkt> aksjonspunkt;
    private final List<AndreKriterierType> kriterier = new ArrayList<>();

    public FpsakAksjonspunkt(List<Aksjonspunkt> aksjonspunkt) {
        this.aksjonspunkt = aksjonspunkt;

        if (tilBeslutter()) kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        if (erRegistrerPapirSøknad()) kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        if (erUtenlandssak()) kriterier.add(AndreKriterierType.UTLANDSSAK);
        if (erVurderFaresignaler()) kriterier.add(AndreKriterierType.VURDER_FARESIGNALER);
    }

    public List<AndreKriterierType> getKriterier() {
        return kriterier;
    }

//    private boolean erSelvstendigEllerFrilans() {
//        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::erSelvstendigEllerFrilanser);
//    }

    private boolean erVurderFaresignaler() {
        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::erVurderFaresignaler);
    }

    private boolean tilBeslutter() {
        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::tilBeslutter);
    }

    private boolean erRegistrerPapirSøknad() {
        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::erRegistrerPapirSøknad);
    }

    private boolean erUtenlandssak() {
        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::erUtenlandssak);
    }
}
