package no.nav.foreldrepenger.los.hendelse.hendelsehåndterer.fpsak;

import static no.nav.foreldrepenger.los.felles.util.StreamUtil.safeStream;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

import no.nav.foreldrepenger.los.klient.fpsak.Aksjonspunkt;
import no.nav.foreldrepenger.los.oppgave.AndreKriterierType;
import no.nav.vedtak.hendelser.behandling.los.LosFagsakEgenskaperDto;

public class FpsakAksjonspunktWrapper {

    private FpsakAksjonspunktWrapper() {
    }

    public static List<AndreKriterierType> getKriterier(List<Aksjonspunkt> aksjonspunkt, LosFagsakEgenskaperDto sakDto) {
        List<AndreKriterierType> kriterier = new ArrayList<>();
        if (finn(aksjonspunkt, Aksjonspunkt::erTilBeslutter)) {
            kriterier.add(AndreKriterierType.TIL_BESLUTTER);
        }
        if (finn(aksjonspunkt, Aksjonspunkt::erRegistrerPapirSøknad)) {
            kriterier.add(AndreKriterierType.PAPIRSØKNAD);
        }
        if (erUtenlandssak(aksjonspunkt, sakDto)) {
            kriterier.add(AndreKriterierType.UTLANDSSAK);
        }
        if (skalVurdereEøs(aksjonspunkt, sakDto)) {
            kriterier.add(AndreKriterierType.VURDER_EØS_OPPTJENING);
        }
        if (finn(aksjonspunkt, Aksjonspunkt::erVurderFormkrav)) {
            kriterier.add(AndreKriterierType.VURDER_FORMKRAV);
        }
        return kriterier;
    }

    private static boolean skalVurdereEøs(List<Aksjonspunkt> aksjonspunkt, LosFagsakEgenskaperDto dto) {
        var skalVurdereInnhentingAvSED = finn(aksjonspunkt, Aksjonspunkt::skalVurdereInnhentingAvSED);
        var egenskapSkalInnhente = Optional.ofNullable(dto.skalInnhenteSED()).isPresent();
        var ikkeNasjonal = Optional.ofNullable(dto.utlandMarkering()).filter(m -> !LosFagsakEgenskaperDto.UtlandMarkering.NASJONAL.equals(m)).isPresent();
        return skalVurdereInnhentingAvSED && ikkeNasjonal && egenskapSkalInnhente;
    }

    private static boolean erUtenlandssak(List<Aksjonspunkt> aksjonspunkt, LosFagsakEgenskaperDto sakDto) {
        var skalVurdereInnhentingAvSED = finn(aksjonspunkt, Aksjonspunkt::skalVurdereInnhentingAvSED);
        if (Optional.ofNullable(sakDto).map(LosFagsakEgenskaperDto::utlandMarkering).isPresent()) {
            return !LosFagsakEgenskaperDto.UtlandMarkering.NASJONAL.equals(sakDto.utlandMarkering()) ||
                (Objects.equals(Boolean.TRUE, sakDto.skalInnhenteSED()) && skalVurdereInnhentingAvSED);
        }
        var overstyrtTilNasjonalsak = finn(aksjonspunkt, Aksjonspunkt::erManueltOverstyrtTilNasjonalSak);
        var overstyrtTilUtenlandssak = finn(aksjonspunkt, Aksjonspunkt::erManueltOverstyrtTilUtenlandssak);

        if (overstyrtTilNasjonalsak) {
            return false;
        } else if (overstyrtTilUtenlandssak) {
            return true;
        } else {
            return skalVurdereInnhentingAvSED;
        }
    }

    private static boolean finn(List<Aksjonspunkt> aksjonspunkt, Predicate<Aksjonspunkt> predicate) {
        return safeStream(aksjonspunkt).anyMatch(predicate);
    }

}
