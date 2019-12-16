package no.nav.fplos.kafkatjenester;

import no.nav.foreldrepenger.loslager.oppgave.AndreKriterierType;
import no.nav.fplos.foreldrepengerbehandling.Aksjonspunkt;

import java.util.ArrayList;
import java.util.List;

import static no.nav.fplos.kafkatjenester.util.StreamUtil.safeStream;

public class FpsakAksjonspunkt {

    private final List<Aksjonspunkt> aksjonspunkt;
    private final List<AndreKriterierType> kriterier;

    public FpsakAksjonspunkt(List<Aksjonspunkt> aksjonspunkt) {
        this.aksjonspunkt = aksjonspunkt;
        this.kriterier = kriterieListeFra(aksjonspunkt);
    }

    public List<AndreKriterierType> getKriterier() {
        return kriterier;
    }

    private List<AndreKriterierType> kriterieListeFra(List<Aksjonspunkt> aksjonspunkt) {
        List<AndreKriterierType> kriterier = new ArrayList<>();

        if (tilBeslutter()) kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        if (erRegistrerPapirSøknad()) kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        if (erUtenlandssak()) kriterier.add(AndreKriterierType.UTLANDSSAK);
        //if (erSelvstendigEllerFrilans()) kriterier.add(AndreKriterierType.SELVSTENDIG_FRILANSER);

        return kriterier;
    }

    public List<Aksjonspunkt> getAksjonspunkt() {
        return aksjonspunkt;
    }

//    private boolean erSelvstendigEllerFrilans() {
//        return safeStream(aksjonspunkt).anyMatch(Aksjonspunkt::erSelvstendigEllerFrilanser);
//    }

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
